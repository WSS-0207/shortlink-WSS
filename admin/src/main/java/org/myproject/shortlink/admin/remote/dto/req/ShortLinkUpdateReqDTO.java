package org.myproject.shortlink.admin.remote.dto.req;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class ShortLinkUpdateReqDTO {

    /**
    * short_url
    */
    private String shortUrl;


    /**
    * origin_url
    */
    private String originUrl;

    /**
    * 完整短连接
    * */
    private String fullShortUrl;

    /**
    * gid
    */
    private String gid;

    /**
    * 网站标识图片地址
    */
    private String favicon;

    /**
    * enable_status
    */
    private Integer enableStatus;


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
    @TableField("`describe`")
    private String describe;
}
