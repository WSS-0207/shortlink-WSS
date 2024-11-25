package org.myproject.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.myproject.shortlink.project.common.convention.exception.ClientException;
import org.myproject.shortlink.project.common.enums.VailDateTypeEnum;
import org.myproject.shortlink.project.dao.entity.ShortLinkDO;
import org.myproject.shortlink.project.dao.entity.ShortLinkGotoDO;
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
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.myproject.shortlink.project.common.constant.RedisKeyConstant.*;
import static org.myproject.shortlink.project.util.LinkUtil.getLinkCacheValidTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {

    private final RBloomFilter<String> shortLinkCachePenetrationBloomFilter;
    private final ShortLinkGotoMapper shortLinkGotoMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;


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
                fullUrl,
                requestParam.getOriginUrl(),
                getLinkCacheValidTime(requestParam.getValidDate()),TimeUnit.MINUTES);
        shortLinkCachePenetrationBloomFilter.add(fullUrl);

        return ShortLinkCreateRespDTO.builder()
                .fullShortUrl(build.getFullShortUrl())
                .originUrl(requestParam.getOriginUrl())
                .gid(requestParam.getGid())
                .build();
    }

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

    @SneakyThrows
    @Override
    public void restoreUrl(String shortLink, ServletRequest request, ServletResponse response) throws IOException {
        String serverName = request.getServerName();
        String fullShortLink = serverName + "/" + shortLink;
        String originalLink = stringRedisTemplate.opsForValue().get(String.format(GOTO_SHORT_LINK_KEY, fullShortLink));
        if (StrUtil.isNotBlank(originalLink)) {
            ((HttpServletResponse) response).sendRedirect(originalLink);
            return;
        }
        boolean contains = shortLinkCachePenetrationBloomFilter.contains(fullShortLink);
        if (!contains) {
            return;
        }
        originalLink = stringRedisTemplate.opsForValue().get(String.format(GOTO_IS_NULL_SHORT_LINK_KEY, fullShortLink));
        if (StrUtil.isNotBlank(originalLink)) {
            return;
        }
        RLock lock = redissonClient.getLock(String.format(LOCK_GOTO_SHORT_LINK_KEY, fullShortLink));
        lock.lock();
        try {
            originalLink = stringRedisTemplate.opsForValue().get(String.format(GOTO_SHORT_LINK_KEY, fullShortLink));
            if (StrUtil.isNotBlank(originalLink)) {
                ((HttpServletResponse) response).sendRedirect(originalLink);
                return;
            }
            LambdaQueryWrapper<ShortLinkGotoDO> eq = Wrappers.lambdaQuery(ShortLinkGotoDO.class)
                    .eq(ShortLinkGotoDO::getFullShortLink, fullShortLink);
            ShortLinkGotoDO shortLinkGotoDO = shortLinkGotoMapper.selectOne(eq);
            if (shortLinkGotoDO == null) {
                stringRedisTemplate.opsForValue().set(String.format(GOTO_IS_NULL_SHORT_LINK_KEY, fullShortLink), "-", 30, TimeUnit.MINUTES);
                return;
            }
            LambdaQueryWrapper<ShortLinkDO> shortLinkDOQuery = Wrappers.lambdaQuery(ShortLinkDO.class)
                    .eq(ShortLinkDO::getGid, shortLinkGotoDO.getGid())
                    .eq(ShortLinkDO::getFullShortUrl, fullShortLink)
                    .eq(ShortLinkDO::getEnableStatus, 0)
                    .eq(ShortLinkDO::getDelFlag, 0);
            ShortLinkDO shortLinkDO = baseMapper.selectOne(shortLinkDOQuery);
            if (shortLinkDO != null) {
                stringRedisTemplate.opsForValue().set(String.format(GOTO_SHORT_LINK_KEY, fullShortLink), shortLinkDO.getOriginUrl());
                ((HttpServletResponse) response).sendRedirect(shortLinkDO.getOriginUrl());
            }
        }finally {
            lock.unlock();
        }
    }

    public String generateShortUrl(ShortLinkCreateReqDTO requestParam) {
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
