package org.myproject.shortlink.project.config;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        // 为什么不能localdatetime????
        strictInsertFill(metaObject, "createTime", DateTime::now, Date.class);
        strictInsertFill(metaObject, "updateTime", DateTime::now, Date.class);
        strictInsertFill(metaObject, "delFlag", ()->0, Integer.class);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        strictUpdateFill(metaObject, "updateTime", DateTime::now, Date.class);
    }
}
