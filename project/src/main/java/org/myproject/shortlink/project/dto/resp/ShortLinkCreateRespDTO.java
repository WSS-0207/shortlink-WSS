package org.myproject.shortlink.project.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortLinkCreateRespDTO {
    /**
    * full_short_url
    */
    private String fullShortUrl;

    /**
    * origin_url
    */
    private String originUrl;

    /**
    * gid
    */
    private String gid;
}
