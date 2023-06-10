package com.akhilesh.digitaloceanspace.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.akhilesh.digitaloceanspace.service.StorageService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/dospace")
public class ContentController<T> {

	@Autowired
	private StorageService storageService;

	@PostMapping("/upload")
	public boolean uploadContent(@RequestParam("file") MultipartFile file) throws Exception {
		boolean result = false;
		try {
			result = storageService.upload(file);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Internal server error.");
		}
		return result;
	}
	
	@DeleteMapping("/delete")
	public boolean deleteContent(@RequestParam("fileName") String fileName) throws Exception {
		boolean result = false;
		try {
			result = storageService.delete(fileName);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Internal server error.");
		}
		return result;
	}

}
