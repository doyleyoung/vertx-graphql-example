package com.github.bmsantos.graphql.apigen.resolvers;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.github.bmsantos.graphql.model.rental.Rental;

import static com.github.bmsantos.graphql.utils.VertxCompletableFutureUtils.completedVertxCompletableFuture;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

public class TestableRentalResolver implements Rental.AsyncResolver {

  private Map<Long, Rental> rentals;
  public TestableRentalResolver(Map<Long, Rental> rentals) {
    this.rentals = rentals;
  }

  @Override
  public CompletableFuture<List<Rental>> resolve(final List<Rental> unresolved) {

    if (!requireNonNull(unresolved).isEmpty()) {
      Rental rental = unresolved.get(0);
      if (isNull(rental.getId())) { // is create new vehicle mutation
        rental = new Rental.Builder(rental).withId((long) (rentals.size() + 1)).build();
        rentals.put(rental.getId(), rental);
        return completedVertxCompletableFuture(newArrayList(rental));
      }
      return completedVertxCompletableFuture(unresolved.stream().map(u -> rentals.get(u.getId())).collect(toList())); // is argument query by id
    }

    return completedVertxCompletableFuture(newArrayList(rentals.values())); // is query all
  }

  @Override
  public CompletableFuture<List<Rental>> resolve(final Object context, final List<Rental> list) {
    return null;
  }
}