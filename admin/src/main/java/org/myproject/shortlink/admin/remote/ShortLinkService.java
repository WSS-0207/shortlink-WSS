package org.myproject.shortlink.admin.remote;


import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.myproject.shortlink.admin.common.convention.result.Result;
import org.myproject.shortlink.admin.remote.dto.req.ShortLinkCreateReqDTO;
import org.myproject.shortlink.admin.remote.dto.req.ShortLinkPageReqDTO;
import org.myproject.shortlink.admin.remote.dto.req.ShortLinkUpdateReqDTO;
import org.myproject.shortlink.admin.remote.dto.resp.ShortLinkCountQueryRespDTO;
import org.myproject.shortlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import org.myproject.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ShortLinkService {
    /*
    * 创建短连接
    * */
    default Result<ShortLinkCreateRespDTO> createShortLink(ShortLinkCreateReqDTO requestParam) {
        String post = HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/create", JSON.toJSONString(requestParam));
        return JSON.parseObject(post, new TypeReference<>() {
        });
    }

    /*
    * 分页查询短连接
    * */

    default Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam) {
        Map<String,Object> map = new HashMap<>();
        map.put("gid",requestParam.getGid());
        map.put("current",requestParam.getCurrent());
        map.put("size",requestParam.getSize());
        String get = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/page", map);
        return JSON.parseObject(get, new TypeReference<>() {
        });
    }

    default Result<Void> update(ShortLinkUpdateReqDTO requestParam){
        String post = HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/update", JSON.toJSONString(requestParam));
        return JSON.parseObject(post, new TypeReference<>() {
        });
    }

    default Result<List<ShortLinkCountQueryRespDTO>> countShortLink(List<String> requestParam) {
        Map<String,Object> map = new HashMap<>();
        map.put("requestParam",requestParam);
        String get = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/count-short-link", map);
        return JSON.parseObject(get, new TypeReference<>() {
        });
    }
}
