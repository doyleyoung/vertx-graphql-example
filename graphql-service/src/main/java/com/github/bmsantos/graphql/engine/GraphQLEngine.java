package com.github.bmsantos.graphql.engine;

import java.util.HashSet;
import java.util.Map;

import javax.inject.Inject;

import com.github.bmsantos.graphql.utils.VertxCompletableFutureFactory;
import graphql.GraphQLAsync;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLType;
import io.vertx.core.logging.Logger;

import static graphql.execution.async.AsyncExecutionStrategy.parallel;
import static io.vertx.core.logging.LoggerFactory.getLogger;
import static java.util.Objects.isNull;

public class GraphQLEngine {
  private static Logger log = getLogger(GraphQLEngine.class);

  @Inject
  private Map<String, GraphQLType> types;

  private static GraphQLAsync graphQL;

  public GraphQLAsync engine() {
    if (isNull(graphQL)) {
      graphQL = createGraphQL();
    }
    return graphQL;
  }

  private GraphQLAsync createGraphQL() {
    try {
      GraphQLSchema schema = GraphQLSchema.newSchema()
        .query((GraphQLObjectType) types.get("QueryRentals"))
        .build(new HashSet<>(types.values()));
      return new GraphQLAsync(schema, parallel(new VertxCompletableFutureFactory()));
    } catch (final Exception e) {
      final String error = "Unable to instantiate Guest GraphQL Schema";
      log.error(error, e);
      throw new RuntimeException(error, e);
    }
  }
}