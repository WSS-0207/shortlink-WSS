package org.myproject.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myproject.shortlink.project.common.convention.exception.ClientException;
import org.myproject.shortlink.project.dao.entity.ShortLinkDO;
import org.myproject.shortlink.project.dao.mapper.ShortLinkMapper;
import org.myproject.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import org.myproject.shortlink.project.dto.req.ShortLinkPageReqDTO;
import org.myproject.shortlink.project.dto.resp.ShortLinkCountQueryRespDTO;
import org.myproject.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import org.myproject.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import org.myproject.shortlink.project.service.ShortLinkService;
import org.myproject.shortlink.project.util.HashUtil;
import org.redisson.api.RBloomFilter;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {

    private final RBloomFilter<String> shortLinkCachePenetrationBloomFilter;
    private final ShortLinkMapper shortLinkMapper;

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
        try {
            baseMapper.insert(build);
        } catch (DuplicateKeyException e) {
            LambdaQueryWrapper<ShortLinkDO> eq = Wrappers.lambdaQuery(ShortLinkDO.class)
                    .eq(ShortLinkDO::getShortUrl, shortUrl);
            ShortLinkDO shortLinkDO = baseMapper.selectOne(eq);
            if (shortLinkDO != null) {
                log.error("短连接{}重复，请更换短连接", fullUrl);
                throw new ClientException("短连接重复，请更换短连接");
            }
        }
        shortLinkCachePenetrationBloomFilter.add(shortUrl);

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

    public String generateShortUrl(ShortLinkCreateReqDTO requestParam) {
        int cnt = 0;
        String originUrl = requestParam.getOriginUrl();
        String shortUrl;
        while (true) {
            if (cnt > 10) {
                throw new ClientException("短连接生成频繁，请稍后再试");
            }
            shortUrl = HashUtil.hashToBase62(originUrl + System.currentTimeMillis());
            if (!shortLinkCachePenetrationBloomFilter.contains(shortUrl)) {
                break;
            }
            cnt++;
        }
        return HashUtil.hashToBase62(originUrl);
    }
}
