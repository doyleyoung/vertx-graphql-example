package com.github.bmsantos.graphql.resolvers;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.github.bmsantos.graphql.model.rental.Rental;
import com.github.bmsantos.graphql.model.vehicles.Vehicle;
import graphql.schema.DataFetchingEnvironment;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import static java.util.concurrent.CompletableFuture.completedFuture;

public class TestableVehicleResolver implements Vehicle.AsyncResolver {
  private Map<Long, Vehicle> vehicles;

  public TestableVehicleResolver() {
    this(new HashMap<>());
  }
  
  public TestableVehicleResolver(Map<Long, Vehicle> vehicles) {
    this.vehicles = vehicles;
  }

  @Override
  public CompletableFuture<Object> resolve(final DataFetchingEnvironment env) {
    if (env.containsArgument("id")) { // is query method
      return processArgumentQuery(env);
    }

    if (env.containsArgument("vehicle")) { // is mutation
      return processMutation(env);
    }

    if (env.getParentType().getName().equals("Rental")) { // is child
      return processRentalVehicle(env);
    }

    return completedFuture(vehicles.values()); // is query all
  }

  private CompletableFuture<Object> processRentalVehicle(final DataFetchingEnvironment env) {
    Rental rental = env.getSource();
    final Long id = rental.getCustomer().getId();
    return completedFuture(vehicles.get(id));
  }

  private CompletableFuture<Object> processArgumentQuery(final DataFetchingEnvironment env) {
    final Long id = env.getArgument("id");
    return completedFuture(vehicles.get(id));
  }

  private CompletableFuture<Object> processMutation(final DataFetchingEnvironment env) {
    JsonObject values = new JsonObject((Map<String, Object>) env.getArgument("vehicle"));
    Vehicle createVehicle = new Vehicle.Builder()
      .withId(Long.valueOf(vehicles.size() + 1))
      .withBrand(values.getString("brand"))
      .withModel(values.getString("model"))
      .withType(values.getString("type"))
      .withYear(values.getInteger("year"))
      .withMileage(values.getLong("mileage", 0L))
      .withExtras(values.getJsonArray("extras", new JsonArray()).getList())
      .build();
    return completedFuture(createVehicle);
  }
}