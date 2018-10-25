package com.qpp.framework.web.exception.user;


/**
 * @ClassName UserRealm
 * @Description TODO 用户错误最大次数异常类
 * @Author qipengpai
 * @Date 2018/10/25 14：56
 * @Version 1.0.1
 */
public class UserPasswordRetryLimitExceedException extends UserException {
    private static final long serialVersionUID = 1L;

    public UserPasswordRetryLimitExceedException(int retryLimitCount) {
        super("user.password.retry.limit.exceed", new Object[] { retryLimitCount });
    }
}
