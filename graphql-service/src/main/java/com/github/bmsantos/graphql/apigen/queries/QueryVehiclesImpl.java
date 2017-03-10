package com.github.bmsantos.graphql.apigen.queries;

import java.util.List;

import com.github.bmsantos.graphql.model.vehicles.QueryVehicles;
import com.github.bmsantos.graphql.model.vehicles.Vehicle;
import com.github.bmsantos.graphql.model.vehicles.Vehicle.Unresolved;

import static java.util.Collections.emptyList;

public class QueryVehiclesImpl implements QueryVehicles {
  @Override
  public List<Vehicle> getVehicles() {
    return emptyList();
  }

  @Override
  public Vehicle vehicle(final VehicleArgs args) {
    return new Unresolved(args.getId());
  }
}
