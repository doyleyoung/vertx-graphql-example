package com.github.bmsantos.graphql.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

import com.github.bmsantos.graphql.model.customer.Customer;
import com.github.bmsantos.graphql.model.guice.GuiceModule;
import com.github.bmsantos.graphql.model.rental.Rental;
import com.github.bmsantos.graphql.model.vehicles.Vehicle;
import com.github.bmsantos.graphql.resolvers.TestableCustomerResolver;
import com.github.bmsantos.graphql.resolvers.TestableRentalResolver;
import com.github.bmsantos.graphql.resolvers.TestableVehicleResolver;
import com.github.bmsantos.graphql.utils.VertxCompletableFutureFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import graphql.ExecutionResult;
import graphql.GraphQLAsync;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLType;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

import static com.google.inject.Guice.createInjector;
import static graphql.execution.async.AsyncExecutionStrategy.parallel;

abstract public class BaseGraphQLTest {

  protected Vertx vertx;
  protected GraphQLAsync graphQL;

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void setup(TestContext context) throws Exception {
    vertx = rule.vertx();
  }

  @After
  public void tearDown(TestContext context) {
    vertx.close(context.asyncAssertSuccess());
  }

  protected void initGraphQLAsync(final Injector injector, final String queryName) {
    Map<String, GraphQLType> types =
      injector.getInstance(Key.get(new TypeLiteral<Map<String, GraphQLType>>() {
      }));

    GraphQLSchema schema = GraphQLSchema.newSchema()
      .query((GraphQLObjectType) types.get(queryName))
      .build(new HashSet<>(types.values()));
    graphQL = new GraphQLAsync(schema, parallel(new VertxCompletableFutureFactory()));
  }

  protected void initGraphQLAsync(final Injector injector, final String queryName, final String mutationName) {
    Map<String, GraphQLType> types =
      injector.getInstance(Key.get(new TypeLiteral<Map<String, GraphQLType>>() {
      }));

    GraphQLSchema schema = GraphQLSchema.newSchema()
      .query((GraphQLObjectType) types.get(queryName))
      .mutation((GraphQLObjectType) types.get(mutationName))
      .build(new HashSet<>(types.values()));
    graphQL = new GraphQLAsync(schema, parallel(new VertxCompletableFutureFactory()), parallel(new VertxCompletableFutureFactory()));
  }

  protected void printAndValidate(CompletionStage<ExecutionResult> future, Consumer<String> validator) {
    future.handle((result, throwable) -> {
      String doc = Json.encode(result);
      System.out.println(doc);
      validator.accept(doc);
      return this;
    }).toCompletableFuture();
  }

  protected Map<Long, Vehicle> setupVehiclesDS() {
    Map<Long, Vehicle> vehicles = new LinkedHashMap<>();
    vehicles.put(1L,
      new Vehicle.Builder()
        .withId(1L)
        .withBrand("Toyota")
        .withModel("Corolla")
        .withType("Car")
        .withYear(2006)
        .withMileage(40000L)
        .build());

    vehicles.put(2L,
      new Vehicle.Builder()
        .withId(2L)
        .withBrand("Tesla")
        .withModel("P100D")
        .withType("Car")
        .withYear(20017)
        .withMileage(100L)
        .build());

    return vehicles;
  }

  private Map<Long, Customer> setupCustomersDS() {
    Map<Long, Customer> customers = new LinkedHashMap<>();
    customers.put(1L,
      new Customer.Builder()
        .withId(1L)
        .withName("Albert Einstein")
        .withAddress("123 Someplace")
        .withCity("Some city")
        .withState("New York")
        .withCountry("USA")
        .build());

    customers.put(2L,
      new Customer.Builder()
        .withId(2L)
        .withName("Isaac Newton")
        .withAddress("456 Somewhere Else")
        .withCity("Some other city")
        .withState("Virginia")
        .withCountry("USA")
        .build());
    return customers;
  }

  private Map<Long, Rental> setupRentalsDS() {
    Map<Long, Rental> rentals = new HashMap<>();
    rentals.put(1L,
      new Rental.Builder()
        .withId(1L)
        .withCustomer(new Customer.Unresolved(1L))
        .withVehicle(new Vehicle.Unresolved(1L))
        .build());

    rentals.put(2L,
      new Rental.Builder()
        .withId(2L)
        .withCustomer(new Customer.Unresolved(2L))
        .withVehicle(new Vehicle.Unresolved(2L))
        .build());
    return rentals;
  }

  public Injector setupVehiclesInjector() throws Exception {
    return createInjector(
      new GuiceModule(),
      new AbstractModule() {
        @Override
        protected void configure() {
          bind(Vehicle.AsyncResolver.class)
            .toInstance(new TestableVehicleResolver(setupVehiclesDS()));
        }
      });
  }

  public Injector setupCustomersInjector() throws Exception {
    return createInjector(
      new GuiceModule(),
      new AbstractModule() {
        @Override
        protected void configure() {
          bind(Customer.AsyncResolver.class)
            .toInstance(new TestableCustomerResolver(setupCustomersDS()));
        }
      });
  }

  public Injector setupRentalsInjector() throws Exception {
    return createInjector(
      new GuiceModule(),
      new AbstractModule() {
        @Override
        protected void configure() {
          bind(Customer.AsyncResolver.class)
            .toInstance(new TestableCustomerResolver(setupCustomersDS()));
          bind(Vehicle.AsyncResolver.class)
            .toInstance(new TestableVehicleResolver(setupVehiclesDS()));
          bind(Rental.AsyncResolver.class)
            .toInstance(new TestableRentalResolver(setupRentalsDS()));
        }
      });
  }
}
