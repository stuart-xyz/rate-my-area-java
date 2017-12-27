package controllers;

import play.filters.csrf.CSRF;
import play.mvc.Controller;
import play.mvc.Result;


public class IndexController extends Controller {

  public Result index() {
    return ok(views.html.index.render(CSRF.getToken(request()).get().value()));
  }

}
