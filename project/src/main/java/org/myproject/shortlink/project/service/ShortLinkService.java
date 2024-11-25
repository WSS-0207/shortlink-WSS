package org.myproject.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.myproject.shortlink.project.dao.entity.ShortLinkDO;
import org.myproject.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import org.myproject.shortlink.project.dto.req.ShortLinkPageReqDTO;
import org.myproject.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import org.myproject.shortlink.project.dto.resp.ShortLinkCountQueryRespDTO;
import org.myproject.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import org.myproject.shortlink.project.dto.resp.ShortLinkPageRespDTO;

import java.io.IOException;
import java.util.List;

public interface ShortLinkService extends IService<ShortLinkDO> {
    /*
    * 创建短连接
    * */
    ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam);

    /*
    * 分页查询
    * */
    IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam);

    /*
    * 分组计数
    * */
    List<ShortLinkCountQueryRespDTO> countShortLink(List<String> requestParam);

    /*
    * 更新短连接
    * */
    void update(ShortLinkUpdateReqDTO requestParam);

    /*
    * 缓存穿透
    * */
    void restoreUrl(String shortLink, ServletRequest request, ServletResponse response) throws IOException;
}
