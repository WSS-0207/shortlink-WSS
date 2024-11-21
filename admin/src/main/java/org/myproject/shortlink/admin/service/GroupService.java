package org.myproject.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.myproject.shortlink.admin.dao.entity.GroupDO;
import org.myproject.shortlink.admin.dto.req.GroupReqDTO;

public interface GroupService extends IService<GroupDO> {
    /*
    * 创建短连接分组
    * @param groupParam
    * */
    void saveGroup(GroupReqDTO groupParam);
}
