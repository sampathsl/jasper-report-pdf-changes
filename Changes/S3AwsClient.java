package src.com.intervest.hercules;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class S3AwsClient {
	
	//amazon s3 credintials and client creation	
	public static final AWSCredentials CREDINTIALS = new BasicAWSCredentials(
				"",
				"");
	
	//public static final AWSCredentials CREDINTIALS = new ProfileCredentialsProvider().getCredentials();
		
	public static final AmazonS3 client = new AmazonS3Client(CREDINTIALS);		
	public static final String bucketName = "live-tim-p-files";
	
	public void createFile(String fileName, String content) {
		
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(content.getBytes().length);
		InputStream isContent = new ByteArrayInputStream(content.getBytes());
		try{
			
			PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, isContent, metadata);
			client.putObject(putObjectRequest);
			
		} catch(AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to Amazon S3, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        }
		
	}
	
	public String readFile(String fileName) throws IOException {	
		
		S3Object object = null;
		
		try{
			System.out.println("bucketName"+bucketName);
			object = client.getObject(new GetObjectRequest(bucketName, fileName));		
			
		} catch(AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to Amazon S3, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        }
		
		BufferedReader br = new BufferedReader(new InputStreamReader(object.getObjectContent()));
		String line;
		StringBuilder sb = new StringBuilder();

		while ((line = br.readLine()) != null) {
			sb.append(line.trim());
		}

		return sb.toString();	
		
	}
	
	
	public void deleteFolder(String folderName) {
		
		try{
		
			List<S3ObjectSummary> fileList = client.listObjects(bucketName, folderName).getObjectSummaries();
			for (S3ObjectSummary file : fileList) {
				client.deleteObject(bucketName, file.getKey());
			}
			client.deleteObject(bucketName, folderName);
		
		} catch(AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to Amazon S3, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        }
		
	}
	
	
	public void deleteFile(String fileName) {
		
		try{
			
			client.deleteObject(bucketName, fileName);
		
		} catch(AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to Amazon S3, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        }
	}

}
