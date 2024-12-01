package org.myproject.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.Week;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.myproject.shortlink.project.common.convention.exception.ClientException;
import org.myproject.shortlink.project.common.enums.VailDateTypeEnum;
import org.myproject.shortlink.project.dao.entity.LinkAccessStatsDO;
import org.myproject.shortlink.project.dao.entity.LinkLocaleStatsDO;
import org.myproject.shortlink.project.dao.entity.ShortLinkDO;
import org.myproject.shortlink.project.dao.entity.ShortLinkGotoDO;
import org.myproject.shortlink.project.dao.mapper.LinkAccessStatsMapper;
import org.myproject.shortlink.project.dao.mapper.LinkLocaleStatsMapper;
import org.myproject.shortlink.project.dao.mapper.ShortLinkGotoMapper;
import org.myproject.shortlink.project.dao.mapper.ShortLinkMapper;
import org.myproject.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import org.myproject.shortlink.project.dto.req.ShortLinkPageReqDTO;
import org.myproject.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import org.myproject.shortlink.project.dto.resp.ShortLinkCountQueryRespDTO;
import org.myproject.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import org.myproject.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import org.myproject.shortlink.project.service.ShortLinkService;
import org.myproject.shortlink.project.util.HashUtil;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.myproject.shortlink.project.common.constant.RedisKeyConstant.*;
import static org.myproject.shortlink.project.common.constant.ShortLinkConstant.AMAP_REMOTE_URL;
import static org.myproject.shortlink.project.util.LinkUtil.getLinkCacheValidTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {

    private final RBloomFilter<String> shortLinkCachePenetrationBloomFilter;
    private final ShortLinkGotoMapper shortLinkGotoMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;
    private final LinkAccessStatsMapper linkAccessStatsMapper;
    private final LinkLocaleStatsMapper linkLocaleStatsMapper;

    @Value("${short-link.stats.locale.amap-key}")
    private String statsLocaleAmapKey;


    @Override
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam) {
        String shortUrl = generateShortUrl(requestParam);
        String fullUrl = new StringBuilder()
                .append(requestParam.getDomain())
                .append("/")
                .append(shortUrl).toString();
        ShortLinkDO build = ShortLinkDO.builder()
                .fullShortUrl(fullUrl)
                .originUrl(requestParam.getOriginUrl())
                .gid(requestParam.getGid())
                .shortUrl(shortUrl)
                .describe(requestParam.getDescribe())
                .domain(requestParam.getDomain())
                .validDate(requestParam.getValidDate())
                .createType(requestParam.getCreateType())
                .validDateType(requestParam.getValidDateType())
                .favicon(getFavicon(requestParam.getOriginUrl()))
                .enableStatus(0)
                .build();

        ShortLinkGotoDO shortLinkGotoDO = ShortLinkGotoDO.builder()
                .gid(requestParam.getGid())
                .fullShortLink(fullUrl)
                .build();
        try {
            baseMapper.insert(build);
            shortLinkGotoMapper.insert(shortLinkGotoDO);
        } catch (DuplicateKeyException e) {
            LambdaQueryWrapper<ShortLinkDO> eq = Wrappers.lambdaQuery(ShortLinkDO.class)
                    .eq(ShortLinkDO::getShortUrl, shortUrl);
            ShortLinkDO shortLinkDO = baseMapper.selectOne(eq);
            if (shortLinkDO != null) {
                log.error("短连接{}重复，请更换短连接", fullUrl);
                throw new ClientException("短连接重复，请更换短连接");
            }
        }
        //缓存预热
        stringRedisTemplate.opsForValue().set(
                String.format(GOTO_SHORT_LINK_KEY, fullUrl),
                requestParam.getOriginUrl(),
                getLinkCacheValidTime(requestParam.getValidDate()),TimeUnit.MINUTES);
        shortLinkCachePenetrationBloomFilter.add(fullUrl);

        return ShortLinkCreateRespDTO.builder()
                .fullShortUrl(build.getFullShortUrl())
                .originUrl(requestParam.getOriginUrl())
                .gid(requestParam.getGid())
                .build();
    }

    @SneakyThrows
    private String getFavicon(String url){
        URL targetUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) targetUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        int responseCode = connection.getResponseCode();
        if (responseCode==HttpURLConnection.HTTP_OK){
            Document document = Jsoup.connect(url).get();
            Element first = document.select("Link[rel~=(?i)^(shortcut )?icon]").first();
            if (first != null){
                return first.attr("abs:href");
            }
        }
        return null;
    }

    /*
    * 分页查询短连接
    * */
    @Override
    public IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkDO> eq = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getEnableStatus, 0)
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .orderByDesc(ShortLinkDO::getCreateTime);
        IPage<ShortLinkDO> resultPage = baseMapper.selectPage(requestParam, eq);

        return resultPage.convert(item -> BeanUtil.toBean(item, ShortLinkPageRespDTO.class));
    }

    @Override
    public List<ShortLinkCountQueryRespDTO> countShortLink(List<String> requestParam) {
        QueryWrapper<ShortLinkDO> shortLinkDOQueryWrapper = Wrappers.query(new ShortLinkDO()).select("gid,count(*) as shortLinkCount")
                .in("gid", requestParam)
                .eq("enable_status", 0)
                .groupBy("gid");
        List<Map<String, Object>> maps = baseMapper.selectMaps(shortLinkDOQueryWrapper);
        return BeanUtil.copyToList(maps, ShortLinkCountQueryRespDTO.class);
    }

    @Override
    public void update(ShortLinkUpdateReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkDO> eq = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(ShortLinkDO::getEnableStatus, 0)
                .eq(ShortLinkDO::getDelFlag, 0);
        ShortLinkDO hasShortLinkDO = baseMapper.selectOne(eq);
        if(hasShortLinkDO==null){
            throw new ClientException("短连接不存在");
        }
        ShortLinkDO build = ShortLinkDO.builder()
                .gid(requestParam.getGid())
                .domain(hasShortLinkDO.getDomain())
                .shortUrl(hasShortLinkDO.getShortUrl())
                .clickNum(hasShortLinkDO.getClickNum())
                .favicon(hasShortLinkDO.getFavicon())
                .createType(hasShortLinkDO.getCreateType())
                .originUrl(requestParam.getOriginUrl())
                .describe(requestParam.getDescribe())
                .validDate(requestParam.getValidDate())
                .validDateType(requestParam.getValidDateType())
                .build();
        if (Objects.equals(requestParam.getGid(), hasShortLinkDO.getGid())){
            LambdaUpdateWrapper<ShortLinkDO> set = Wrappers.lambdaUpdate(ShortLinkDO.class)
                    .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                    .eq(ShortLinkDO::getGid, requestParam.getGid())
                    .eq(ShortLinkDO::getEnableStatus, 0)
                    .eq(ShortLinkDO::getDelFlag, 0)
                    .set(Objects.equals(requestParam.getValidDateType(), VailDateTypeEnum.PERMANERNT.getType())
                            , ShortLinkDO::getValidDate, null);
            baseMapper.update(build, set);
        }else {
            LambdaUpdateWrapper<ShortLinkDO> set = Wrappers.lambdaUpdate(ShortLinkDO.class)
                    .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                    .eq(ShortLinkDO::getGid, requestParam.getGid())
                    .eq(ShortLinkDO::getEnableStatus, 0)
                    .eq(ShortLinkDO::getDelFlag, 0);
            baseMapper.delete(set);
            baseMapper.insert(build);
        }
    }

    /*
    * 短连接跳转
    * */
    @SneakyThrows
    @Override
    public void restoreUrl(String shortLink, ServletRequest request, ServletResponse response) throws IOException {
        String serverName = request.getServerName();
        String fullShortLink = serverName + "/" + shortLink;
        // 查询redis缓存
        String originalLink = stringRedisTemplate.opsForValue().get(String.format(GOTO_SHORT_LINK_KEY, fullShortLink));
        if (StrUtil.isNotBlank(originalLink)) {
            shortLinkStats(fullShortLink, null, request, response);
            ((HttpServletResponse) response).sendRedirect(originalLink);
            return;
        }
        //查询布隆过滤器
        boolean contains = shortLinkCachePenetrationBloomFilter.contains(fullShortLink);
        if (!contains) {
            ((HttpServletResponse) response).sendRedirect("/page/notfound");
            return;
        }
        //查询redis中的空值
        originalLink = stringRedisTemplate.opsForValue().get(String.format(GOTO_IS_NULL_SHORT_LINK_KEY, fullShortLink));
        if (StrUtil.isNotBlank(originalLink)) {
            ((HttpServletResponse) response).sendRedirect("/page/notfound");
            return;
        }
        //加锁
        RLock lock = redissonClient.getLock(String.format(LOCK_GOTO_SHORT_LINK_KEY, fullShortLink));
        lock.lock();
        try {
            //双重锁，查询redis缓存
            originalLink = stringRedisTemplate.opsForValue().get(String.format(GOTO_SHORT_LINK_KEY, fullShortLink));
            if (StrUtil.isNotBlank(originalLink)) {
                shortLinkStats(fullShortLink, null, request, response);
                ((HttpServletResponse) response).sendRedirect(originalLink);
                return;
            }
            //缓存穿透，查询路由表
            LambdaQueryWrapper<ShortLinkGotoDO> eq = Wrappers.lambdaQuery(ShortLinkGotoDO.class)
                    .eq(ShortLinkGotoDO::getFullShortLink, fullShortLink);
            ShortLinkGotoDO shortLinkGotoDO = shortLinkGotoMapper.selectOne(eq);
            if (shortLinkGotoDO == null) {
                stringRedisTemplate.opsForValue().set(String.format(GOTO_IS_NULL_SHORT_LINK_KEY, fullShortLink), "-", 30, TimeUnit.MINUTES);
                ((HttpServletResponse) response).sendRedirect("/page/notfound");
                return;
            }
            //查询数据库
            LambdaQueryWrapper<ShortLinkDO> shortLinkDOQuery = Wrappers.lambdaQuery(ShortLinkDO.class)
                    .eq(ShortLinkDO::getGid, shortLinkGotoDO.getGid())
                    .eq(ShortLinkDO::getFullShortUrl, fullShortLink)
                    .eq(ShortLinkDO::getEnableStatus, 0)
                    .eq(ShortLinkDO::getDelFlag, 0);
            ShortLinkDO shortLinkDO = baseMapper.selectOne(shortLinkDOQuery);
            //查询数据库，短连接已失效
            if (shortLinkDO == null || (shortLinkDO.getValidDate()!=null && shortLinkDO.getValidDate().before(new Date()))) {
                stringRedisTemplate.opsForValue().set(String.format(GOTO_IS_NULL_SHORT_LINK_KEY, fullShortLink), "-", 30, TimeUnit.MINUTES);
                ((HttpServletResponse) response).sendRedirect("/page/notfound");
                return;
            }
            //缓存预热
            stringRedisTemplate.opsForValue().set(
                    String.format(GOTO_SHORT_LINK_KEY, fullShortLink),
                    shortLinkDO.getOriginUrl(),
                    getLinkCacheValidTime(shortLinkDO.getValidDate()),TimeUnit.MINUTES);
            shortLinkStats(fullShortLink, shortLinkGotoDO.getGid(), request, response);
            ((HttpServletResponse) response).sendRedirect(shortLinkDO.getOriginUrl());
        }finally {
            lock.unlock();
        }
    }

    private void shortLinkStats(String fullShortUrl, String gid, ServletRequest request, ServletResponse response){
        if (StrUtil.isBlank(gid)){
            LambdaQueryWrapper<ShortLinkGotoDO> eq = Wrappers.lambdaQuery(ShortLinkGotoDO.class).eq(ShortLinkGotoDO::getFullShortLink, fullShortUrl);
            ShortLinkGotoDO shortLinkGotoDO = shortLinkGotoMapper.selectOne(eq);
            gid = shortLinkGotoDO.getGid();
        }
        AtomicBoolean uvFirstFlag = new AtomicBoolean();
        Cookie[] cookies = ((HttpServletRequest) request).getCookies();
        try {
            Runnable runnable = ()->{
                String uv = UUID.fastUUID().toString();
                Cookie uvCookie = new Cookie("uv", uv);
                uvCookie.setMaxAge(60*60*24*30);
                uvCookie.setPath(StrUtil.sub(fullShortUrl,fullShortUrl.indexOf("/"),fullShortUrl.length()));
                ((HttpServletResponse) response).addCookie(uvCookie);
                uvFirstFlag.set(Boolean.TRUE);
                stringRedisTemplate.opsForSet().add("short-link:stats:uv:" + fullShortUrl, uv);
            };
            //uv监控
            if(ArrayUtil.isNotEmpty(cookies)){
                Arrays.stream(cookies)
                        .filter(cookie -> Objects.equals("uv", cookie.getName()))
                        .findFirst()
                        .map(Cookie::getValue)
                        .ifPresentOrElse(
                                value->{
                                    Long add = stringRedisTemplate.opsForSet()
                                            .add("short-link:stats:uv:" + fullShortUrl, value);
                                    uvFirstFlag.set(add!=null&& add>0L);
                                },
                                runnable
                        );
            }else{
                runnable.run();
            }
            //ip监控
            String remoteAddr = request.getRemoteAddr();
            Long uipAdd = stringRedisTemplate.opsForSet()
                    .add("short-link:stats:uip:" + fullShortUrl, remoteAddr);
            boolean uipFirstFlag = uipAdd!=null&& uipAdd>0L;
            int hour = DateUtil.hour(new Date(), true);
            Week week = DateUtil.dayOfWeekEnum(new Date());
            int weekday = week.getValue();
            LinkAccessStatsDO linkAccessStatsDO = LinkAccessStatsDO.builder()
                    .fullShortUrl(fullShortUrl)
                    .gid(gid)
                    .date(new Date())
                    .pv(1)
                    .uv(uvFirstFlag.get()?1:0)
                    .uip(uipFirstFlag?1:0)
                    .hour(hour)
                    .weekday(weekday)
                    .build();
            linkAccessStatsMapper.shortLinkStats(linkAccessStatsDO);
            //地区监控
            HashMap<String,Object> map = new HashMap<>();
            map.put("key",statsLocaleAmapKey);
            map.put("ip", remoteAddr);
            //高德获取地区接口地址
            String localeResult = HttpUtil.get(AMAP_REMOTE_URL, map);
            JSONObject localeObject = JSON.parseObject(localeResult);
            String infocode = localeObject.getString("infocode");
            if(StrUtil.isNotBlank(infocode)&&StrUtil.equals(infocode, "10000")){
                String province = localeObject.getString("province");
                boolean unknownFlag = StrUtil.equals(province,"[]");
                LinkLocaleStatsDO linkLocaleStatsDO = LinkLocaleStatsDO.builder()
                        .fullShortUrl(fullShortUrl)
                        .gid(gid)
                        .date(new Date())
                        .cnt(1)
                        .province(unknownFlag?"unknown" : province)
                        .city(unknownFlag?"unknown" : localeObject.getString("city"))
                        .adcode(unknownFlag?"unknown" : localeObject.getString("adcode"))
                        .country(unknownFlag?"China" : localeObject.getString("country"))
                        .build();

                linkLocaleStatsMapper.shortLinkLocaleStats(linkLocaleStatsDO);
            }
        }catch (Throwable ex){
            log.error("短连接访问统计失败",ex);
        }
    }

    private String generateShortUrl(ShortLinkCreateReqDTO requestParam) {
        int cnt = 0;
        String originUrl = requestParam.getOriginUrl();
        String shortUrl;
        while (true) {
            if (cnt > 10) {
                throw new ClientException("短连接生成频繁，请稍后再试");
            }
            shortUrl = HashUtil.hashToBase62(originUrl + System.currentTimeMillis());
            if (!shortLinkCachePenetrationBloomFilter.contains(requestParam.getDomain()+"/"+shortUrl)) {
                break;
            }
            cnt++;
        }
        return HashUtil.hashToBase62(originUrl);
    }
}
