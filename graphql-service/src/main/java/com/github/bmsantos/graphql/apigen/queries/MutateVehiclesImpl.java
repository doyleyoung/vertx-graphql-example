package com.github.bmsantos.graphql.apigen.queries;

import com.github.bmsantos.graphql.model.vehicles.InputVehicle;
import com.github.bmsantos.graphql.model.vehicles.MutateVehicles;
import com.github.bmsantos.graphql.model.vehicles.Vehicle;

public class MutateVehiclesImpl implements MutateVehicles {

  @Override
  public Vehicle createVehicle(final CreateVehicleArgs args) {
    final InputVehicle inputVehicle = args.getVehicle();
    return new Vehicle.Builder()
      .withBrand(inputVehicle.getBrand())
      .withModel(inputVehicle.getModel())
      .withType(inputVehicle.getType())
      .withYear(inputVehicle.getYear())
      .withMileage(inputVehicle.getMileage())
      .withExtras(inputVehicle.getExtras())
      .build();
  }
}
