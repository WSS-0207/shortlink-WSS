package org.myproject.shortlink.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.myproject.shortlink.admin.common.convention.result.Result;
import org.myproject.shortlink.admin.remote.dto.req.ShortLinkRecycleBinPageReqDTO;
import org.myproject.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;

public interface RecycleBinService {
    Result<IPage<ShortLinkPageRespDTO>> pageRecycleShortLink(ShortLinkRecycleBinPageReqDTO requestParam);
}
