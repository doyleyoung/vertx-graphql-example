package com.github.bmsantos.graphql.resolvers;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;

import com.github.bmsantos.graphql.model.vehicles.Vehicle;
import com.github.bmsantos.graphql.model.vehicles.Vehicle.Unresolved;
import com.github.bmsantos.graphql.rest.RestClient;
import com.google.common.collect.Lists;
import io.vertx.core.logging.Logger;
import me.escoffier.vertx.completablefuture.VertxCompletableFuture;

import static com.github.bmsantos.graphql.utils.CompletableObserver.completableObserver;
import static com.github.bmsantos.graphql.utils.VertxCompletableFutureUtils.completedVertxCompletableFuture;
import static io.vertx.core.logging.LoggerFactory.getLogger;
import static java.util.Objects.requireNonNull;

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
    if (vehicle.getClass().equals(Unresolved.class)) {
      final Long id = vehicle.getId();
      log.debug("Fetching vehicle for rental id: " + id);
      try {
        final VertxCompletableFuture<List<Vehicle>> future = new VertxCompletableFuture<>();
        restClient.findVehicleById(id)
          .map(Lists::newArrayList)
          .subscribe(completableObserver(future));
        return future;
      } catch (final Exception e) {
        log.error("Failed to fetch vehicle with id: " + id, e);
      }
    }
    return completedVertxCompletableFuture(null);
  }
}