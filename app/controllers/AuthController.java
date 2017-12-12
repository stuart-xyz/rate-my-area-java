package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.AuthService;

import javax.inject.Inject;
import java.util.Optional;


public class AuthController extends Controller {

  private final AuthService authService;

  public static class UserLoginData {
    String email;
    String password;
  }

  @Inject
  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  public Result login() {
    JsonNode json = request().body().asJson();
    UserLoginData userLoginData = Json.fromJson(json, UserLoginData.class);
    final Optional<String> cookieOption = authService.login(userLoginData.email, userLoginData.password);
    return ok(cookieOption.orElse("cookie not found"));
  }

}
