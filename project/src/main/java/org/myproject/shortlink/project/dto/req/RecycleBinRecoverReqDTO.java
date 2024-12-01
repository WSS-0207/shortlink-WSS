package org.myproject.shortlink.project.dto.req;

import lombok.Data;

@Data
public class RecycleBinRecoverReqDTO {
    private String gid;

    private String fullShortUrl;
}
