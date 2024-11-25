package org.myproject.shortlink.admin.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String username;
}
