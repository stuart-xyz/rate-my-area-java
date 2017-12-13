package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import services.AuthService;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Optional;


public class AuthController extends Controller {

  private final AuthService authService;

  public static class UserLoginData {
    public String email;
    public String password;
  }

  @Inject
  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  public Result login() {
    JsonNode json = request().body().asJson();
    UserLoginData userLoginData = Json.fromJson(json, UserLoginData.class);
    final Optional<Http.Cookie> cookieOption = authService.login(userLoginData.email, userLoginData.password);
    final HashMap<String, String> responseJson = new HashMap<>();
    if (cookieOption.isPresent()) {
      responseJson.put("message", "Log in successful");
      return ok(Json.toJson(responseJson));
    } else {
      responseJson.put("error", "Invalid login credentials provided");
      return unauthorized(Json.toJson(responseJson));
    }
  }

}
