package org.myproject.shortlink.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.myproject.shortlink.admin.common.convention.result.Result;
import org.myproject.shortlink.admin.remote.ShortLinkRemoveService;
import org.myproject.shortlink.admin.remote.dto.req.ShortLinkStatsAccessRecordReqDTO;
import org.myproject.shortlink.admin.remote.dto.req.ShortLinkStatsReqDTO;
import org.myproject.shortlink.admin.remote.dto.resp.ShortLinkStatsAccessRecordRespDTO;
import org.myproject.shortlink.admin.remote.dto.resp.ShortLinkStatsRespDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ShortLinkStatsLogsController {
    ShortLinkRemoveService shortLinkRemoveService = new ShortLinkRemoveService() {};

    /**
     * 访问单个短链接指定时间内监控数据
     */
    @GetMapping("/api/short-link/admin/v1/stats")
    public Result<ShortLinkStatsRespDTO> shortLinkStats(ShortLinkStatsReqDTO requestParam) {
        return shortLinkRemoveService.oneShortLinkStats(requestParam);
    }

    /**
     * 访问单个短链接指定时间内访问记录监控数据
     */
    @GetMapping("/api/short-link/admin/v1/stats/access-record")
    public Result<Page<ShortLinkStatsAccessRecordRespDTO>> shortLinkStatsAccessRecord(ShortLinkStatsAccessRecordReqDTO requestParam) {
        return shortLinkRemoveService.shortLinkStatsAccessRecord(requestParam);
    }
}
