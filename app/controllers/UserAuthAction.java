package controllers;

import models.User;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import services.AuthService;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;


public class UserAuthAction extends play.mvc.Action.Simple {

  private final AuthService authService;

  @Inject
  public UserAuthAction(AuthService authService) {
    this.authService = authService;
  }

  public CompletionStage<Result> call(Http.Context ctx) {
    final Optional<User> userOption = this.authService.checkCookie(ctx.request());
    if (userOption.isPresent()) {
      ctx.args.put("user", userOption.get());
      return delegate.call(ctx);
    } else {
      final HashMap<String, String> responseJson = new HashMap<>();
      responseJson.put("error", "unauthorised");
      return CompletableFuture.completedFuture(unauthorized(Json.toJson(responseJson)));
    }
  }

}
