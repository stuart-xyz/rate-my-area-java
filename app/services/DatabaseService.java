package services;

import models.User;

import java.util.Optional;

class DatabaseService {

  Optional<User> getUserOption(String email) {
    return User.find.query().where().ieq("email", email).findOneOrEmpty();
  }

}
