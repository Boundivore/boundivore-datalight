package cn.boundivore.dl.boot.lock;

import cn.boundivore.dl.base.constants.Constants;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * Description: 当前服务本地锁 TODO 可以考虑使用 SPEL 注解实现动态参数控制并发锁，实现多集群多用户安全并发操作
 * Created by: Boundivore
 * Creation time: 2024/2/29
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Target({ElementType.LOCAL_VARIABLE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LocalLock {

    long timeout() default Constants.LOCK_TIMEOUT_DEFAULT;

    TimeUnit unit() default TimeUnit.MILLISECONDS;
}
