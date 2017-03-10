package com.github.bmsantos.graphql.apigen.resolvers;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;

import com.github.bmsantos.graphql.model.rental.Rental;
import com.github.bmsantos.graphql.model.rental.Rental.Unresolved;
import com.github.bmsantos.graphql.rest.RestClient;
import com.google.common.collect.Lists;
import io.engagingspaces.vertx.dataloader.DataLoader;
import io.vertx.core.Future;
import io.vertx.core.logging.Logger;
import me.escoffier.vertx.completablefuture.VertxCompletableFuture;

import static com.github.bmsantos.graphql.utils.CompletableObserver.completableObserver;
import static com.github.bmsantos.graphql.utils.VertxCompletableFutureUtils.completedVertxCompletableFuture;
import static io.vertx.core.CompositeFuture.all;
import static io.vertx.core.Future.future;
import static io.vertx.core.Vertx.currentContext;
import static io.vertx.core.logging.LoggerFactory.getLogger;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static me.escoffier.vertx.completablefuture.VertxCompletableFuture.from;

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
    final DataLoader<Long, List<Rental>> dataLoader = getDataLoader();

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

  private CompletableFuture<List<Rental>> processQueryAll() {
    final VertxCompletableFuture<List<Rental>> future = new VertxCompletableFuture<>();
    restClient.findAllRentals()
      .subscribe(completableObserver(future));
    return future;
  }

  private DataLoader<Long, List<Rental>> getDataLoader() {
    return new DataLoader<>(keys -> {
      List<Future> futures = keys.stream().map(key -> {

        final Future<List<Rental>> future = future();
        restClient.findRentalById(key)
          .map(Lists::newArrayList)
          .subscribe(completableObserver(future));

        return future;
      }).collect(toList());
      return all(futures);
    });
  }

}