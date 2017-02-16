package com.github.bmsantos.graphql.rest;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.bmsantos.graphql.model.customer.Customer;
import com.github.bmsantos.graphql.model.rental.Rental;
import com.github.bmsantos.graphql.model.vehicles.Vehicle;
import io.vertx.core.Context;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.rx.java.ObservableHandler;
import io.vertx.rx.java.RxHelper;
import rx.Observable;

import static com.github.bmsantos.graphql.utils.UnmarshallerOperator.unmarshaller;
import static io.vertx.core.Vertx.currentContext;
import static io.vertx.rx.java.RxHelper.observableHandler;
import static java.util.Objects.isNull;

public class RestClient {
  private String rentalUri;
  private String vehicleUri;
  private String customerUri;

  public RestClient() {
    final JsonObject config = currentContext().config();
    customerUri = config.getString("customer.service.url", "http://localhost:8081");
    vehicleUri = config.getString("vehicle.service.url", "http://localhost:8082");
    rentalUri = config.getString("rental.service.url", "http://localhost:8083");
  }

  public Observable<List<Rental>> findAllRentals() {
    final ObservableHandler<HttpClientResponse> handler = observableHandler();
    getHttpClient("rentals").getAbs(rentalUri + "/rentals", handler.toHandler()).end();
    return handler
      .flatMap(RxHelper::toObservable)
      .lift(unmarshaller(new TypeReference<List<Rental>>() { }));
  }

  public Observable<Rental> findRentalById(final Long id) {
    final ObservableHandler<HttpClientResponse> handler = observableHandler();
    getHttpClient("rentals").getAbs(rentalUri + "/rentals/" + id, handler.toHandler()).end();
    return handler
      .flatMap(RxHelper::toObservable)
      .lift(unmarshaller(Rental.class));
  }

  public Observable<Customer> findCustomerById(final Long id) {
    final ObservableHandler<HttpClientResponse> handler = observableHandler();
    getHttpClient("customers").getAbs(customerUri + "/customers/" + id, handler.toHandler()).end();
    return handler
      .flatMap(RxHelper::toObservable)
      .lift(unmarshaller(Customer.class));
  }

  public Observable<Vehicle> findVehicleById(final Long id) {
    final ObservableHandler<HttpClientResponse> handler = observableHandler();
    getHttpClient("vehicles").getAbs(vehicleUri + "/vehicles/" + id, handler.toHandler()).end();
    return handler
      .flatMap(RxHelper::toObservable)
      .lift(unmarshaller(Vehicle.class));
  }

  private HttpClient getHttpClient(final String name) {
    final Context context = currentContext();
    final String id = name + ":" + context.deploymentID();
    HttpClient client = context.get(id);
    if (isNull(client)) {
      client = currentContext().owner().createHttpClient(new HttpClientOptions());
      context.put(id, client);
    }
    return client;
  }
}