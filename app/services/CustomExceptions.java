package services;


public class CustomExceptions {

  public static class UserNotLoggedInException extends Exception {}
  public static class CookieNotPresentException extends Exception {}
  public static class ReviewNotFoundException extends Exception {}

}
