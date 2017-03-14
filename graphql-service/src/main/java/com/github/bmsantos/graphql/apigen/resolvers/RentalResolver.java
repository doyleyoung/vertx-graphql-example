package com.github.bmsantos.graphql.apigen.resolvers;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.github.bmsantos.graphql.dataloaders.DataLoaders;
import com.github.bmsantos.graphql.model.rental.Rental;
import com.github.bmsantos.graphql.model.rental.Rental.Unresolved;
import com.github.bmsantos.graphql.rest.RestClient;
import io.engagingspaces.vertx.dataloader.DataLoader;
import io.vertx.core.logging.Logger;
import me.escoffier.vertx.completablefuture.VertxCompletableFuture;

import static com.github.bmsantos.graphql.utils.CompletableObserver.completableObserver;
import static com.github.bmsantos.graphql.utils.VertxCompletableFutureUtils.completedVertxCompletableFuture;
import static io.vertx.core.Vertx.currentContext;
import static io.vertx.core.logging.LoggerFactory.getLogger;
import static java.util.Objects.requireNonNull;
import static me.escoffier.vertx.completablefuture.VertxCompletableFuture.from;

public class RentalResolver implements Rental.AsyncResolver {
  private static final Logger log = getLogger(RentalResolver.class);

  @Override
  public CompletableFuture<List<Rental>> resolve(final List<Rental> unresolved) {
    return null;
  }

  @Override
  public CompletableFuture<List<Rental>> resolve(final Object context, final List<Rental> unresolved) {
    log.debug("Fetching all active rentals");

    final DataLoaders dataLoaders = (DataLoaders) context;

    if (!requireNonNull(unresolved).isEmpty()) {
      final Rental rental = unresolved.get(0);
      return processArgumentsQuery(dataLoaders.getRentalDataLoader(), requireNonNull(rental));
    }

    return processQueryAll(dataLoaders.getRestClient());
  }

  private CompletableFuture<List<Rental>> processArgumentsQuery(final DataLoader<Long, List<Rental>> dataLoader, final Rental rental) {
    if (rental.getClass().equals(Unresolved.class)) {
      final Long id = rental.getId();
      log.debug("Fetching rental with id: " + id);
      try {
        return from(currentContext(), dataLoader.load(id));
      } catch (final Exception e) {
        log.error("Failed to fetch rental with id: " + id, e);
      } finally {
        dataLoader.dispatch();
      }
    }
    return completedVertxCompletableFuture(null);
  }

  private CompletableFuture<List<Rental>> processQueryAll(final RestClient restClient) {
    final VertxCompletableFuture<List<Rental>> future = new VertxCompletableFuture<>();
    restClient.findAllRentals()
      .subscribe(completableObserver(future));
    return future;
  }
}