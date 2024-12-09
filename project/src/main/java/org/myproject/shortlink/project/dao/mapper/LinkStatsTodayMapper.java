package org.myproject.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.myproject.shortlink.project.dao.entity.LinkStatsTodayDO;

public interface LinkStatsTodayMapper extends BaseMapper<LinkStatsTodayDO> {
    /*
     * 短连接地域监控
     * */
    @Insert("INSERT INTO t_link_stats_today (full_short_url, gid, date, today_pv,today_uv,today_uip, create_time, update_time, del_flag) " +
            "VALUES(#{linkStatsToday.fullShortUrl}, #{linkStatsToday.gid}, #{linkStatsToday.date}, " +
            "#{linkStatsToday.todayPv}, #{linkStatsToday.todayUv}, #{linkStatsToday.todayUip},NOW(), NOW(), 0) " +
            "ON DUPLICATE KEY UPDATE today_pv = today_pv + #{linkStatsToday.todayPv},today_uv = today_uv + #{linkStatsToday.todayUv},today_uip = today_uip + #{linkStatsToday.todayUip};")
    void shortLinkStatsToday(@Param("linkStatsToday") LinkStatsTodayDO linkStatsTodayDO);
}
