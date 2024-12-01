package org.myproject.shortlink.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.myproject.shortlink.project.common.convention.result.Result;
import org.myproject.shortlink.project.common.convention.result.Results;
import org.myproject.shortlink.project.dto.req.RecycleBinRecoverReqDTO;
import org.myproject.shortlink.project.dto.req.RecycleBinReqDTO;
import org.myproject.shortlink.project.dto.req.ShortLinkRecycleBinPageReqDTO;
import org.myproject.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import org.myproject.shortlink.project.service.RecycleBinService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RecycleBinController {

    private final RecycleBinService recycleBinService;

    /*
    * 短连接移至回收站
    * */
    @PostMapping("/api/short-link/v1/recycle-bin/save")
    public Result<Void> recycleShortLink(@RequestBody RecycleBinReqDTO requestParam) {
        recycleBinService.recycleShortLink(requestParam);
        return Results.success();
    }

    /*
    * 短连接分页查询
    * */
    @GetMapping("/api/short-link/v1/recycle-bin/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkRecycleBinPageReqDTO requestParam){
        return Results.success(recycleBinService.pageRecycleShortLink(requestParam));
    }

    @PostMapping("/api/short-link/v1/recycle-bin/recover")
    public Result<Void> recoverShortLink(@RequestBody RecycleBinRecoverReqDTO requestParam) {
        recycleBinService.recoverShortLink(requestParam);
        return Results.success();
    }
}
