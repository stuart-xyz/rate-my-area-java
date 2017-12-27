package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.ebean.Finder;
import io.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.*;


@Entity
@Table(name="image_urls")
public class ImageUrl extends Model {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  public Long id;

  @Constraints.Required
  public final String url;

  @ManyToOne @JsonIgnore
  public Review review;

  public ImageUrl(String url) {
    this.url = url;
  }

  public static final Finder<Long, ImageUrl> find = new Finder<>(ImageUrl.class);

}
