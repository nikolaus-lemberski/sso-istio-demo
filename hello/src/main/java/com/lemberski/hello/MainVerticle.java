package com.lemberski.hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class MainVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    Router router = createRouter();
    Integer port = config().getInteger("PORT", 8080);

    vertx.createHttpServer()
        .requestHandler(router)
        .listen(port, http -> {
          if (http.succeeded()) {
            startPromise.complete();
            LOG.info("HTTP server started on port {}", port);
          } else {
            startPromise.fail(http.cause());
          }
        });
  }

  private Router createRouter() {
    Router router = Router.router(vertx);

    router.route("/").handler(ctx -> {
      out("Hello!", ctx);
    });

    router.route("/health").handler(ctx -> {
      out("UP", ctx);
    });
    return router;
  }

  private void out(String message, RoutingContext ctx) {
    HttpServerResponse response = ctx.response();
    response.putHeader("content-type", "text/plain");
    response.end(message + "\n");
  }

}
