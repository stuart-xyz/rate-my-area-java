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
    public HashedPasswordWithSalt(String hashedPassword, String salt) {
      this.hashedPassword = hashedPassword;
      this.salt = salt;
    }
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

  public void logout(Http.RequestHeader header) throws Exception {
    final Http.Cookie cookie = header.cookies().get(cookieHeader);
    if (cookie == null) throw new Exception();
    else this.cacheApi.remove(cookie.value());
  }

  public void signup(String email, String username, String password) {
    final HashedPasswordWithSalt hashedPasswordWithSalt = hashPasswordWithSalt(password);
    final User user = new User(email, hashedPasswordWithSalt.hashedPassword, hashedPasswordWithSalt.salt, username);
    databaseService.addUser(user);
  }

  private HashedPasswordWithSalt hashPasswordWithSalt(String password) {
    final String salt = BCrypt.gensalt();
    final String hashedPassword = BCrypt.hashpw(password, salt);
    return new HashedPasswordWithSalt(hashedPassword, salt);
  }

  private Optional<User> checkCookie(Http.RequestHeader header) {
    final Http.Cookie cookie = header.cookies().get(cookieHeader);
    if (cookie == null) return Optional.empty();
    else return this.cacheApi.get(cookie.value());
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
