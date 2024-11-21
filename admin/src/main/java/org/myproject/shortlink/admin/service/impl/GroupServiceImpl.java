package org.myproject.shortlink.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.myproject.shortlink.admin.dao.entity.GroupDO;
import org.myproject.shortlink.admin.dao.mapper.GroupMapper;
import org.myproject.shortlink.admin.dto.req.GroupReqDTO;
import org.myproject.shortlink.admin.service.GroupService;
import org.myproject.shortlink.admin.util.GroupIdGenerator;
import org.springframework.stereotype.Service;

@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO>  implements GroupService {
    @Override
    public void saveGroup(GroupReqDTO groupParam) {
        String gid;
        do {
            gid = GroupIdGenerator.generateRandomString();
        }while (hasGroupId(gid));
        GroupDO groupDO = GroupDO.builder()
                .gid(gid)
                .name(groupParam.getName())
                .build();
        baseMapper.insert(groupDO);
    }

    private boolean hasGroupId(String gid) {
        LambdaQueryWrapper<GroupDO> eq = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid, gid)
                // TODO 获取用户名
                .eq(GroupDO::getUsername, null);
        GroupDO groupDO = baseMapper.selectOne(eq);
        return groupDO!=null;
    }


}
