package com.github.bmsantos.graphql.resolvers;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import com.github.bmsantos.graphql.model.customer.Customer;
import com.github.bmsantos.graphql.model.rental.Rental;
import com.github.bmsantos.graphql.model.vehicles.Vehicle;
import com.github.bmsantos.graphql.rest.RestClient;
import graphql.schema.DataFetchingEnvironment;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import me.escoffier.vertx.completablefuture.VertxCompletableFuture;

import static com.github.bmsantos.graphql.utils.CompletableObserver.completableObserver;
import static io.vertx.core.logging.LoggerFactory.getLogger;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.CompletableFuture.completedFuture;

public class VehicleResolver implements Vehicle.AsyncResolver {
  private static final Logger log = getLogger(VehicleResolver.class);

  @Inject
  private RestClient restClient;

  @Override
  public CompletableFuture<?> resolve(final DataFetchingEnvironment env) {
    if (env.getParentType().getName().equals("Rental")) {
      return processRentalVehicle(env);
    }
    return completedFuture(null);
  }

  private CompletableFuture<?> processRentalVehicle(final DataFetchingEnvironment env) {
    Rental rental = env.getSource();
    log.debug("Fetching vehicle for rental id: " + rental.getId());

    final Vehicle vehicle = requireNonNull(rental.getVehicle());
    if (vehicle.getClass().equals(Vehicle.Unresolved.class)) {
      final Long id = vehicle.getId();
      try {
        final VertxCompletableFuture<Vehicle> future = new VertxCompletableFuture<>();
        restClient.findVehicleById(id)
          .subscribe(completableObserver(future));
        return future;
      } catch (final Exception e) {
        log.error("Failed to fetch vehicle with id: " + id, e);
      }
    }

    return completedFuture(null);
  }}