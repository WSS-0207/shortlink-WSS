package org.myproject.shortlink.admin.remote.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern="YYYY-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date validDate;

    /**
    * describe
    */
    private String describe;
}
