package models;

import io.ebean.Finder;
import io.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.*;


@Entity
@Table(name="image_urls")
public class ImageUrl extends Model {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  @Constraints.Required
  public final String url;

  @Constraints.Required
  @Column(name="review_id")
  public final Long reviewId;

  public ImageUrl(String url, Long reviewId) {
    this.url = url;
    this.reviewId = reviewId;
  }

  public static final Finder<Long, ImageUrl> find = new Finder<>(ImageUrl.class);

}
