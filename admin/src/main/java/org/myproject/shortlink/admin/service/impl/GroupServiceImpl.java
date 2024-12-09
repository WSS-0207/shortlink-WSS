package org.myproject.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import org.myproject.shortlink.admin.common.biz.user.UserContext;
import org.myproject.shortlink.admin.common.convention.exception.ClientException;
import org.myproject.shortlink.admin.common.convention.result.Result;
import org.myproject.shortlink.admin.dao.entity.GroupDO;
import org.myproject.shortlink.admin.dao.mapper.GroupMapper;
import org.myproject.shortlink.admin.dto.req.GroupReqDTO;
import org.myproject.shortlink.admin.dto.req.GroupSortReqDTO;
import org.myproject.shortlink.admin.dto.req.GroupUpdateReqDTO;
import org.myproject.shortlink.admin.dto.resp.GroupRespDTO;
import org.myproject.shortlink.admin.remote.ShortLinkService;
import org.myproject.shortlink.admin.remote.dto.resp.ShortLinkCountQueryRespDTO;
import org.myproject.shortlink.admin.service.GroupService;
import org.myproject.shortlink.admin.util.GroupIdGenerator;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.myproject.shortlink.admin.common.constant.RedisCacheConstant.LOCK_GROUP_CREATE_KEY;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO>  implements GroupService {
    ShortLinkService shortLinkService = new ShortLinkService() {};
    private final RedissonClient redissonClient;

    @Value("short-link.group.max-num")
    private String groupMaxNum;
    /*
    * 新增分组
    * */
    @Override
    public void saveGroup(GroupReqDTO groupParam) {
        RLock lock = redissonClient.getLock(String.format(LOCK_GROUP_CREATE_KEY,groupParam.getUsername()));
        lock.lock();
        try {
            LambdaQueryWrapper<GroupDO> eq = Wrappers.lambdaQuery(GroupDO.class).eq(GroupDO::getUsername, groupParam.getUsername()).eq(GroupDO::getDelFlag, 0);
            List<GroupDO> groupDOS = baseMapper.selectList(eq);
            if(CollUtil.isNotEmpty(groupDOS)&&groupDOS.size()>=Integer.parseInt(groupMaxNum)){
                throw new ClientException(String.format("分组超过最大创建数： %d", Integer.parseInt(groupMaxNum)));
            }

            String gid;
            do {
                gid = GroupIdGenerator.generateRandomString();
            }while (hasGroupId(groupParam.getUsername(),gid));
            GroupDO groupDO = GroupDO.builder()
                    .gid(gid)
                    .sortOrder(0)
                    .username(groupParam.getUsername())
                    .name(groupParam.getName())
                    .build();
            baseMapper.insert(groupDO);
        }finally {
            lock.unlock();
        }
    }

    /*
    * 查询分组
    * */
    @Override
    public List<GroupRespDTO> listGroup() {
        LambdaQueryWrapper<GroupDO> eq = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getDelFlag, 0)
                // 根据上下文获取用户名
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .orderByDesc(GroupDO::getSortOrder, GroupDO::getUpdateTime);
        List<GroupDO> groupDOS = baseMapper.selectList(eq);
        Result<List<ShortLinkCountQueryRespDTO>> shortLinkCountResult = shortLinkService
                .countShortLink(groupDOS.stream().map(GroupDO::getGid).toList());
        List<GroupRespDTO> shortLinkGroup = BeanUtil.copyToList(groupDOS,GroupRespDTO.class);
        shortLinkGroup.forEach(each->{
            Optional<ShortLinkCountQueryRespDTO> first = shortLinkCountResult.getData().stream()
                    .filter(item -> Objects.equals(item.getGid(), each.getGid()))
                    .findFirst();
            first.ifPresent(item->each.setShortLinkCount(first.get().getShortLinkCount()));
        });
        return shortLinkGroup;
    }

    @Override
    public void updateGroup(GroupUpdateReqDTO requestParam) {
        LambdaUpdateWrapper<GroupDO> eq = Wrappers.lambdaUpdate(GroupDO.class)
                .eq(GroupDO::getGid, requestParam.getGid())
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getDelFlag, 0);
        baseMapper.update(GroupDO.builder().name(requestParam.getName()).build(), eq);
    }

    @Override
    public void removeGroup(String groupId) {
        LambdaUpdateWrapper<GroupDO> eq = Wrappers.lambdaUpdate(GroupDO.class)
                .eq(GroupDO::getGid, groupId)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getDelFlag, 0);
        GroupDO groupDO = new GroupDO();
        groupDO.setDelFlag(1);
        baseMapper.update(groupDO,eq);
    }

    @Override
    public void sortGroup(List<GroupSortReqDTO> groupList) {
        groupList.forEach(each->{
            GroupDO build = GroupDO.builder().sortOrder(each.getSortOrder()).build();
            LambdaUpdateWrapper<GroupDO> eq = Wrappers.lambdaUpdate(GroupDO.class)
                    .eq(GroupDO::getGid, each.getGid())
                    .eq(GroupDO::getUsername, UserContext.getUsername())
                    .eq(GroupDO::getDelFlag, 0);
            baseMapper.update(build,eq);
                }
        );
    }

    private boolean hasGroupId(String username, String gid) {
        LambdaQueryWrapper<GroupDO> eq = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid, gid)
                // 根据上下文获取用户名
                .eq(GroupDO::getUsername, Optional.ofNullable(username).orElse(UserContext.getUsername()));
        GroupDO groupDO = baseMapper.selectOne(eq);
        return groupDO!=null;
    }
}
