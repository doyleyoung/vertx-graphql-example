package com.github.bmsantos.graphql.apigen.resolvers;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.github.bmsantos.graphql.dataloaders.DataLoaders;
import com.github.bmsantos.graphql.model.vehicles.Vehicle;
import com.github.bmsantos.graphql.model.vehicles.Vehicle.Unresolved;
import io.engagingspaces.vertx.dataloader.DataLoader;
import io.vertx.core.logging.Logger;

import static com.github.bmsantos.graphql.utils.VertxCompletableFutureUtils.completedVertxCompletableFuture;
import static io.vertx.core.Vertx.currentContext;
import static io.vertx.core.logging.LoggerFactory.getLogger;
import static java.util.Objects.requireNonNull;
import static me.escoffier.vertx.completablefuture.VertxCompletableFuture.from;

public class VehicleResolver implements Vehicle.AsyncResolver {
  private static final Logger log = getLogger(VehicleResolver.class);

  @Override
  public CompletableFuture<List<Vehicle>> resolve(final List<Vehicle> unresolved) {
    return null;
  }

  @Override
  public CompletableFuture<List<Vehicle>> resolve(final Object context, final List<Vehicle> unresolved) {
    if (!requireNonNull(unresolved).isEmpty()) {
      final Vehicle vehicle = unresolved.get(0);
      return processRentalVehicle(((DataLoaders)context).getVehicleDataLoader(), requireNonNull(vehicle));
    }
    return completedVertxCompletableFuture(null);
  }

  private CompletableFuture<List<Vehicle>> processRentalVehicle(final DataLoader<Long, List<Vehicle>> dataLoader, final Vehicle vehicle) {
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
}