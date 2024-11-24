package org.myproject.shortlink.admin;

public class UserTableShardingTest {
    public static final String SQL = "CREATE TABLE `t_user_%d` (" +
            "`id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID'," +
            "`username` varchar(256) DEFAULT NULL COMMENT '用户名'," +
            "`password` varchar(512) DEFAULT NULL COMMENT '密码'," +
            "`real_name` varchar(256) DEFAULT NULL COMMENT '真实姓名'," +
            "`phone` varchar(128) DEFAULT NULL COMMENT '手机号'," +
            "`mail` varchar(512) DEFAULT NULL COMMENT '邮箱'," +
            "`deletion_time` bigint(20) DEFAULT NULL COMMENT '注销时间戳'," +
            "`create_time` datetime DEFAULT NULL COMMENT '创建时间'," +
            "`update_time` datetime DEFAULT NULL COMMENT '修改时间'," +
            "`del_flag` tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除'," +
            "PRIMARY KEY (`id`)" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";

    public static final String LINK_SQL = "CREATE TABLE `t_link_%d` (" +
            "`id` bigint(20) NOT NULL COMMENT 'ID'," +
            "`domain` varchar(128) DEFAULT NULL," +
            "`short_url` varchar(8) DEFAULT NULL,   " +
            "`full_short_url` varchar(128) DEFAULT NULL," +
            "`origin_url` varchar(1024) DEFAULT NULL," +
            "`click_num` int(11) unsigned zerofill DEFAULT NULL," +
            "`gid` varchar(32) DEFAULT NULL," +
            "`favicon` varchar(255) DEFAULT NULL COMMENT '网站标识图片地址',          " +
            "`enable_status` tinyint(1) DEFAULT NULL," +
            "`create_type` tinyint(1) DEFAULT NULL, " +
            "`valid_date_type` tinyint(1) DEFAULT NULL, " +
            "`valid_date` datetime DEFAULT NULL, " +
            "`describe` varchar(1024) DEFAULT NULL, " +
            "`create_time` datetime DEFAULT NULL COMMENT '创建时间', " +
            "`update_time` datetime DEFAULT NULL COMMENT '修改时间', " +
            "`del_flag` tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除'," +
            "PRIMARY KEY (`id`) USING BTREE, " +
            "UNIQUE KEY `idx_unique_full_short` (`full_short_url`) USING BTREE" +
            ") ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;";
    public static final String GROUP_SQL = "CREATE TABLE `t_group_%d` (" +
            "`id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID'," +
            "`gid` varchar(32) DEFAULT NULL COMMENT '分组标识'," +
            "`name` varchar(64) DEFAULT NULL COMMENT '分组名称'," +
            "`username` varchar(256) DEFAULT NULL COMMENT '创建分组用户名'," +
            "`sort_order` int(3) DEFAULT NULL COMMENT '分组排序'," +
            "`create_time` datetime DEFAULT NULL COMMENT '创建时间'," +
            "`update_time` datetime DEFAULT NULL COMMENT '修改时间'," +
            "`del_flag` tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除'," +
            "PRIMARY KEY (`id`)," +
            "UNIQUE KEY `idx_unique_username_gid` (`gid`,`username`) USING BTREE" +
            ") ENGINE=InnoDB AUTO_INCREMENT=1859519988175044610 DEFAULT CHARSET=utf8mb4;";
    public static void main(String[] args) {
        for (int i = 0; i < 16; i++) {
            System.out.println(String.format(GROUP_SQL, i));
        }
    }
}
