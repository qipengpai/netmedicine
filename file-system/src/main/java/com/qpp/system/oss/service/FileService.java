package com.qpp.system.oss.service;



import com.qpp.apicommons.base.PageResult;
import com.qpp.system.oss.model.FileInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @author 作者 owen E-mail: 624191343@qq.com
 * @version 创建时间：2017年11月12日 上午22:57:51
 * 文件service 目前仅支持阿里云oss,七牛云
*/
public interface FileService {

	FileInfo upload(MultipartFile file) throws Exception;

	void delete(FileInfo fileInfo);
	
	FileInfo getById(String id);
	
	PageResult<FileInfo> findList(Map<String, Object> params);

}
