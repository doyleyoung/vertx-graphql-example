package com.github.bmsantos.graphql.apigen.resolvers;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.github.bmsantos.graphql.model.customer.Customer;

import static com.github.bmsantos.graphql.utils.VertxCompletableFutureUtils.completedVertxCompletableFuture;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

public class TestableCustomerResolver implements Customer.AsyncResolver {
  private Map<Long, Customer> customers;

  public TestableCustomerResolver(Map<Long, Customer> customers) {
    this.customers = customers;
  }

  @Override
  public CompletableFuture<List<Customer>> resolve(final List<Customer> unresolved) {

    if (!requireNonNull(unresolved).isEmpty()) {
      Customer customer = unresolved.get(0);
      if (isNull(customer.getId())) { // is create new customer mutation
        customer = new Customer.Builder(customer).withId((long) (customers.size() + 1)).build();
        customers.put(customer.getId(), customer);
        return completedVertxCompletableFuture(newArrayList(customer));
      }
      return completedVertxCompletableFuture(unresolved.stream().map(u -> customers.get(u.getId())).collect(toList())); // is argument query by id
    }

    return completedVertxCompletableFuture(newArrayList(customers.values())); // is query all
  }

  @Override
  public CompletableFuture<List<Customer>> resolve(final Object context, final List<Customer> list) {
    return null;
  }
}