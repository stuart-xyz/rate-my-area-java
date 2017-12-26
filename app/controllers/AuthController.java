package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.data.Form;
import play.data.FormFactory;
import play.data.validation.Constraints;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.With;
import services.AuthService;
import services.CustomExceptions;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;


public class AuthController extends Controller {

  private final AuthService authService;
  private final FormFactory formFactory;

  public static class UserLoginData {
    private String email;
    public String password;

    @Constraints.Email
    public String getEmail() {
      return this.email;
    }

    public void setEmail(String email) {
      this.email = email;
    }
  }

  public static class UserSignupData {
    public String email;
    public String username;
    public String password;
  }

  @Inject
  public AuthController(AuthService authService, FormFactory formFactory) {
    this.authService = authService;
    this.formFactory = formFactory;
  }

  public Result login() {
    final HashMap<String, String> responseJson = new HashMap<>();
    Optional<JsonNode> jsonOption = Optional.ofNullable(request().body().asJson());
    if (jsonOption.isPresent()) {
      Form<UserLoginData> userLoginForm = formFactory.form(UserLoginData.class).bind(jsonOption.get());
      if (userLoginForm.hasErrors()) {
        responseJson.put("error", "Expected email and password");
        return badRequest(Json.toJson(responseJson));
      } else {
        final Optional<Http.Cookie> cookieOption = authService.login(userLoginForm.get().email, userLoginForm.get().password);
        if (cookieOption.isPresent()) {
          responseJson.put("message", "Log in successful");
          response().setCookie(cookieOption.get());
          return ok(Json.toJson(responseJson));
        } else {
          responseJson.put("error", "Invalid login credentials provided");
          return unauthorized(Json.toJson(responseJson));
        }
      }
    } else {
      responseJson.put("error", "Expected JSON body");
      return badRequest(Json.toJson(responseJson));
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
    ObjectNode userWithoutPassword = (ObjectNode) Json.toJson(ctx().args.get("user"));
    userWithoutPassword.remove(Arrays.asList("hashedPassword", "salt"));
    return ok(userWithoutPassword);
  }

}
