package com.github.bmsantos.graphql.rest;

import java.util.Map;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;

import com.github.bmsantos.graphql.dataloaders.DataLoaders;
import com.github.bmsantos.graphql.engine.GraphQLEngine;
import graphql.ExecutionResult;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.ext.web.RoutingContext;

import static io.vertx.core.logging.LoggerFactory.getLogger;
import static java.util.Collections.emptyMap;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class GraphQLHandler implements Handler<RoutingContext> {
  private static Logger log = getLogger(GraphQLHandler.class);

  public static final int BAD_REQUEST_SC = 400;

  @Inject
  private GraphQLEngine graphQL;

  @Inject
  private RestClient restClient;

  @Override
  public void handle(final RoutingContext ctx) {
    final String body = ctx.getBody().toString();
    if (isNull(body)) {
      ctx.response().setStatusCode(BAD_REQUEST_SC).end();
    } else {
      final JsonObject json = new JsonObject(body);

      final String operationName = json.getString("operationName");
      final String query = json.getString("query");
      final Map<String, Object> variables = processVariables(json);

      log.debug("Received graphql request: " + json.toString());

      if (isNull(query)) {
        ctx.response().setStatusCode(BAD_REQUEST_SC).end();
      } else {
        log.debug("Executing Query: " + json);

        final CompletionStage<ExecutionResult> future =
          graphQL.engine().executeAsync(query, operationName, new DataLoaders(restClient), variables);

        future.handle((result, throwable) -> {
          final String doc = Json.encode(result);
          ctx.response().headers().set("Content-Type", "application/json");
          ctx.response().end(doc);
          return this;
        }).toCompletableFuture();
      }
    }
  }

  private Map<String, Object> processVariables(final JsonObject json) {
    try {
      return json.getJsonObject("variables").getMap();
    } catch (final Throwable t) {
      try {
        final String vars = json.getString("variables");
        final Map map = new JsonObject(isNull(vars) ? "{}" : vars).getMap();
        if (nonNull(map)) {
          return map;
        }
        return emptyMap();
      } catch (final Throwable tt) {
        return emptyMap();
      }
    }
  }
}
