package com.qpp.framework.shiro.web.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.qpp.apicommons.constant.Constants;
import com.qpp.apicommons.utils.StringUtils;
import com.qpp.framework.manager.AsyncManager;
import com.qpp.framework.manager.factory.AsyncFactory;
import com.qpp.framework.util.MessageUtils;
import com.qpp.framework.util.ShiroUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.qpp.system.domain.SysUser;


/**
 * @ClassName OnlineSessionFilter
 * @Description TODO 退出过滤器
 * @Author qipengpai
 * @Date 2018/10/25 11:41
 * @Version 1.0.1
 */
@Slf4j
@Data
public class LogoutFilter extends org.apache.shiro.web.filter.authc.LogoutFilter {

    /**
     * 退出后重定向的地址
     */
    private String loginUrl;


    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        try {
            Subject subject = getSubject(request, response);
            String redirectUrl = getRedirectUrl(request, response, subject);
            try {
                SysUser user = ShiroUtils.getUser();
                if (StringUtils.isNotNull(user)) {
                    String loginName = user.getLoginName();
                    // 记录用户退出日志
                    AsyncManager.me().execute(AsyncFactory.recordLogininfor(loginName, Constants.LOGOUT, MessageUtils.message("user.logout.success")));
                }
                // 退出登录
                subject.logout();
            }
            catch (SessionException ise) {
                log.error("logout fail.", ise);
            }
            issueRedirect(request, response, redirectUrl);
        }
        catch (Exception e) {
            log.error("Encountered session exception during logout.  This can generally safely be ignored.", e);
        }
        return false;
    }

    /**
     * @Author qipengpai
     * @Description //TODO 退出跳转URL
     * @Date 2018/10/25 13:30
     * @Param [request, response, subject] 
     * @return java.lang.String
     * @throws 
     **/
    @Override
    protected String getRedirectUrl(ServletRequest request, ServletResponse response, Subject subject) {
        String url = getLoginUrl();
        if (StringUtils.isNotEmpty(url)) {
            return url;
        }
        return super.getRedirectUrl(request, response, subject);
    }
}
