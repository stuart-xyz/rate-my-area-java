package services;

import models.User;
import org.mindrot.jbcrypt.BCrypt;

import javax.inject.Inject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

public class AuthService {

  private final DatabaseService databaseService;
  private final MessageDigest mda;
  private final String cookieHeader = "X-Auth-Token";

  public static class HashedPasswordWithSalt {
    public String hashedPassword;
    public String salt;
  }

  @Inject
  public AuthService(DatabaseService databaseService) throws NoSuchAlgorithmException {
    this.databaseService = databaseService;
    this.mda = MessageDigest.getInstance("SHA-512");
  }

  public Optional<String> login(String email, String password) {
    final Optional<User> userOption = this.databaseService.getUserOption(email);
    return userOption.map(user -> {
      if (BCrypt.checkpw(password, user.hashedPassword)) return "cookie";
      else return null;
    });
  }

}
