package com.github.bmsantos.graphql;

import javax.inject.Inject;

import com.github.bmsantos.graphql.apigen.guice.AppModule;
import com.github.bmsantos.graphql.apigen.resolvers.VehicleResolver;
import com.github.bmsantos.graphql.model.guice.GuiceModule;
import com.github.bmsantos.graphql.rest.GraphQLHandler;
import com.google.inject.Injector;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.logging.Logger;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

import static com.google.inject.Guice.createInjector;
import static io.vertx.core.Future.future;
import static io.vertx.core.logging.LoggerFactory.getLogger;
import static io.vertx.ext.web.Router.router;

public class AppVerticle extends AbstractVerticle {
  private static final Logger log = getLogger(AppVerticle.class);

  @Inject
  public GraphQLHandler graphQLHandler;

  @Override
  public void init(final Vertx vertx, final Context context) {
    super.init(vertx, context);
  }

  @Override
  public void start(final Future<Void> startFuture) throws Exception {
    final Injector injector = createInjector(new GuiceModule(), new AppModule());
    context.put("injector", injector);
    injector.injectMembers(this);


    final Router router = router(vertx);
    router.route().handler(BodyHandler.create());
    router.post("/graphql").handler(graphQLHandler);

//    vertx.eventBus()
//      .consumer("vehicle_publisher")
//      .handler(VehicleResolver::handleVehicleRequest);

    startHttpServer(router).setHandler(startFuture.completer());
  }

  private Future<Void> startHttpServer(final Router router) {
    Future<HttpServer> httpServerFuture = future();
    vertx.createHttpServer()
      .requestHandler(it -> router.accept(it))
      .listen(config().getInteger("service.http.port", 8080),
        config().getString("service.http.address", "0.0.0.0"),
        httpServerFuture.completer());
    return httpServerFuture.map(r -> null);
  }
}