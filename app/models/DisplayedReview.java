package models;

import java.util.List;
import java.util.stream.Collectors;


public class DisplayedReview {

  public final String title;
  public final String areaName;
  public final String description;
  public final List<String> imageUrls;
  public final String username;

  public DisplayedReview(Review review, String username) {
    this.title = review.title;
    this.areaName = review.areaName;
    this.description = review.description;
    this.imageUrls = review.imageUrls.stream().map(imageUrl -> imageUrl.url).collect(Collectors.toList());
    this.username = username;
  }

}
