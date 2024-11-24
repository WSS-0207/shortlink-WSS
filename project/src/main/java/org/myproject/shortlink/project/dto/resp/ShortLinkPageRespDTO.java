package org.myproject.shortlink.project.dto.resp;

import lombok.Data;

import java.util.Date;

@Data
public class ShortLinkPageRespDTO {
    /**
    * ID
    */
    private Long id;

    /**
    * domain
    */
    private String domain;

    /**
    * short_url
    */
    private String shortUrl;

    /**
    * full_short_url
    */
    private String fullShortUrl;

    /**
    * origin_url
    */
    private String originUrl;

    /**
    * click_num
    */
    private Integer clickNum;

    /**
    * gid
    */
    private String gid;

    /**
    * 网站标识图片地址
    */
    private String favicon;

    /**
    * valid_data_type
    */
    private Integer validDataType;

    /**
    * valid_data
    */
    private Date validData;

    /**
    * describe
    */
    private String describe;
}
