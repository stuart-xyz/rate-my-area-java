package controllers;

import play.filters.csrf.CSRF;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;


public class IndexController extends Controller {

  private final CSRF.TokenProviderProvider tokenProviderProvider;

  @Inject
  public IndexController(CSRF.TokenProviderProvider tokenProviderProvider) {
    this.tokenProviderProvider = tokenProviderProvider;
  }

  public Result index() {
    return ok(views.html.index.render(tokenProviderProvider.get().generateToken()));
  }

}
