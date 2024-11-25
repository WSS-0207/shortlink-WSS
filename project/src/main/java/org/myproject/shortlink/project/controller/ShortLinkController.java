package org.myproject.shortlink.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import lombok.RequiredArgsConstructor;
import org.myproject.shortlink.project.common.convention.result.Result;
import org.myproject.shortlink.project.common.convention.result.Results;
import org.myproject.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import org.myproject.shortlink.project.dto.req.ShortLinkPageReqDTO;
import org.myproject.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import org.myproject.shortlink.project.dto.resp.ShortLinkCountQueryRespDTO;
import org.myproject.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import org.myproject.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import org.myproject.shortlink.project.service.ShortLinkService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ShortLinkController {

    private final ShortLinkService shortLinkService;

    /*
    * 创建短连接
    * */
    @PostMapping("/api/short-link/v1/create")
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam) {
        return Results.success(shortLinkService.createShortLink(requestParam));
    }

    /*
    * 更新短连接
    * */
    @PostMapping("/api/short-link/v1/update")
    public Result<Void> update(@RequestBody ShortLinkUpdateReqDTO requestParam){
        shortLinkService.update(requestParam);
        return Results.success();
    }

    /*
    * 短连接分页查询
    * */
    @GetMapping("/api/short-link/v1/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam){
        return Results.success(shortLinkService.pageShortLink(requestParam));
    }

    /*
    * 短连接计数
    * */
    @GetMapping("/api/short-link/v1/count-short-link")
    public Result<List<ShortLinkCountQueryRespDTO>> countShortLink(@RequestParam("requestParam") List<String> requestParam){
        return Results.success(shortLinkService.countShortLink(requestParam));
    }

    @GetMapping("/{short-link}")
    public void restoreUrl(@PathVariable("short-link") String shortLink, ServletRequest request, ServletResponse response) throws IOException {
        shortLinkService.restoreUrl(shortLink, request, response);
    }
}
