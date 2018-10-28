package com.qpp.apirest.oss.config;



import com.qpp.system.oss.model.FileType;
import com.qpp.system.oss.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;


/**
 * @author 作者 owen E-mail: 624191343@qq.com
 * @version 创建时间：2017年11月12日 上午22:57:51
 * FileService工厂<br>
 * 将各个实现类放入map
*/
@Configuration
public class OssServiceFactory {

	private Map<FileType, FileService> map = new HashMap<>();


	@Autowired(required=false)
	private FileService aliyunOssServiceImpl;

	@Autowired(required=false)
	private FileService qiniuOssServiceImpl;
	

	@PostConstruct
	public void init() {
		map.put(FileType.ALIYUN, aliyunOssServiceImpl);
		map.put(FileType.QINIU ,  qiniuOssServiceImpl);
	}

	public FileService getFileService(String fileType) {

		return map.get(FileType.valueOf(fileType));
	}
}
