package com.qpp.web.core.base;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qpp.apicommons.base.AjaxResult;
import com.qpp.apicommons.utils.StringUtils;
import com.qpp.framework.util.ShiroUtils;
import com.qpp.framework.web.page.PageDomain;
import com.qpp.framework.web.page.TableDataInfo;
import com.qpp.framework.web.page.TableSupport;
import com.qpp.system.domain.SysUser;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @ClassName BaseController
 * @Description TODO web层通用数据处理
 * @Author qipengpai
 * @Date 2018/10/25 11:41
 * @Version 1.0.1
 */
public class BaseController {


    /**
     * 将前台传递过来的日期格式的字符串，自动转化为Date类型
     */
    /**
     * 由@InitBinder表示的方法，可以对WebDataBinder对象进行初始化。WebDataBinder是DataBinder的子类，用于完成由表单到JavaBean属性的绑定。
     * @InitBinder方法不能有返回值，它必须声明为void。
     * @InitBinder方法的参数通常是WebDataBinder，@InitBinder可以对WebDataBinder进行初始化。
     * */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    /**
     * 设置请求分页数据
     */
    protected void startPage() {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize))
        {
            String orderBy = pageDomain.getOrderBy();
            PageHelper.startPage(pageNum, pageSize, orderBy);
        }
    }

    /**
     * 响应请求分页数据
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected TableDataInfo getDataTable(List<?> list) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(0);
        rspData.setRows(list);
        rspData.setTotal(new PageInfo(list).getTotal());
        return rspData;
    }

    /**
     * 响应返回结果
     * 
     * @param rows 影响行数
     * @return 操作结果
     */
    protected AjaxResult toAjax(int rows)
    {
        return rows > 0 ? success() : error();
    }

    /**
     * 返回成功
     */
    public AjaxResult success()
    {
        return AjaxResult.success();
    }

    /**
     * 返回失败消息
     */
    public AjaxResult error()
    {
        return AjaxResult.error();
    }

    /**
     * 返回成功消息
     */
    public AjaxResult success(String message)
    {
        return AjaxResult.success(message);
    }

    /**
     * 返回失败消息
     */
    public AjaxResult error(String message)
    {
        return AjaxResult.error(message);
    }

    /**
     * 返回错误码消息
     */
    public AjaxResult error(int code, String message)
    {
        return AjaxResult.error(code, message);
    }

    /**
     * 页面跳转
     */
    public String redirect(String url)
    {
        return StringUtils.format("redirect:{}", url);
    }

    public SysUser getUser()
    {
        return ShiroUtils.getUser();
    }

    public void setUser(SysUser user)
    {
        ShiroUtils.setUser(user);
    }

    public Long getUserId()
    {
        return getUser().getUserId();
    }

    public String getLoginName()
    {
        return getUser().getLoginName();
    }
}
