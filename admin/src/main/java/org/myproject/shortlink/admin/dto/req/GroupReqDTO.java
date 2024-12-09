package org.myproject.shortlink.admin.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.myproject.shortlink.admin.common.biz.user.UserContext;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupReqDTO {

    /**
    * 分组名
    * */
    private String name;


    /**
    * 用户名
    * */
    private String username = UserContext.getUsername();
}
