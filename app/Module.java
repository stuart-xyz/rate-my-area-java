import com.google.inject.AbstractModule;
import services.AbstractS3Service;
import services.DefaultS3Service;

public class Module extends AbstractModule {

  protected void configure() {
    bind(AbstractS3Service.class).to(DefaultS3Service.class);
  }

}
