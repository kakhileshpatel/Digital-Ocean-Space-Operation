package com.akhilesh.digitaloceanspace.service;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.ObjectAttributes;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.model.S3ResponseMetadata;

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
                .contentType("image/png")
                .contentDisposition("inline")
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();

        // Upload the file to DigitalOcean Spaces
        try {
            //s3Client.putObject(putObjectRequest, RequestBody.fromFile(new File(filePath)));
        	PutObjectResponse response = s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(multipartFile.getInputStream(), multipartFile.getSize()));
            S3ResponseMetadata metadata = response.responseMetadata();
            System.out.println(response.sdkHttpResponse().isSuccessful()+":"+response.sdkHttpResponse().statusCode()+":"+response.sdkHttpResponse().statusText());
        	System.out.println("File uploaded successfully!");
            isStored = true;
        } catch (S3Exception e) {
            System.err.println("Error uploading file: " + e.awsErrorDetails().errorMessage());
        }
        return isStored;
	}
	
	@Override
	public boolean isObjectExist(String key) throws Exception {
		boolean isObjectExist = false;
		
		try {
            ListObjectsV2Request request = ListObjectsV2Request.builder()
                    .bucket(doSpaceBucket)
                    .prefix("Test")
                    //.delimiter("/")
                    .build();

            ListObjectsV2Response response = s3Client.listObjectsV2(request);
            List<S3Object> objects = response.contents();

            for (S3Object object : objects) {
                System.out.println("Object Key: " + object.key());
                System.out.println("Object Size: " + object.size());
                // Access other properties of the object as needed
                System.out.println("------------------------------------");
            }

        } catch (S3Exception e) {
            // Handle exception
            System.err.println("Error: " + e.awsErrorDetails().errorMessage());
        } finally {
           // s3Client.close();
        }
		
		try {
			GetUrlRequest getUrlRequest = GetUrlRequest.builder()
                    .bucket(doSpaceBucket)
                    .key(key)
                    .build();

            URL objectUrl = s3Client.utilities().getUrl(getUrlRequest);
            System.out.println("Object URL: " + objectUrl.toString());

        } catch (S3Exception e) {
            // Handle exception
            System.err.println("Error: " + e.awsErrorDetails().errorMessage());
        } finally {
            //s3Client.close();
        }
		
		 try {
	            ListObjectsRequest request = ListObjectsRequest.builder()
	                    .bucket(doSpaceBucket)
	                    .build();

	            ListObjectsResponse response = s3Client.listObjects(request);
	            List<S3Object> objects = response.contents();

	            for (S3Object object : objects) {
	                System.out.println("Object Key: " + object.key());
	                System.out.println("Object Size: " + object.size());
	                // Access other properties of the object as needed
	                System.out.println("------------------------------------");
	            }

	        } catch (S3Exception e) {
	            // Handle exception
	            System.err.println("Error: " + e.awsErrorDetails().errorMessage());
	        } finally {
	            //s3Client.close();
	        }
		
		 try {
	            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
	                    .bucket(doSpaceBucket)
	                    .key(key)
	                    .build();

	            ResponseInputStream<GetObjectResponse> responseInputStream = s3Client.getObject(getObjectRequest);
	            GetObjectResponse getObjectResponse = responseInputStream.response();
	            Map<String, String> objectMetadata = getObjectResponse.metadata();

	            for(Entry<String, String> entry : objectMetadata.entrySet()) {
	            	System.out.println(entry.getKey() +" : "+ entry.getValue());
	            }

	        } catch (S3Exception e) {
	            // Handle exception
	            System.err.println("Error: " + e.awsErrorDetails().errorMessage());
	        } finally {
	            //s3Client.close();
	        }
		
		HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
				.bucket(doSpaceBucket)
				.key(key)
				.build();
		
		 try {
	            HeadObjectResponse response = s3Client.headObject(headObjectRequest);
	            isObjectExist = true;
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
        return isObjectExist;
	}

}
