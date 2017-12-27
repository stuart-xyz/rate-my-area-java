package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.ebean.Finder;
import io.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name="reviews")
public class Review extends Model {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  public Long id;

  @Constraints.Required
  public final String title;

  @Constraints.Required
  @Column(name="area_name")
  public final String areaName;

  @Constraints.Required
  public final String description;

  @ManyToOne @JsonIgnore
  public User user;

  public Review(String title, String areaName, String description) {
    this.title = title;
    this.areaName = areaName;
    this.description = description;
  }

  public static final Finder<Long, Review> find = new Finder<>(Review.class);

  @OneToMany(cascade=CascadeType.ALL)
  public List<ImageUrl> imageUrls = new ArrayList<>();

}
