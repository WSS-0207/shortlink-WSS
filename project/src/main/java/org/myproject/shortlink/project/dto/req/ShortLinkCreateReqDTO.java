package org.myproject.shortlink.project.dto.req;

import lombok.Data;

import java.util.Date;

@Data
public class ShortLinkCreateReqDTO {
    /**
    * domain
    */
    private String domain;

    /**
    * origin_url
    */
    private String originUrl;

    /**
    * gid
    */
    private String gid;

    /**
    * create_type
    */
    private Integer createType;

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
