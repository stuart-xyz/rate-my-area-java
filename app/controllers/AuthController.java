package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.ebean.DuplicateKeyException;
import org.postgresql.util.PSQLException;
import play.data.Form;
import play.data.FormFactory;
import play.data.validation.Constraints;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;


public class AuthController extends Controller {

  private final HttpExecutionContext httpExecutionContext;
  private final AuthService authService;
  private final FormFactory formFactory;

  public static class UserLoginData {
    @Constraints.Required
    @Constraints.Email
    public String email;

    @Constraints.Required
    @Constraints.MinLength(1)
    public String password;
  }

  public static class UserSignupData {
    @Constraints.Required
    @Constraints.Email
    public String email;

    @Constraints.Required
    @Constraints.MinLength(1)
    public String username;

    @Constraints.Required
    @Constraints.MinLength(1)
    public String password;
  }

  @Inject
  public AuthController(HttpExecutionContext httpExecutionContext, AuthService authService, FormFactory formFactory) {
    this.httpExecutionContext = httpExecutionContext;
    this.authService = authService;
    this.formFactory = formFactory;
  }

  public CompletionStage<Result> login() {
    final HashMap<String, String> responseJson = new HashMap<>();
    final Optional<JsonNode> jsonOption = Optional.ofNullable(request().body().asJson());
    if (jsonOption.isPresent()) {
      final Form<UserLoginData> userLoginForm = formFactory.form(UserLoginData.class).bind(jsonOption.get());
      if (userLoginForm.hasErrors()) {
        responseJson.put("error", "Expected email and password");
        return CompletableFuture.completedFuture(badRequest(Json.toJson(responseJson)));
      }

      return CompletableFuture.supplyAsync(() -> {
        final Optional<Http.Cookie> cookieOption;
        try {
          cookieOption = authService.login(userLoginForm.get().email, userLoginForm.get().password);
        } catch (Exception e) {
          responseJson.put("Error", "Unexpected internal error occurred");
          return internalServerError(Json.toJson(responseJson));
        }

        if (cookieOption.isPresent()) {
          responseJson.put("message", "Log in successful");
          response().setCookie(cookieOption.get());
          return ok(Json.toJson(responseJson));
        } else {
          responseJson.put("error", "Invalid login credentials provided");
          return unauthorized(Json.toJson(responseJson));
        }
      }, this.httpExecutionContext.current());

    } else {
      responseJson.put("error", "Expected JSON body");
      return CompletableFuture.completedFuture(badRequest(Json.toJson(responseJson)));
    }
  }

  public CompletionStage<Result> signup() {
    final HashMap<String, String> responseJson = new HashMap<>();
    final Optional<JsonNode> jsonOption = Optional.ofNullable(request().body().asJson());
    if (jsonOption.isPresent()) {
      final Form<UserSignupData> userSignupForm = formFactory.form(UserSignupData.class).bind(jsonOption.get());
      if (userSignupForm.hasErrors()) {
        responseJson.put("error", "Invalid email, username or password");
        return CompletableFuture.completedFuture(badRequest(Json.toJson(responseJson)));
      }

      return CompletableFuture.supplyAsync(() -> {
        try {
          this.authService.signup(userSignupForm.get().email, userSignupForm.get().username, userSignupForm.get().password);
        } catch (DuplicateKeyException e) {
          responseJson.put("error", ((PSQLException) e.getCause()).getServerErrorMessage().getConstraint());
          return status(CONFLICT, Json.toJson(responseJson));
        } catch (Exception e) {
          responseJson.put("error", "Unexpected internal error occurred");
          return internalServerError(Json.toJson(responseJson));
        }

        responseJson.put("message", "Signup was successful");
        return ok(Json.toJson(responseJson));
      }, this.httpExecutionContext.current());

    } else {
      responseJson.put("error", "Expected JSON body");
      return CompletableFuture.completedFuture(badRequest(Json.toJson(responseJson)));
    }
  }

  @With(UserAuthAction.class)
  public Result logout() {
    final HashMap<String, String> responseJson = new HashMap<>();
    try {
      this.authService.logout(request());
    } catch (CustomExceptions.UserNotLoggedInException e) {
      responseJson.put("error", "The specified authentication token is not active");
      return badRequest(Json.toJson(responseJson));
    } catch (CustomExceptions.CookieNotPresentException e) {
      responseJson.put("Error", "Unauthorised");
      return unauthorized(Json.toJson(responseJson));
    } catch (Exception e) {
      responseJson.put("Error", "Unexpected internal error occurred");
      return internalServerError(Json.toJson(responseJson));
    }

    responseJson.put("message", "Successfully logged out");
    response().discardCookie(AuthService.cookieHeader);
    return ok(Json.toJson(responseJson));
  }

  @With(UserAuthAction.class)
  public Result getUser() {
    final ObjectNode userWithoutPassword = (ObjectNode) Json.toJson(ctx().args.get("user"));
    userWithoutPassword.remove(Arrays.asList("hashedPassword", "salt"));
    return ok(userWithoutPassword);
  }

}
