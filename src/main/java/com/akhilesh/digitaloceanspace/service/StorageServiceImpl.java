package com.akhilesh.digitaloceanspace.service;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
public class StorageServiceImpl implements StorageService {
	
	public static final String FOLDER = "/Test";
	
	@Value("${do.space.key}")
	private String doSpaceKey;

	@Value("${do.space.secret}")
	private String doSpaceSecret;

	@Value("${do.space.endpoint}")
	private String doSpaceEndpoint;

	@Value("${do.space.region}")
	private String doSpaceRegion;
	
	@Value("${do.space.bucket}")
	private String doSpaceBucket;
	
	@Autowired
	S3Client s3Client;
	
	@Override
	public boolean upload(MultipartFile multipartFile) throws Exception {
		String extension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
		String imgName = FilenameUtils.removeExtension(multipartFile.getOriginalFilename());
		String key = System.currentTimeMillis() + "." + extension;
		
		return saveImageToDOSpace(multipartFile, key);
		
	}
	
	@Override
	public boolean delete(String key) throws Exception {
		boolean isDeleted = false;
		DeleteObjectRequest request = DeleteObjectRequest.builder()
	            .bucket(doSpaceBucket)
	            .key(key)
	            .build();

	    try {
			s3Client.deleteObject(request);
			System.out.println("Object deleted from S3 bucket.");
			isDeleted = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isDeleted;
	}
	
	public boolean saveImageToDOSpace(MultipartFile multipartFile, String key) throws AwsServiceException, SdkClientException, IOException {
		boolean isStored = false;
		
     // Create PutObjectRequest
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(doSpaceBucket)
                .key(key) // Key is the name of the file in DigitalOcean Spaces
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();

        // Upload the file to DigitalOcean Spaces
        try {
            //s3Client.putObject(putObjectRequest, RequestBody.fromFile(new File(filePath)));
        	s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(multipartFile.getInputStream(), multipartFile.getSize()));
            System.out.println("File uploaded successfully!");
            isStored = true;
        } catch (S3Exception e) {
            System.err.println("Error uploading file: " + e.awsErrorDetails().errorMessage());
        }
        return isStored;
	}

}
