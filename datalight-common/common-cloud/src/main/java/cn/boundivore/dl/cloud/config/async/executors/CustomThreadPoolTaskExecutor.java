package cn.boundivore.dl.cloud.config.async.executors;

import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * Description: CustomThreadPoolTaskExecutor
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/3/30
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class CustomThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {


    @Override
    public void execute(@NotNull Runnable task) {
        super.execute(task);
    }

    @Override
    public Future<?> submit(@NotNull Runnable task) {
        return super.submit(task);
    }

    @Override
    public <T> Future<T> submit(@NotNull Callable<T> task) {
        return super.submit(task);
    }

    @Override
    public ListenableFuture<?> submitListenable(@NotNull Runnable task) {
        return super.submitListenable(task);
    }

    @Override
    public <T> ListenableFuture<T> submitListenable(@NotNull Callable<T> task) {
        return super.submitListenable(task);
    }
}