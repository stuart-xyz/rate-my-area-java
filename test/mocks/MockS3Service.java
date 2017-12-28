package mocks;

import services.AbstractS3Service;

import java.io.File;


public class MockS3Service implements AbstractS3Service {

  @Override
  public String upload(File file, String filename, Long userId) {
    return "test";
  }

  @Override
  public void delete(String url) {}

}
