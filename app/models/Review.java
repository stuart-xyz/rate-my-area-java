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
  private String title;

  @Constraints.Required
  @Column(name="area_name")
  private String areaName;

  @Constraints.Required
  private String description;

  @ManyToOne @JsonIgnore
  public User user;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getAreaName() {
    return areaName;
  }

  public void setAreaName(String areaName) {
    this.areaName = areaName;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Review(String title, String areaName, String description) {
    this.title = title;
    this.areaName = areaName;
    this.description = description;
  }

  public static final Finder<Long, Review> find = new Finder<>(Review.class);

  @OneToMany(cascade=CascadeType.ALL)
  public List<ImageUrl> imageUrls = new ArrayList<>();

}
