import play.Application;
import play.ApplicationLoader;

public class AppApplicationLoader implements ApplicationLoader {

  @Override
  public Application load(Context context) {
    return new AppComponents(context).application();
  }

}
