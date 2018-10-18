package com.qpp.system.domain;

import com.qpp.apicommons.annotation.Excel;
import com.qpp.apicommons.base.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import java.util.Date;


/**
 * 系统访问记录表 sys_logininfor
 * 
 * @author ruoyi
 */
@Data
public class SysLogininfor extends BaseEntity {
    private static final long serialVersionUID = 1L;
    
    /** ID */
    @Excel(name = "序号")
    private Long infoId;
    
    /** 用户账号 */
    @Excel(name = "用户账号")
    private String loginName;
    
    /** 登录状态 0成功 1失败 */
    @Excel(name = "登录状态")
    private String status;
    
    /** 登录IP地址 */
    @Excel(name = "登录地址")
    private String ipaddr;
    
    /** 登录地点 */
    @Excel(name = "登录地点")
    private String loginLocation;
    
    /** 浏览器类型 */
    @Excel(name = "浏览器")
    private String browser;
    
    /** 操作系统 */
    @Excel(name = "操作系统 ")
    private String os;
    
    /** 提示消息 */
    @Excel(name = "提示消息")
    private String msg;
    
    /** 访问时间 */
    @Excel(name = "访问时间")
    private Date loginTime;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("infoId", getInfoId())
            .append("loginName", getLoginName())
            .append("ipaddr", getIpaddr())
            .append("loginLocation", getLoginLocation())
            .append("browser", getBrowser())
            .append("os", getOs())
            .append("status", getStatus())
            .append("msg", getMsg())
            .append("loginTime", getLoginTime())
            .toString();
    }
}