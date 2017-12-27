package services;

import io.ebean.Ebean;
import models.DisplayedReview;
import models.Review;
import models.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class DatabaseService {

  Optional<User> getUserOption(String email) {
    return User.find.query().where().ieq("email", email).findOneOrEmpty();
  }

  public Review getReview(Long id) throws CustomExceptions.ReviewNotFoundException {
    final Optional<Review> reviewOption = Review.find.query().where().eq("id", id).findOneOrEmpty();
    if (reviewOption.isPresent()) {
      return reviewOption.get();
    } else {
      throw new CustomExceptions.ReviewNotFoundException();
    }
  }

  public List<DisplayedReview> listReviews() {
    final List<User> users = User.find.all();
    return users.stream().flatMap(user -> {
      final List<Review> reviewsByThisUser = user.reviews;
      return reviewsByThisUser.stream().map(reviewByThisUser -> new DisplayedReview(reviewByThisUser, user.username));
    }).collect(Collectors.toList());
  }

  public void updateReview(Long id, Review updatedReview) throws CustomExceptions.ReviewNotFoundException {
    Optional<Review> reviewOption = Optional.ofNullable(Ebean.find(Review.class, id));
    if (reviewOption.isPresent()) {
      Review review = reviewOption.get();
      Optional.ofNullable(updatedReview.getTitle()).ifPresent(review::setTitle);
      Optional.ofNullable(updatedReview.getAreaName()).ifPresent(review::setAreaName);
      Optional.ofNullable(updatedReview.getDescription()).ifPresent(review::setDescription);
      Ebean.save(review);
    } else {
      throw new CustomExceptions.ReviewNotFoundException();
    }
  }

  public void deleteReview(Long id) throws CustomExceptions.ReviewNotFoundException {
    Optional<Review> reviewOption = Optional.ofNullable(Ebean.find(Review.class, id));
    if (reviewOption.isPresent()) {
      reviewOption.get().delete();
    } else {
      throw new CustomExceptions.ReviewNotFoundException();
    }
  }

}
