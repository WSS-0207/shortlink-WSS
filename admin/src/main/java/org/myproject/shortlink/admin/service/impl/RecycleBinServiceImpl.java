package org.myproject.shortlink.admin.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.myproject.shortlink.admin.common.biz.user.UserContext;
import org.myproject.shortlink.admin.common.convention.exception.ServiceException;
import org.myproject.shortlink.admin.common.convention.result.Result;
import org.myproject.shortlink.admin.dao.entity.GroupDO;
import org.myproject.shortlink.admin.dao.mapper.GroupMapper;
import org.myproject.shortlink.admin.remote.ShortLinkService;
import org.myproject.shortlink.admin.remote.dto.req.ShortLinkRecycleBinPageReqDTO;
import org.myproject.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import org.myproject.shortlink.admin.service.RecycleBinService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecycleBinServiceImpl implements RecycleBinService {
    private final GroupMapper groupMapper;
    ShortLinkService shortLinkService = new ShortLinkService() {};
    @Override
    public Result<IPage<ShortLinkPageRespDTO>> pageRecycleShortLink(ShortLinkRecycleBinPageReqDTO requestParam) {
        System.out.println(UserContext.getUsername());
        LambdaQueryWrapper<GroupDO> eq = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getDelFlag, 0);

        List<GroupDO> groupDOS = groupMapper.selectList(eq);
        if (CollUtil.isEmpty(groupDOS)){
            throw new ServiceException("用户无分组信息");
        }
        requestParam.setGidList(groupDOS.stream().map(GroupDO::getGid).toList());
        return shortLinkService.pageRecycleShortLink(requestParam);
    }
}
