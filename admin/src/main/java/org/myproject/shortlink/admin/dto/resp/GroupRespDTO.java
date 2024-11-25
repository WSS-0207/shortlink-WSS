package org.myproject.shortlink.admin.dto.resp;

import lombok.Data;

@Data
public class GroupRespDTO {
    private String gid;

    /**
    * 分组名称
    */
    private String name;



    /**
    * 分组排序
    */
    private Integer sortOrder;

    private Integer shortLinkCount;
}
