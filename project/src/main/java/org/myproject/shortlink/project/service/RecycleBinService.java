package org.myproject.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.myproject.shortlink.project.dao.entity.ShortLinkDO;
import org.myproject.shortlink.project.dto.req.RecycleBinRecoverReqDTO;
import org.myproject.shortlink.project.dto.req.RecycleBinRemoveReqDTO;
import org.myproject.shortlink.project.dto.req.RecycleBinReqDTO;
import org.myproject.shortlink.project.dto.req.ShortLinkRecycleBinPageReqDTO;
import org.myproject.shortlink.project.dto.resp.ShortLinkPageRespDTO;

public interface RecycleBinService extends IService<ShortLinkDO> {

    /*
    * 将短连接移至回收站
    * */
    void recycleShortLink(RecycleBinReqDTO requestParam);

    /*
    * 分页查询回收站短连接
    * */
    IPage<ShortLinkPageRespDTO> pageRecycleShortLink(ShortLinkRecycleBinPageReqDTO requestParam);

    /*
    * 恢复短连接
    * */
    void recoverShortLink(RecycleBinRecoverReqDTO requestParam);

    /*
    * 短连接移除
    * */
    void removeShortLink(RecycleBinRemoveReqDTO requestParam);
}
