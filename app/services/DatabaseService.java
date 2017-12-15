package services;

import io.ebean.Ebean;
import models.User;

import java.util.Optional;

class DatabaseService {

  Optional<User> getUserOption(String email) {
    return User.find.query().where().ieq("email", email).findOneOrEmpty();
  }

  void addUser(User user) {
    Ebean.save(user);
  }

}
