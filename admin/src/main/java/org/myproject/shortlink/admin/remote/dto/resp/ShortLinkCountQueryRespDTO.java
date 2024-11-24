package org.myproject.shortlink.admin.remote.dto.resp;

import lombok.Data;

@Data
public class ShortLinkCountQueryRespDTO {
    private String gid;
    private Integer shortLinkCount;
}
