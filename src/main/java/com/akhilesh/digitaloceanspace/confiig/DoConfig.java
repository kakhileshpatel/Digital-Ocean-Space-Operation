package com.akhilesh.digitaloceanspace.confiig;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class DoConfig {

	@Value("${do.space.key}")
	private String doSpaceKey;

	@Value("${do.space.secret}")
	private String doSpaceSecret;

	@Value("${do.space.endpoint}")
	private String doSpaceEndpoint;

	@Value("${do.space.region}")
	private String doSpaceRegion;

	@Bean
	public S3Client s3Client() {
		//String accessKey = "DO00K94UMWWG9ZNFEN6H";
       // String secretKey = "VUrjqUkM+FwEKYMckLKXkm8WfbIRYR1f87VamHImdVM";

        // Set your DigitalOcean Spaces endpoint URL and region
        //String endpoint = "https://nyc3.digitaloceanspaces.com";
        //String region = "nyc3";

        

        // Create S3 client
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(doSpaceKey, doSpaceSecret);
        
        S3Client s3Client = S3Client.builder()
        .region(Region.of(doSpaceRegion))
        .endpointOverride(URI.create(doSpaceEndpoint))
        .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
        .build();
		return s3Client;
	}

}