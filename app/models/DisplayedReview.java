package models;

import java.util.List;
import java.util.stream.Collectors;


public class DisplayedReview {

  public final Long id;
  public final String title;
  public final String areaName;
  public final String description;
  public final List<String> imageUrls;
  public final String username;

  public DisplayedReview(Review review, String username) {
    this.id = review.id;
    this.title = review.getTitle();
    this.areaName = review.getAreaName();
    this.description = review.getDescription();
    this.imageUrls = review.imageUrls.stream().map(imageUrl -> imageUrl.url).collect(Collectors.toList());
    this.username = username;
  }

}
