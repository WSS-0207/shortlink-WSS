package org.myproject.shortlink.project.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("t_link_os_stats")
public class LinkOsStatsDO extends BaseDO {
    /**
     * id
     */
    private Long id;

    /**
     * full_short_url
     */
    private String fullShortUrl;

    /**
     * gid
     */
    private String gid;

    /**
     * date
     */
    private Date date;

    /**
     * cnt
     */
    private Integer cnt;

    /*
    * os
    * */
    private String os;
}
