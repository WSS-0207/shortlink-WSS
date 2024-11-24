package org.myproject.shortlink.project.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.myproject.shortlink.project.common.database.BaseDO;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_link")
public class ShortLinkDO extends BaseDO {
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
    * enable_status
    */
    private Integer enableStatus;

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
    @TableField("`describe`")
    private String describe;
}
