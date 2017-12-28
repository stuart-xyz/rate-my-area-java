package controllers;

import mocks.MockS3Service;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithApplication;
import services.AbstractS3Service;

import static org.junit.Assert.assertEquals;
import static play.inject.Bindings.bind;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.GET;
import static play.test.Helpers.route;


public class IndexControllerTest extends WithApplication {

  @Override
  protected Application provideApplication() {
    return new GuiceApplicationBuilder().overrides(bind(AbstractS3Service.class).to(MockS3Service.class)).build();
  }

  @Test
  public void testIndex() {
    Http.RequestBuilder request = new Http.RequestBuilder()
      .method(GET)
      .uri("/");

    Result result = route(app, request);
    assertEquals(OK, result.status());
  }

}
