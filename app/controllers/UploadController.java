package controllers;

import models.User;
import play.libs.Files;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import services.S3Service;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class UploadController extends Controller {

  private final S3Service s3Service;

  @Inject
  public UploadController(S3Service s3Service) {
    this.s3Service = s3Service;
  }

  public Result upload() {
    final User user = (User) ctx().args.get("user");
    final Http.MultipartFormData<Files.TemporaryFile> formData = request().body().asMultipartFormData();
    final List<String> urls = formData.getFiles().stream().map(file -> {
      String filename = UUID.randomUUID().toString();
      return this.s3Service.upload(file.getFile().path().toFile(), filename, user.id);
    }).collect(Collectors.toList());

    final HashMap<String, List<String>> responseJson = new HashMap<>();
    responseJson.put("urls", urls);
    return ok(Json.toJson(responseJson));
  }

}
