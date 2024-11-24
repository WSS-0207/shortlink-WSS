package org.myproject.shortlink.admin.remote.dto.req;

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
    private Integer validDateType;

    /**
    * valid_data
    */
    private Date validDate;

    /**
    * describe
    */
    private String describe;
}
