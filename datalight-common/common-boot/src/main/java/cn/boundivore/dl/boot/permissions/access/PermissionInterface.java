package cn.boundivore.dl.boot.permissions.access;

import java.lang.annotation.*;

/**
 * Description: 该注解用于验证访问接口 URI 的权限
 * Created by: Boundivore
 * Creation time: 2024/4/9
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PermissionInterface {

    String name() default "";

    boolean enabled() default true;

    Class<? extends IAccessAuthorizationHandler> authHandler() default IAccessAuthorizationHandler.class;

}
