package com.github.bmsantos.graphql.apigen.resolvers;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.github.bmsantos.graphql.model.vehicles.Vehicle;

import static com.github.bmsantos.graphql.utils.VertxCompletableFutureUtils.completedVertxCompletableFuture;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

public class TestableVehicleResolver implements Vehicle.AsyncResolver {
  private Map<Long, Vehicle> vehicles;

  public TestableVehicleResolver(Map<Long, Vehicle> vehicles) {
    this.vehicles = vehicles;
  }

  @Override
  public CompletableFuture<List<Vehicle>> resolve(final List<Vehicle> unresolved) {

    if (!requireNonNull(unresolved).isEmpty()) {
      Vehicle vehicle = unresolved.get(0);
      if (isNull(vehicle.getId())) { // is create new vehicle mutation
        vehicle = new Vehicle.Builder(vehicle).withId((long) (vehicles.size() + 1)).build();
        vehicles.put(vehicle.getId(), vehicle);
        return completedVertxCompletableFuture(newArrayList(vehicle));
      }
      return completedVertxCompletableFuture(unresolved.stream().map(u -> vehicles.get(u.getId())).collect(toList())); // is argument query by id
    }

    return completedVertxCompletableFuture(newArrayList(vehicles.values())); // is query all
  }
}