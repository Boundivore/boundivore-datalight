package cn.boundivore.dl.boot.lock;

import cn.boundivore.dl.base.request.IRequest;
import cn.boundivore.dl.exception.LockException;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Description: 当前服务本地锁
 * Created by: Boundivore
 * Creation time: 2024/2/29
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Aspect
@Component
@Slf4j
public class LocalLockAspect {
    private final ConcurrentHashMap<String, ReentrantLock> lockMap = new ConcurrentHashMap<>();

    @Resource
    private ApplicationContext applicationContext;

    @Pointcut(value = "@annotation(cn.boundivore.dl.boot.lock.LocalLock)")
    public void lockPointCut() {
    }

    @SneakyThrows
    @Around(value = "lockPointCut()")
    public Object lock(ProceedingJoinPoint joinPoint) {

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();

        LocalLock localLockAnnotation = method.getAnnotation(LocalLock.class);
        String clusterId = StpUtil.getSession().get("clusterId", "0");
        String principal = StpUtil.getSession().get("principal", "defaultPrincipal");

        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            try {
                String findClusterId = this.findClusterId(arg);
                if (findClusterId != null) {
                    clusterId = findClusterId;
                    break;
                }
            } catch (IllegalAccessException e) {
                log.error(ExceptionUtil.stacktraceToString(e));
            }
        }

        long timeout = localLockAnnotation.timeout();
        TimeUnit timeUnit = localLockAnnotation.unit();

        // 构造锁的键
        String key = generateKey(clusterId, principal);
        ReentrantLock lock = lockMap.computeIfAbsent(key, k -> new ReentrantLock());

        try {
            if (!lock.tryLock(timeout, timeUnit)) {
                throw new LockException("获取单机锁超时");
            }
            // 执行目标方法
            return joinPoint.proceed();
        } finally {
            lock.unlock();
            lockMap.remove(key);
        }

    }

    /**
     * Description: 生成锁键
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/2/29
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId 集群 ID
     * @param principal 用户主体
     * @return 锁键
     */
    private String generateKey(String clusterId, String principal) {
        return "LockKey::ClusterId:" + clusterId + "::Principal:" + principal;
    }

    /**
     * Description: 查找参数中的 clusterId 属性
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/3/5
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param obj 被反射函数的参数对象
     * @return clusterId 查找 clusterId 属性
     */
    private String findClusterId(Object obj) throws IllegalAccessException {
        if (obj == null) {
            return null;
        }

        if(!(obj instanceof IRequest)){
            return null;
        }

        // 获取对象类
        Class<?> objClass = obj.getClass();
        // 遍历所有字段
        for (Field field : objClass.getDeclaredFields()) {
            field.setAccessible(true);
            // 如果是我们想找的字段
            if ("clusterId".equals(field.getName())) {
                // 获取该字段的值
                return field.get(obj).toString();
            } else if (IRequest.class.isAssignableFrom(field.getType())) {
                // 如果该类型实现了 IRequest 接口，则继续查找
                Object nestedObj = field.get(obj); // 获取字段的实例对象
                if (nestedObj != null) {
                    // 递归调用 findClusterId 方法
                    String nestedClusterId = this.findClusterId(nestedObj);
                    if (nestedClusterId != null) {
                        // 如果在嵌套对象中找到了 clusterId 字段，则返回它的值
                        return nestedClusterId;
                    }
                }
            }
        }

        // 如果当前类没有找到，递归检查父类（如果需要）
//        if (objClass.getSuperclass() != null) {
//            return this.findClusterId(objClass.getSuperclass().newInstance());
//        }
        return null;
    }

}
