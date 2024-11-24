package org.myproject.shortlink.admin.remote.dto.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    private Integer validDateType;


    /**
    * valid_data
    */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date validDate;

    /*
    * 创建时间
    * */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;

    /**
    * describe
    */
    private String describe;
}
