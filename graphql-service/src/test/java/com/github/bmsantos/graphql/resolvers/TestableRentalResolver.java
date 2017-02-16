package com.github.bmsantos.graphql.resolvers;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.github.bmsantos.graphql.model.rental.Rental;
import graphql.schema.DataFetchingEnvironment;

import static java.util.concurrent.CompletableFuture.completedFuture;

public class TestableRentalResolver implements Rental.AsyncResolver {

  private Map<Long, Rental> rentals;
  public TestableRentalResolver(Map<Long, Rental> rentals) {
    this.rentals = rentals;
  }

  @Override
  public CompletableFuture<Object> resolve(final DataFetchingEnvironment env) {
    if (env.containsArgument("id")) { // is query method
      return processArgumentsQuery(env);
    }

    return completedFuture(rentals.values()); // is query all
  }

  private CompletableFuture<Object> processArgumentsQuery(final DataFetchingEnvironment env) {
    final Long id = env.getArgument("id");
    return completedFuture(rentals.get(id));
  }
}