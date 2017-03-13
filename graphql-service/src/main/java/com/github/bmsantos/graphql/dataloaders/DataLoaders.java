package com.github.bmsantos.graphql.dataloaders;

import java.util.List;

import com.github.bmsantos.graphql.model.customer.Customer;
import com.github.bmsantos.graphql.model.rental.Rental;
import com.github.bmsantos.graphql.model.vehicles.Vehicle;
import com.github.bmsantos.graphql.rest.RestClient;
import com.google.common.collect.Lists;
import io.engagingspaces.vertx.dataloader.DataLoader;
import io.vertx.core.Future;

import static com.github.bmsantos.graphql.utils.CompletableObserver.completableObserver;
import static io.vertx.core.CompositeFuture.all;
import static io.vertx.core.Future.future;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

public class DataLoaders {

  private RestClient restClient;
  private DataLoader<Long, List<Customer>> customerDataLoader;
  private DataLoader<Long, List<Vehicle>> vehicleDataLoader;
  private DataLoader<Long, List<Rental>> rentalDataLoader;

  public DataLoaders(final RestClient restClient) {
    this.restClient = restClient;
  }

  public RestClient getRestClient() {
    return restClient;
  }

  public DataLoader<Long, List<Customer>> getCustomerDataLoader() {
    if (isNull(customerDataLoader)) {
      customerDataLoader = new DataLoader<>(keys -> {
        List<Future> futures = keys.stream().map(key -> {

          final Future<List<Customer>> future = future();
          restClient.findCustomerById(key)
            .map(Lists::newArrayList)
            .subscribe(completableObserver(future));

          return future;
        }).collect(toList());
        return all(futures);
      });
    }
    return customerDataLoader;
  }

  public DataLoader<Long, List<Vehicle>> getVehicleDataLoader() {
    if (isNull(vehicleDataLoader)) {
      vehicleDataLoader = new DataLoader<>(keys -> {
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
    return vehicleDataLoader;
  }

  public DataLoader<Long, List<Rental>> getRentalDataLoader() {
    if (isNull(rentalDataLoader)) {
      rentalDataLoader = new DataLoader<>(keys -> {
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
    return rentalDataLoader;
  }
}
