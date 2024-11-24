package org.myproject.shortlink.project.dto.resp;

import lombok.Data;

@Data
public class ShortLinkCountQueryRespDTO {
    private String gid;
    private Integer shortLinkCount;
}
