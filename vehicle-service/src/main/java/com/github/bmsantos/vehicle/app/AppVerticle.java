package com.github.bmsantos.vehicle.app;

import java.util.List;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.logging.Logger;
import io.vertx.ext.web.Router;

import static io.vertx.core.logging.LoggerFactory.getLogger;
import static java.util.Objects.nonNull;

public class AppVerticle extends AbstractVerticle {
  private static final Logger log = getLogger(AppVerticle.class);

  @Override
  public void start(final Future<Void> startFuture) throws Exception {

    final Router router = Router.router(vertx);

    router.getWithRegex("/|/vehicles").handler(req -> {
      log.info("GET /vehicles");
      req.response().putHeader("content-type", "application/json").end(config().getJsonArray("vehicles").toString());
    });

    router.get("/vehicles/:id").handler(req -> {
      Integer id = getIntegerValue(req.request().getParam("id"));
      log.info("GET /vehicles/" + id);

      List stays = config().getJsonArray("vehicles", new JsonArray()).getList();
      if (nonNull(id) && id < stays.size()) {
        req.response().putHeader("content-type", "application/json").end(stays.get(id).toString());
      } else {
        req.response().setStatusCode(404).setStatusMessage("Resource not found").end();
      }
    });

    vertx.createHttpServer().requestHandler(router::accept).listen(8082);
  }

  private Integer getIntegerValue(String value) {
    try {
      return Integer.valueOf(value);
    } catch (Exception e) {
    }
    return null;
  }
}
