package org.myproject.shortlink.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.myproject.shortlink.admin.common.convention.result.Result;
import org.myproject.shortlink.admin.common.convention.result.Results;
import org.myproject.shortlink.admin.dto.req.RecycleBinReqDTO;
import org.myproject.shortlink.admin.remote.ShortLinkService;
import org.myproject.shortlink.admin.remote.dto.req.ShortLinkRecycleBinPageReqDTO;
import org.myproject.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import org.myproject.shortlink.admin.service.RecycleBinService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RecycleBinController {
    private final RecycleBinService recycleBinService;
    ShortLinkService shortLinkService = new ShortLinkService() {
    };

    /*
    * 短连接移至回收站
    * */
    @PostMapping("/api/short-link/admin/v1/recycle-bin/save")
    public Result<Void> recycleShortLink(@RequestBody RecycleBinReqDTO requestParam) {
        shortLinkService.recycleShortLink(requestParam);
        return Results.success();
    }

    /*
     * 回收站分页查询短连接
     * */
    @GetMapping("/api/short-link/admin/v1/recycle-bin/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageRecycleShortLink(ShortLinkRecycleBinPageReqDTO requestParam){
        return recycleBinService.pageRecycleShortLink(requestParam);
    }
}
