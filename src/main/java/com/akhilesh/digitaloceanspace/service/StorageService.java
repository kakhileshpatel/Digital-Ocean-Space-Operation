package com.akhilesh.digitaloceanspace.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
	
	public boolean upload(MultipartFile multipartFile) throws Exception;

	boolean delete(String key) throws Exception;

	boolean isObjectExist(String key) throws Exception;

}
