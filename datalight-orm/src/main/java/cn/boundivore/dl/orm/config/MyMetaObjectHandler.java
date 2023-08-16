package cn.boundivore.dl.orm.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


/**
 * Description: Automatically populates the time in the database
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/13
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler, CommandLineRunner {

    @Override
    public void insertFill(MetaObject metaObject) {
        metaObject.setValue("createTime", null);
        metaObject.setValue("updateTime", null);

        long ts = System.currentTimeMillis();
        this.strictInsertFill(metaObject, "createTime", Long.class, ts);
        this.strictInsertFill(metaObject, "updateTime", Long.class, ts);
        this.strictInsertFill(metaObject, "version", Long.class, 0L);

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        metaObject.setValue("updateTime", null);

        long ts = System.currentTimeMillis();
        this.strictUpdateFill(metaObject, "updateTime", Long.class, ts);

    }

    @Override
    public void run(String... args) throws Exception {
        JacksonTypeHandler.setObjectMapper(new ObjectMapper());
    }
}
