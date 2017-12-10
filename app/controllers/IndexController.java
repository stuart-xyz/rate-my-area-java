package controllers;

import play.mvc.*;


public class IndexController extends Controller {

  public Result index() {
    return ok(views.html.index.render());
  }

}
