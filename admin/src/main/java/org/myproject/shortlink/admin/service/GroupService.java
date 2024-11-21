package org.myproject.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.myproject.shortlink.admin.dao.entity.GroupDO;
import org.myproject.shortlink.admin.dto.req.GroupReqDTO;
import org.myproject.shortlink.admin.dto.req.GroupSortReqDTO;
import org.myproject.shortlink.admin.dto.req.GroupUpdateReqDTO;
import org.myproject.shortlink.admin.dto.resp.GroupRespDTO;

import java.util.List;

public interface GroupService extends IService<GroupDO> {
    /*
    * 创建短连接分组
    * @param groupParam
    * */
    void saveGroup(GroupReqDTO groupParam);

    /*
    * 查询分组列表
    * */
    List<GroupRespDTO> listGroup();

    /*
    * 更新分组
    * */
    void updateGroup(GroupUpdateReqDTO requestParam);

    /*
    * 删除短连接分组
    * */
    void removeGroup(String groupId);

    /*
    * 短连接分组排序
    * */
    void sortGroup(List<GroupSortReqDTO> groupSortReqDTOList);
}
