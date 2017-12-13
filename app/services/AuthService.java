package services;

import models.User;
import org.mindrot.jbcrypt.BCrypt;
import play.cache.SyncCacheApi;
import play.mvc.Http;

import javax.inject.Inject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

public class AuthService {

  private final DatabaseService databaseService;
  private final SyncCacheApi cacheApi;
  private final MessageDigest mda;
  private final String cookieHeader = "X-Auth-Token";

  public static class HashedPasswordWithSalt {
    public String hashedPassword;
    public String salt;
  }

  @Inject
  public AuthService(DatabaseService databaseService, SyncCacheApi cacheApi) throws NoSuchAlgorithmException {
    this.databaseService = databaseService;
    this.cacheApi = cacheApi;
    this.mda = MessageDigest.getInstance("SHA-512");
  }

  public Optional<Http.Cookie> login(String email, String password) {
    final Optional<User> userOption = this.databaseService.getUserOption(email);
    return userOption.map(user -> {
      if (BCrypt.checkpw(password, user.hashedPassword)) return generateCookie(user);
      else return null;
    });
  }

  private Http.Cookie generateCookie(User user) {
    final String randomPart = UUID.randomUUID().toString().toUpperCase();
    final String userPart = user.id.toString().toUpperCase();
    final String key = String.format("%s|%s", randomPart, userPart);
    final String token = Base64.getEncoder().encodeToString(mda.digest(key.getBytes()));
    final Integer duration = (int) Duration.ofHours(10).getSeconds();
    cacheApi.set(token, user, duration);
    return new Http.Cookie(cookieHeader, token, duration, "/", null, false, true, Http.Cookie.SameSite.STRICT);
  }

}
