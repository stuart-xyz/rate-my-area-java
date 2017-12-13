package models;

import io.ebean.Finder;
import io.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="users")
public class User extends Model {

  @Id
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

  public static final Finder<Long, User> find = new Finder<>(User.class);

}
