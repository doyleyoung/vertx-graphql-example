package com.github.bmsantos.graphql.resolvers;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;

import com.github.bmsantos.graphql.model.rental.Rental;
import com.github.bmsantos.graphql.rest.RestClient;
import graphql.schema.DataFetchingEnvironment;
import io.vertx.core.logging.Logger;
import me.escoffier.vertx.completablefuture.VertxCompletableFuture;

import static com.github.bmsantos.graphql.utils.CompletableObserver.completableObserver;
import static io.vertx.core.logging.LoggerFactory.getLogger;
import static java.util.concurrent.CompletableFuture.completedFuture;

public class RentalResolver implements Rental.AsyncResolver {
  private static final Logger log = getLogger(RentalResolver.class);

  @Inject
  private RestClient restClient;

  @Override
  public CompletableFuture<?> resolve(final DataFetchingEnvironment env) {
    log.debug("Fetching all active rentals");

    try {
      if (env.containsArgument("id")) { // is query method
        return processArgumentsQuery(env);
      }
      return processQueryAll();
    } catch (final Throwable e) {
      log.error("Failed to fetch active rentals", e);
    }

    return completedFuture(null);
  }

  private CompletableFuture<?> processArgumentsQuery(final DataFetchingEnvironment env) {
    final Long id = env.getArgument("id");
    final VertxCompletableFuture<Rental> future = new VertxCompletableFuture<>();
    restClient.findRentalById(id)
      .subscribe(completableObserver(future));
    return future;
  }

  private CompletableFuture<?> processQueryAll() {
    final VertxCompletableFuture<List<Rental>> future = new VertxCompletableFuture<>();
    restClient.findAllRentals()
      .subscribe(completableObserver(future));
    return future;
  }
}