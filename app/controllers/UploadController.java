package controllers;

import models.User;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.With;
import services.AbstractS3Service;

import javax.inject.Inject;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

public class UploadController extends Controller {

  private final HttpExecutionContext httpExecutionContext;
  private final AbstractS3Service s3Service;

  @Inject
  public UploadController(HttpExecutionContext httpExecutionContext, AbstractS3Service s3Service) {
    this.httpExecutionContext = httpExecutionContext;
    this.s3Service = s3Service;
  }

  @With(UserAuthAction.class)
  public CompletionStage<Result> upload() {
    final User user = (User) ctx().args.get("user");
    final Http.MultipartFormData<File> formData = request().body().asMultipartFormData();

    return CompletableFuture.supplyAsync(() -> {
      final List<String> urls = formData.getFiles().stream().map(file -> {
        String filename = UUID.randomUUID().toString();
        return this.s3Service.upload(file.getFile(), filename, user.id);
      }).collect(Collectors.toList());

      final HashMap<String, List<String>> responseJson = new HashMap<>();
      responseJson.put("urls", urls);
      return ok(Json.toJson(responseJson));
    }, this.httpExecutionContext.current());
  }

}
