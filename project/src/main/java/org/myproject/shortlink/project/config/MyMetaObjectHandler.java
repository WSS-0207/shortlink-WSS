package org.myproject.shortlink.project.config;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        // 为什么不能localdatetime????
        strictInsertFill(metaObject, "createTime", DateTime::now, DateTime.class);
        strictInsertFill(metaObject, "updateTime", DateTime::now, DateTime.class);
        strictInsertFill(metaObject, "delFlag", ()->0, Integer.class);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        strictUpdateFill(metaObject, "updateTime", DateTime::now, DateTime.class);
    }
}
