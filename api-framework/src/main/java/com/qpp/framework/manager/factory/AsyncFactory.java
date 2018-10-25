package com.qpp.framework.manager.factory;

import java.util.TimerTask;

import com.qpp.apicommons.constant.Constants;
import com.qpp.apicommons.utils.AddressUtils;
import com.qpp.framework.shiro.session.OnlineSession;
import com.qpp.framework.util.LogUtils;
import com.qpp.framework.util.ServletUtils;
import com.qpp.framework.util.ShiroUtils;
import com.qpp.framework.util.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.qpp.system.domain.SysLogininfor;
import com.qpp.system.domain.SysOperLog;
import com.qpp.system.domain.SysUserOnline;
import com.qpp.system.service.ISysOperLogService;
import com.qpp.system.service.impl.SysLogininforServiceImpl;
import com.qpp.system.service.impl.SysUserOnlineServiceImpl;
import eu.bitwalker.useragentutils.UserAgent;


/**
 * @ClassName OnlineSessionFilter
 * @Description TODO 异步工厂（产生任务用）
 * @Author qipengpai
 * @Date 2018/10/25 11:41
 * @Version 1.0.1
 */
public class AsyncFactory {
    private static final Logger sys_user_logger = LoggerFactory.getLogger("sys-user");

    /**
     * @Author qipengpai
     * @Description //TODO 同步session到数据库
     * @Date 2018/10/25 12:53
     * @Param [session] 在线用户会话
     * @return java.util.TimerTask 任务task
     * @throws
     **/
    public static TimerTask syncSessionToDb(final OnlineSession session) {
        //是jdk自带的定时任务实现
        return new TimerTask() {
            @Override
            public void run() {
                SysUserOnline online = new SysUserOnline();
                online.setSessionId(String.valueOf(session.getId()));
                online.setDeptName(session.getDeptName());
                online.setLoginName(session.getLoginName());
                online.setStartTimestamp(session.getStartTimestamp());
                online.setLastAccessTime(session.getLastAccessTime());
                online.setExpireTime(session.getTimeout());
                online.setIpaddr(session.getHost());
                online.setLoginLocation(AddressUtils.getRealAddressByIP(session.getHost()));
                online.setBrowser(session.getBrowser());
                online.setOs(session.getOs());
                online.setStatus(session.getStatus());
                SpringUtils.getBean(SysUserOnlineServiceImpl.class).saveOnline(online);
            }
        };
    }


    /**
     * @Author qipengpai
     * @Description //TODO 操作日志记录
     * @Date 2018/10/25 13:31
     * @Param [operLog] 操作日志信息
     * @return java.util.TimerTask 任务task
     * @throws
     **/
    public static TimerTask recordOper(final SysOperLog operLog) {
        return new TimerTask() {
            @Override
            public void run() {
                // 远程查询操作地点
                operLog.setOperLocation(AddressUtils.getRealAddressByIP(operLog.getOperIp()));
                SpringUtils.getBean(ISysOperLogService.class).insertOperlog(operLog);
            }
        };
    }

    /**
     * @Author qipengpai
     * @Description //TODO 记录登陆信息
     * @Date 2018/10/25 13:25
     * @Param [username, status, message, args]  用户名 ，状态， 消息，列表
     * @return java.util.TimerTask
     * @throws
     **/
    public static TimerTask recordLogininfor(final String username, final String status, final String message, final Object... args) {
        final UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtils.getRequest().getHeader("User-Agent"));
        final String ip = ShiroUtils.getIp();
        return new TimerTask()
        {
            @Override
            public void run()
            {
                StringBuilder s = new StringBuilder();
                s.append(LogUtils.getBlock(ip));
                s.append(AddressUtils.getRealAddressByIP(ip));
                s.append(LogUtils.getBlock(username));
                s.append(LogUtils.getBlock(status));
                s.append(LogUtils.getBlock(message));
                // 打印信息到日志
                sys_user_logger.info(s.toString(), args);
                // 获取客户端操作系统
                String os = userAgent.getOperatingSystem().getName();
                // 获取客户端浏览器
                String browser = userAgent.getBrowser().getName();
                // 封装对象
                SysLogininfor logininfor = new SysLogininfor();
                logininfor.setLoginName(username);
                logininfor.setIpaddr(ip);
                logininfor.setLoginLocation(AddressUtils.getRealAddressByIP(ip));
                logininfor.setBrowser(browser);
                logininfor.setOs(os);
                logininfor.setMsg(message);
                // 日志状态
                if (Constants.LOGIN_SUCCESS.equals(status) || Constants.LOGOUT.equals(status)) {
                    logininfor.setStatus(Constants.SUCCESS);
                }
                else if (Constants.LOGIN_FAIL.equals(status)) {
                    logininfor.setStatus(Constants.FAIL);
                }
                // 插入数据
                SpringUtils.getBean(SysLogininforServiceImpl.class).insertLogininfor(logininfor);
            }
        };
    }
}
