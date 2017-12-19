package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.With;
import services.AuthService;
import services.CustomExceptions;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Optional;


public class AuthController extends Controller {

  private final AuthService authService;

  public static class UserLoginData {
    public String email;
    public String password;
  }

  public static class UserSignupData {
    public String email;
    public String username;
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

  public Result signup() {
    JsonNode json = request().body().asJson();
    UserSignupData userSignupData = Json.fromJson(json, UserSignupData.class);
    final HashMap<String, String> responseJson = new HashMap<>();
    try {
      this.authService.signup(userSignupData.email, userSignupData.username, userSignupData.password);
    } catch (Exception e) {
      responseJson.put("error", "An error occurred creating this user");
      return internalServerError(Json.toJson(responseJson));
    }
    responseJson.put("message", "Signup was successful");
    return ok(Json.toJson(responseJson));
  }

  public Result logout() {
    final HashMap<String, String> responseJson = new HashMap<>();
    try {
      this.authService.logout(request());
    } catch (CustomExceptions.UserNotLoggedInException e) {
      responseJson.put("error", "The specified authentication token is not active");
      return badRequest(Json.toJson(responseJson));
    }
    responseJson.put("message", "Successfully logged out");
    response().discardCookie(AuthService.cookieHeader);
    return ok(Json.toJson(responseJson));
  }

  @With(UserAuthAction.class)
  public Result getUser() {
    return ok(Json.toJson(ctx().args.get("user")));
  }

}
