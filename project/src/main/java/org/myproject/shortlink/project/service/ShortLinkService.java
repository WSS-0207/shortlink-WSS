package org.myproject.shortlink.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.myproject.shortlink.project.dao.entity.ShortLinkDO;
import org.myproject.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import org.myproject.shortlink.project.dto.resp.ShortLinkCreateRespDTO;

public interface ShortLinkService extends IService<ShortLinkDO> {
    /*
    * 创建短连接
    * */
    ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam);
}
