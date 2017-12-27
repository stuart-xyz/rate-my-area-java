package models;

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

  @Constraints.Required
  @Column(name="review_id")
  public Long reviewId;

  public ImageUrl(String url) {
    this.url = url;
  }

  public static final Finder<Long, ImageUrl> find = new Finder<>(ImageUrl.class);

  @ManyToOne @JoinColumn(name="review_id", referencedColumnName="id")
  public Review associatedReview;

  public void setAssociatedReview(Review review) {
    this.associatedReview = review;
    this.reviewId = review.id;
  }

}
