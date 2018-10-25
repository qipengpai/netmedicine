package com.qpp.framework.manager;

import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * @ClassName ShiroConfig
 * @Description TODO 异步任务管理器
 * @Author qipengpai
 * @Date 2018/10/25 11:53
 * @Version 1.0.1
 */
public class AsyncManager {
    /**
     * 操作延迟10毫秒
     */
    private final int OPERATE_DELAY_TIME = 10;

    /**
     * 异步操作任务调度线程池  能完成Timer一样的定时任务，并且时间间隔更加准确
     */
    private ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(5);

    /**
     * 单例模式
     */
    private static AsyncManager me = new AsyncManager();

    public static AsyncManager me()
    {
        return me;
    }


    /**
     * @Author qipengpai
     * @Description //TODO 执行任务
     * @Date 2018/10/25 12:54
     * @Param [task] 任务
     * @return void
     * @throws
     **/
    public void execute(TimerTask task) {
        executor.schedule(task, OPERATE_DELAY_TIME, TimeUnit.MILLISECONDS);
    }
}
