package org.myproject.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.myproject.shortlink.project.dao.entity.LinkOsStatsDO;

public interface LinkOsStatsMapper extends BaseMapper<LinkOsStatsDO> {

    /*
    * 短连接地域监控
    * */
    @Insert("INSERT INTO t_link_os_stats (full_short_url, gid, date, cnt, os, create_time, update_time, del_flag) " +
            "VALUES( #{linkOsStats.fullShortUrl}, #{linkOsStats.gid}, #{linkOsStats.date}, " +
            "#{linkOsStats.cnt}, #{linkOsStats.os}, NOW(), NOW(), 0) " +
            "ON DUPLICATE KEY UPDATE cnt = cnt +  #{linkOsStats.cnt};")
    void shortLinkOsStats(@Param("linkOsStats") LinkOsStatsDO linkOsStatsDO);
}
