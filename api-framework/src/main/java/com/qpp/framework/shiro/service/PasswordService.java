package com.qpp.framework.shiro.service;

import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.PostConstruct;

import com.qpp.apicommons.constant.Constants;
import com.qpp.framework.manager.AsyncManager;
import com.qpp.framework.manager.factory.AsyncFactory;
import com.qpp.framework.util.MessageUtils;
import com.qpp.framework.web.exception.user.UserPasswordNotMatchException;
import com.qpp.framework.web.exception.user.UserPasswordRetryLimitExceedException;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.qpp.system.domain.SysUser;


/**
 * @ClassName PasswordService
 * @Description TODO 登录密码方法
 * @Author qipengpai
 * @Date 2018/10/25 13:41
 * @Version 1.0.1
 */
@Component
public class PasswordService {
    @Autowired
    private CacheManager cacheManager;

    private Cache<String, AtomicInteger> loginRecordCache;

    //大于多少次锁定 5
    @Value(value = "${user.password.maxRetryCount}")
    private String maxRetryCount;

    @PostConstruct
    public void init()
    {
        loginRecordCache = cacheManager.getCache("loginRecordCache");
    }

    /**
     * @Author qipengpai
     * @Description //TODO 验证密码
     * @Date 2018/10/25 14:48
     * @Param [user, password]
     * @return void
     * @throws
     **/
    public void validate(SysUser user, String password) {
        String loginName = user.getLoginName();
        //得到当前用户验证密码次数
        AtomicInteger retryCount = loginRecordCache.get(loginName);
        //如果第一次验证为0
        if (retryCount == null) {
            retryCount = new AtomicInteger(0);
            loginRecordCache.put(loginName, retryCount);
        }
        //验证密码次数是否大约五次
        if (retryCount.incrementAndGet() > Integer.valueOf(maxRetryCount).intValue()) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(loginName, Constants.LOGIN_FAIL, MessageUtils.message("user.password.retry.limit.exceed", maxRetryCount)));
            throw new UserPasswordRetryLimitExceedException(Integer.valueOf(maxRetryCount).intValue());
        }
        //匹配密码
        if (!matches(user, password)) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(loginName, Constants.LOGIN_FAIL, MessageUtils.message("user.password.retry.limit.count", retryCount)));
            //存储加一次
            loginRecordCache.put(loginName, retryCount);
            throw new UserPasswordNotMatchException();
        }
        else {
            //清空该用户错误密码次数
            clearLoginRecordCache(loginName);
        }
    }

    /**
     * @Author qipengpai
     * @Description //TODO 匹配密码
     * @Date 2018/10/25 14:54
     * @Param [user, newPassword]
     * @return boolean
     * @throws
     **/
    public boolean matches(SysUser user, String newPassword) {
        return user.getPassword().equals(encryptPassword(user.getLoginName(), newPassword, user.getSalt()));
    }

    /**
     * @Author qipengpai
     * @Description //TODO 清空该用户错误密码次数
     * @Date 2018/10/25 14:58
     * @Param [username] 
     * @return void
     * @throws 
     **/
    public void clearLoginRecordCache(String username) {
        loginRecordCache.remove(username);
    }

    /**
     * @Author qipengpai
     * @Description //TODO 加密密码
     * @Date 2018/10/25 13:34
     * @Param [username, password, salt]
     * @return java.lang.String
     * @throws
     **/
    public String encryptPassword(String username, String password, String salt) {
        return new Md5Hash(username + password + salt).toHex().toString();
    }

    public static void main(String[] args)
    {
        System.out.println(new PasswordService().encryptPassword("admin", "admin123", "111111"));
        System.out.println(new PasswordService().encryptPassword("ry", "admin123", "222222"));
    }
}
