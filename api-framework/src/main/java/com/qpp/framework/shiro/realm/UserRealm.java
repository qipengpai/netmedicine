package com.qpp.framework.shiro.realm;

import java.util.HashSet;
import java.util.Set;

import com.qpp.framework.shiro.service.LoginService;
import com.qpp.framework.util.ShiroUtils;
import com.qpp.framework.web.exception.user.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.qpp.system.domain.SysUser;
import com.qpp.system.service.ISysMenuService;
import com.qpp.system.service.ISysRoleService;


/**
 * @ClassName UserRealm
 * @Description TODO 自定义Realm 处理登录 权限
 * @Author qipengpai
 * @Date 2018/10/25 11:41
 * @Version 1.0.1
 */
@Slf4j
public class UserRealm extends AuthorizingRealm {

    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private LoginService loginService;

    /**
     * @Author qipengpai
     * @Description //TODO 授权
     * @Date 2018/10/25 13:37
     * @Param [arg0]
     * @return org.apache.shiro.authz.AuthorizationInfo
     * @throws
     **/
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection arg0) {
        SysUser user = ShiroUtils.getUser();
        // 角色列表
        Set<String> roles = new HashSet<String>();
        // 功能列表
        Set<String> menus = new HashSet<String>();
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        // 管理员拥有所有权限
        if (user.isAdmin()) {
            info.addRole("admin");
            info.addStringPermission("*:*:*");
        } else {
            //获取当前用户角色权限
            roles = roleService.selectRoleKeys(user.getUserId());
            //根据用户ID查询菜单权限
            menus = menuService.selectPermsByUserId(user.getUserId());
            // 角色加入AuthorizationInfo认证对象
            info.setRoles(roles);
            // 权限加入AuthorizationInfo认证对象
            info.setStringPermissions(menus);
        }
        return info;
    }


    /**
     * @Author qipengpai
     * @Description //TODO  登录认证
     * @Date 2018/10/25 14:14
     * @Param [token]
     * @return org.apache.shiro.authc.AuthenticationInfo
     * @throws
     **/
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        String username = upToken.getUsername();
        String password = "";
        if (upToken.getPassword() != null) {
            password = new String(upToken.getPassword());
        }

        SysUser user = null;
        try {
            user = loginService.login(username, password);
        }
        catch (CaptchaException e) {
            throw new AuthenticationException(e.getMessage(), e);
        }
        catch (UserNotExistsException e) {
            throw new UnknownAccountException(e.getMessage(), e);
        }
        catch (UserPasswordNotMatchException e) {
            throw new IncorrectCredentialsException(e.getMessage(), e);
        }
        catch (UserPasswordRetryLimitExceedException e)
        {
            throw new ExcessiveAttemptsException(e.getMessage(), e);
        }
        catch (UserBlockedException e) {
            throw new LockedAccountException(e.getMessage(), e);
        }
        catch (RoleBlockedException e) {
            throw new LockedAccountException(e.getMessage(), e);
        }
        catch (Exception e) {
            log.info("对用户[" + username + "]进行登录验证..验证未通过{}", e.getMessage());
            throw new AuthenticationException(e.getMessage(), e);
        }
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user, password, getName());
        return info;
    }

    /**
     * @Author qipengpai
     * @Description //TODO 清理缓存权限
     * @Date 2018/10/25 14:15
     * @Param []
     * @return void
     * @throws
     **/
    public void clearCachedAuthorizationInfo() {
        this.clearCachedAuthorizationInfo(SecurityUtils.getSubject().getPrincipals());
    }
}
