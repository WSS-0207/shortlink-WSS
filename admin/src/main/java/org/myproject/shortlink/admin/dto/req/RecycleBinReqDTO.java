package org.myproject.shortlink.admin.dto.req;

import lombok.Data;

@Data
public class RecycleBinReqDTO {
    private String gid;

    private String fullShortUrl;
}
