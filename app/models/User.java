package models;

import io.ebean.Finder;
import io.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.*;

@Entity
@Table(name="users")
public class User extends Model {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  @Constraints.Required
  public String email;

  @Constraints.Required
  @Column(name="password")
  public String hashedPassword;

  @Constraints.Required
  public String salt;

  @Constraints.Required
  public String username;

  public User(String email, String hashedPassword, String salt, String username) {
    this.email = email;
    this.hashedPassword = hashedPassword;
    this.salt = salt;
    this.username = username;
  }

  public static final Finder<Long, User> find = new Finder<>(User.class);

}
