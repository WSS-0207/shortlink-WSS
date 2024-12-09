package org.myproject.shortlink.admin.remote;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.myproject.shortlink.admin.common.convention.result.Result;
import org.myproject.shortlink.admin.remote.dto.req.ShortLinkStatsAccessRecordReqDTO;
import org.myproject.shortlink.admin.remote.dto.req.ShortLinkStatsReqDTO;
import org.myproject.shortlink.admin.remote.dto.resp.ShortLinkStatsAccessRecordRespDTO;
import org.myproject.shortlink.admin.remote.dto.resp.ShortLinkStatsRespDTO;

import java.util.Map;

public interface ShortLinkRemoveService {
    
    default Result<ShortLinkStatsRespDTO> oneShortLinkStats(ShortLinkStatsReqDTO requestParam){
        String responseStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/stats", BeanUtil.beanToMap(requestParam));
        return JSON.parseObject(responseStr, new TypeReference<>() {
        });
    }

    default Result<Page<ShortLinkStatsAccessRecordRespDTO>> shortLinkStatsAccessRecord(ShortLinkStatsAccessRecordReqDTO requestParam){
        Map<String, Object> stringObjectMap = BeanUtil.beanToMap(requestParam, false, true);
        stringObjectMap.remove("orders");
        stringObjectMap.remove("records");
        String responseStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/stats/access-record", stringObjectMap);
        return JSON.parseObject(responseStr, new TypeReference<>() {
        });
    }
}
