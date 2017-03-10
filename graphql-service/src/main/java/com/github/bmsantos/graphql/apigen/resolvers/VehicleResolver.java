package com.github.bmsantos.graphql.apigen.resolvers;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;

import com.github.bmsantos.graphql.model.vehicles.Vehicle;
import com.github.bmsantos.graphql.model.vehicles.Vehicle.Unresolved;
import com.github.bmsantos.graphql.rest.RestClient;
import com.google.common.collect.Lists;
import io.engagingspaces.vertx.dataloader.DataLoader;
import io.vertx.core.Future;
import io.vertx.core.logging.Logger;

import static com.github.bmsantos.graphql.utils.CompletableObserver.completableObserver;
import static com.github.bmsantos.graphql.utils.VertxCompletableFutureUtils.completedVertxCompletableFuture;
import static io.vertx.core.CompositeFuture.all;
import static io.vertx.core.Future.future;
import static io.vertx.core.Vertx.currentContext;
import static io.vertx.core.logging.LoggerFactory.getLogger;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static me.escoffier.vertx.completablefuture.VertxCompletableFuture.from;

public class VehicleResolver implements Vehicle.AsyncResolver {
  private static final Logger log = getLogger(VehicleResolver.class);

  @Inject
  private RestClient restClient;

  @Override
  public CompletableFuture<List<Vehicle>> resolve(final List<Vehicle> unresolved) {
    if (!requireNonNull(unresolved).isEmpty()) {
      final Vehicle vehicle = unresolved.get(0);
      return processRentalVehicle(requireNonNull(vehicle));
    }
    return completedVertxCompletableFuture(null);
  }

  private CompletableFuture<List<Vehicle>> processRentalVehicle(final Vehicle vehicle) {
    final DataLoader<Long, List<Vehicle>> dataLoader = getDataLoader();

    if (vehicle.getClass().equals(Unresolved.class)) {
      final Long id = vehicle.getId();
      log.debug("Fetching vehicle for rental id: " + id);
      try {
        return from(currentContext(), dataLoader.load(id));
      } catch (final Exception e) {
        log.error("Failed to fetch vehicle with id: " + id, e);
      } finally {
        dataLoader.dispatch();
      }
    }

    return completedVertxCompletableFuture(null);
  }

  private DataLoader<Long, List<Vehicle>> getDataLoader() {
    return new DataLoader<>(keys -> {
      List<Future> futures = keys.stream().map(key -> {

        final Future<List<Vehicle>> future = future();
        restClient.findVehicleById(key)
          .map(Lists::newArrayList)
          .subscribe(completableObserver(future));

        return future;
      }).collect(toList());
      return all(futures);
    });
  }

}