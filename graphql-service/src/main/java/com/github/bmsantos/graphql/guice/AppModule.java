package com.github.bmsantos.graphql.guice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.bmsantos.graphql.json.CustomerDeserializer;
import com.github.bmsantos.graphql.json.RentalDeserializer;
import com.github.bmsantos.graphql.json.VehicleDeserializer;
import com.github.bmsantos.graphql.model.customer.Customer;
import com.github.bmsantos.graphql.model.rental.Rental;
import com.github.bmsantos.graphql.model.vehicles.Vehicle;
import com.github.bmsantos.graphql.resolvers.CustomerResolver;
import com.github.bmsantos.graphql.resolvers.RentalResolver;
import com.github.bmsantos.graphql.resolvers.VehicleResolver;
import com.github.bmsantos.graphql.rest.GraphQLHandler;
import com.github.bmsantos.graphql.rest.RestClient;
import com.google.inject.AbstractModule;

import static io.vertx.core.json.Json.mapper;

public class AppModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(GraphQLHandler.class);
    bind(RestClient.class).toInstance(new RestClient());
    bind(Customer.AsyncResolver.class).toInstance(new CustomerResolver());
    bind(Vehicle.AsyncResolver.class).toInstance(new VehicleResolver());
    bind(Rental.AsyncResolver.class).toInstance(new RentalResolver());

    // Setup JSon mapper
    bind(ObjectMapper.class).toInstance(mapper);
    final SimpleModule module = new SimpleModule();
    module.addDeserializer(Rental.class, new RentalDeserializer());
    module.addDeserializer(Customer.class, new CustomerDeserializer());
    module.addDeserializer(Vehicle.class, new VehicleDeserializer());
    mapper.registerModule(module);
  }
}