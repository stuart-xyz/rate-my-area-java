package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.DisplayedReview;
import models.ImageUrl;
import models.Review;
import models.User;
import play.data.Form;
import play.data.FormFactory;
import play.data.validation.Constraints;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
import services.AbstractS3Service;
import services.CustomExceptions;
import services.DatabaseService;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;


public class ReviewController extends Controller {

  private final HttpExecutionContext httpExecutionContext;
  private final FormFactory formFactory;
  private final DatabaseService databaseService;
  private final AbstractS3Service s3Service;

  public static class ReviewFormData {
    @Constraints.Required
    @Constraints.MinLength(1)
    public String title;

    @Constraints.Required
    @Constraints.MinLength(1)
    public String areaName;

    @Constraints.Required
    @Constraints.MinLength(1)
    public String description;

    @Constraints.Required
    public List<String> imageUrls;
  }

  public static class ReviewEditData {
    @Nullable
    @Constraints.MinLength(1)
    public String title;

    @Nullable
    @Constraints.MinLength(1)
    public String areaName;

    @Nullable
    @Constraints.MinLength(1)
    public String description;
  }

  @Inject
  public ReviewController(HttpExecutionContext httpExecutionContext, FormFactory formFactory, DatabaseService databaseService, AbstractS3Service s3Service) {
    this.httpExecutionContext = httpExecutionContext;
    this.formFactory = formFactory;
    this.databaseService = databaseService;
    this.s3Service = s3Service;
  }

  @With(UserAuthAction.class)
  public CompletionStage<Result> create() {
    final HashMap<String, String> responseJson = new HashMap<>();
    Optional<JsonNode> jsonOption = Optional.ofNullable(request().body().asJson());
    if (jsonOption.isPresent()) {
      final Form<ReviewFormData> reviewForm = formFactory.form(ReviewFormData.class).bind(jsonOption.get());
      if (reviewForm.hasErrors()) {
        responseJson.put("Error", "Invalid data supplied");
        return CompletableFuture.completedFuture(badRequest(Json.toJson(responseJson)));
      }

      final User user = (User) ctx().args.get("user");
      final ReviewFormData reviewFormData = reviewForm.get();
      final Review review = new Review(reviewFormData.title, reviewFormData.areaName, reviewFormData.description);
      reviewFormData.imageUrls.forEach(url -> review.imageUrls.add(new ImageUrl(url)));
      user.reviews.add(review);

      return CompletableFuture.supplyAsync(() -> {
        try {
          user.save();
        } catch(Exception e) {
          responseJson.put("Error", "Unexpected internal error occurred");
          return internalServerError(Json.toJson(responseJson));
        }

        responseJson.put("message", "Review added successfully");
        return ok(Json.toJson(responseJson));
      }, this.httpExecutionContext.current());

    } else {
      responseJson.put("Error", "Expected JSON body");
      return CompletableFuture.completedFuture(badRequest(Json.toJson(responseJson)));
    }
  }

  @With(UserAuthAction.class)
  public CompletionStage<Result> list() {
    return CompletableFuture.supplyAsync(() -> {
      final HashMap<String, String> responseJson = new HashMap<>();
      final List<DisplayedReview> reviews;
      try {
        reviews = this.databaseService.listReviews();
      } catch (Exception e) {
        responseJson.put("Error", "Unexpected internal error occurred");
        return internalServerError(Json.toJson(responseJson));
      }

      return ok(Json.toJson(reviews));
    }, this.httpExecutionContext.current());
  }

  private boolean userOwnsReview(User user, Long reviewId) throws CustomExceptions.ReviewNotFoundException {
    final Review currentReview = this.databaseService.getReview(reviewId);
    return user.id.equals(currentReview.user.id);
  }

  @With(UserAuthAction.class)
  public CompletionStage<Result> edit(Long id) {
    return CompletableFuture.supplyAsync(() -> {
      final User user = (User) ctx().args.get("user");
      final HashMap<String, String> responseJson = new HashMap<>();
      try {
        if (!userOwnsReview(user, id)) {
          responseJson.put("error", "Not authorised to edit this review");
          return unauthorized(Json.toJson(responseJson));
        }
      } catch (CustomExceptions.ReviewNotFoundException e) {
        responseJson.put("error", "Review does not exist");
        return notFound(Json.toJson(responseJson));
      } catch (Exception e) {
        responseJson.put("error", "Unexpected internal error occurred");
        return internalServerError(Json.toJson(responseJson));
      }

      Optional<JsonNode> jsonOption = Optional.ofNullable(request().body().asJson());
      if (jsonOption.isPresent()) {
        final Form<ReviewEditData> reviewEditForm = formFactory.form(ReviewEditData.class).bind(jsonOption.get());
        if (reviewEditForm.hasErrors()) {
          responseJson.put("Error", "Invalid data supplied");
          return badRequest(Json.toJson(responseJson));
        }

        final ReviewEditData reviewEditData = reviewEditForm.get();
        try {
          this.databaseService.updateReview(id, new Review(reviewEditData.title, reviewEditData.areaName, reviewEditData.description));
        } catch (CustomExceptions.ReviewNotFoundException e) {
          responseJson.put("error", "Review does not exist");
          return notFound(Json.toJson(responseJson));
        } catch (Exception e) {
          responseJson.put("error", "Unexpected internal error occurred");
          return internalServerError(Json.toJson(responseJson));
        }

        responseJson.put("message", "Review updated successfully");
        return ok(Json.toJson(responseJson));

      } else {
        responseJson.put("Error", "Expected JSON body");
        return badRequest(Json.toJson(responseJson));
      }
    }, this.httpExecutionContext.current());
  }

  @With(UserAuthAction.class)
  public CompletionStage<Result> delete(Long id) {
    return CompletableFuture.supplyAsync(() -> {
      final User user = (User) ctx().args.get("user");
      final HashMap<String, String> responseJson = new HashMap<>();
      try {
        if (!userOwnsReview(user, id)) {
          responseJson.put("error", "Not authorised to edit this review");
          return unauthorized(Json.toJson(responseJson));
        }
      } catch (CustomExceptions.ReviewNotFoundException e) {
        responseJson.put("error", "Review does not exist");
        return notFound(Json.toJson(responseJson));
      } catch (Exception e) {
        responseJson.put("error", "Unexpected internal error occurred");
        return internalServerError(Json.toJson(responseJson));
      }

      final Review currentReview;
      try {
        currentReview = this.databaseService.getReview(id);
        this.databaseService.deleteReview(id);
      } catch (CustomExceptions.ReviewNotFoundException e) {
        responseJson.put("error", "Review does not exist");
        return notFound(Json.toJson(responseJson));
      } catch (Exception e) {
        responseJson.put("error", "Unexpected internal error occurred");
        return internalServerError(Json.toJson(responseJson));
      }
      currentReview.imageUrls.forEach(imageUrl -> this.s3Service.delete(imageUrl.url));

      responseJson.put("message", "Review deleted successfully");
      return ok(Json.toJson(responseJson));
    }, this.httpExecutionContext.current());
  }

}
