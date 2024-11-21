package org.myproject.shortlink.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.myproject.shortlink.project.dao.entity.ShortLinkDO;
import org.myproject.shortlink.project.dao.mapper.ShortLinkMapper;
import org.myproject.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import org.myproject.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import org.myproject.shortlink.project.service.ShortLinkService;
import org.myproject.shortlink.project.util.HashUtil;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {
    @Override
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam) {
        String shortUrl = generateShortUrl(requestParam);
        ShortLinkDO shortLinkDO = new ShortLinkDO();
        shortLinkDO.setShortUrl(shortUrl);
        shortLinkDO.setEnableStatus(0);
        shortLinkDO.setFullShortUrl(requestParam.getDomain()+"/"+shortUrl);
        baseMapper.insert(shortLinkDO);
        return ShortLinkCreateRespDTO.builder()
                .fullShortUrl(shortLinkDO.getFullShortUrl())
                .originUrl(requestParam.getOriginUrl())
                .gid(requestParam.getGid())
                .build();
    }

    public String generateShortUrl(ShortLinkCreateReqDTO requestParam) {
        String originUrl = requestParam.getOriginUrl();
        return HashUtil.hashToBase62(originUrl);
    }
}
