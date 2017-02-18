package com.github.bmsantos.graphql.resolvers;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;

import com.github.bmsantos.graphql.model.rental.Rental;
import com.github.bmsantos.graphql.model.rental.Rental.Unresolved;
import com.github.bmsantos.graphql.model.vehicles.Vehicle;
import com.github.bmsantos.graphql.rest.RestClient;
import com.google.common.collect.Lists;
import graphql.schema.DataFetchingEnvironment;
import io.vertx.core.logging.Logger;
import me.escoffier.vertx.completablefuture.VertxCompletableFuture;

import static com.github.bmsantos.graphql.utils.CompletableObserver.completableObserver;
import static com.github.bmsantos.graphql.utils.VertxCompletableFutureUtils.completedVertxCompletableFuture;
import static io.vertx.core.logging.LoggerFactory.getLogger;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.CompletableFuture.completedFuture;

public class RentalResolver implements Rental.AsyncResolver {
  private static final Logger log = getLogger(RentalResolver.class);

  @Inject
  private RestClient restClient;

  @Override
  public CompletableFuture<List<Rental>> resolve(final List<Rental> unresolved) {
    log.debug("Fetching all active rentals");

    if (!requireNonNull(unresolved).isEmpty()) {
      final Rental rental = unresolved.get(0);
      return processArgumentsQuery(requireNonNull(rental));
    }

    return processQueryAll();
  }

  private CompletableFuture<List<Rental>> processArgumentsQuery(final Rental rental) {
    if (rental.getClass().equals(Unresolved.class)) {
      final Long id = rental.getId();
      log.debug("Fetching rental with id: " + id);
      try {
        final VertxCompletableFuture<List<Rental>> future = new VertxCompletableFuture<>();
        restClient.findRentalById(id)
          .map(Lists::newArrayList)
          .subscribe(completableObserver(future));
        return future;
      } catch (final Exception e) {
        log.error("Failed to fetch rental with id: " + id, e);
      }
    }
    return completedVertxCompletableFuture(null);
  }

  private CompletableFuture<List<Rental>> processQueryAll() {
    final VertxCompletableFuture<List<Rental>> future = new VertxCompletableFuture<>();
    restClient.findAllRentals()
      .subscribe(completableObserver(future));
    return future;
  }
}