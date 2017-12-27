package models;

import io.ebean.Finder;
import io.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name="users")
public class User extends Model {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  public Long id;

  @Constraints.Required
  public final String email;

  @Constraints.Required
  @Column(name="password")
  public final String hashedPassword;

  @Constraints.Required
  public final String salt;

  @Constraints.Required
  public final String username;

  public User(String email, String hashedPassword, String salt, String username) {
    this.email = email;
    this.hashedPassword = hashedPassword;
    this.salt = salt;
    this.username = username;
  }

  public static final Finder<Long, User> find = new Finder<>(User.class);

  @OneToMany(cascade=CascadeType.ALL)
  public List<Review> reviews = new ArrayList<>();

}
