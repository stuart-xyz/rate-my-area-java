package services;

import java.io.File;


public interface AbstractS3Service {

  String upload(File file, String filename, Long userId);
  void delete(String url);

}
