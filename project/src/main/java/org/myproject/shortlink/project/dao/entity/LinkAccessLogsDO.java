package org.myproject.shortlink.project.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.myproject.shortlink.project.common.database.BaseDO;

@Data
@TableName("t_link_access_logs")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkAccessLogsDO extends BaseDO {
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
     * user
     */
    private String user;

    /**
     * browser
     */
    private String browser;

    /**
     * os
     */
    private String os;

    /**
     * ip
     */
    private String ip;
}
