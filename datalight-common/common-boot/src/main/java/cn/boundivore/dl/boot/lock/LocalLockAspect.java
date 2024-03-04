package cn.boundivore.dl.boot.lock;

import cn.boundivore.dl.exception.LockException;
import cn.dev33.satoken.stp.StpUtil;
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

}
