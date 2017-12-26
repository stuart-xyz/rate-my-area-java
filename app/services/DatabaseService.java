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

  void addUser(User user) {
    Ebean.save(user);
  }

  public void addReview(Review review) {
    Ebean.save(review);
  }

  public List<DisplayedReview> listReviews() {
    final List<User> users = User.find.all();
    return users.stream().flatMap(user -> {
      final List<Review> reviewsByThisUser = user.reviews;
      return reviewsByThisUser.stream().map(reviewByThisUser -> new DisplayedReview(reviewByThisUser, user.username));
    }).collect(Collectors.toList());
  }

}
