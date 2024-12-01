package org.myproject.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.myproject.shortlink.project.dao.entity.LinkAccessLogsDO;

public interface LinkAccessLogsMapper extends BaseMapper<LinkAccessLogsDO> {
    /*
     * 短链接访问IP统计
     * */
    @Insert("INSERT INTO t_link_access_logs (full_short_url, gid, user, browser, os, ip, create_time, update_time, del_flag) " +
            "VALUES( #{linkAccessLogs.fullShortUrl}, #{linkAccessLogs.gid}, #{linkAccessLogs.user}," +
            "#{linkAccessLogs.browser}, #{linkAccessLogs.os}, #{linkAccessLogs.ip}, NOW(), NOW(), 0);")
    void shortLinkAccessLogs(@Param("linkAccessLogs") LinkAccessLogsDO linkAccessLogsDO);
}
