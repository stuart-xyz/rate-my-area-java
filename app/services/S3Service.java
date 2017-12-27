package services;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;
import play.api.Configuration;
import scala.Option;

import javax.inject.Inject;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;


public class S3Service {

  private final String configuredBucketName;
  private final AmazonS3 s3Client;

  @Inject
  public S3Service(Configuration appConfig) {
    this.configuredBucketName = appConfig.getString("s3-bucket-name", Option.empty()).get();
    final String regionName = appConfig.getString("s3-region", Option.empty()).get();
    final String accessKey = appConfig.getString("s3-access-key", Option.empty()).get();
    final String secretKey = appConfig.getString("s3-secret-key", Option.empty()).get();
    final BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
    this.s3Client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(regionName).build();
  }

  public String upload(File file, String filename, Long userId) {
    final String key = String.format("%d|%s", userId, filename);
    this.s3Client.putObject(new PutObjectRequest(this.configuredBucketName, key, file));
    return this.s3Client.getUrl(this.configuredBucketName, key).toString();
  }

  public void delete(String url) {
    try {
      this.s3Client.deleteObject(this.configuredBucketName, new URL(url).getFile());
    } catch (MalformedURLException e) {
      throw new RuntimeException("Invalid URL");
    }
  }

}
