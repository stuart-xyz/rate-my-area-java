package models;

import io.ebean.Finder;
import io.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.util.List;


@Entity
@Table(name="reviews")
public class Review extends Model {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  @Constraints.Required
  public final String title;

  @Constraints.Required
  @Column(name="area_name")
  public final String areaName;

  @Constraints.Required
  public final String description;

  @Constraints.Required
  @Column(name="user_id")
  public final Long userId;

  public Review(String title, String areaName, String description, Long userId) {
    this.title = title;
    this.areaName = areaName;
    this.description = description;
    this.userId = userId;
  }

  public static final Finder<Long, Review> find = new Finder<>(Review.class);

  @OneToMany(cascade = CascadeType.ALL)
  public List<ImageUrl> imageUrls;

}
