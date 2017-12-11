import controllers.IndexController;
import play.ApplicationLoader;
import play.BuiltInComponentsFromContext;
import play.controllers.AssetsComponents;
import play.filters.components.HttpFiltersComponents;
import play.routing.Router;
import router.Routes;

public class AppComponents extends BuiltInComponentsFromContext
  implements HttpFiltersComponents,
  AssetsComponents {

  AppComponents(ApplicationLoader.Context context) {
    super(context);
  }

  private IndexController indexController() {
    return new IndexController();
  }

  @Override
  public Router router() {
    return new Routes(scalaHttpErrorHandler(), indexController(), assets()).asJava();
  }

}
