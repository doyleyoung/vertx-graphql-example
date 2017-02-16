package com.github.bmsantos.graphql.resolvers;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.github.bmsantos.graphql.model.customer.Customer;
import com.github.bmsantos.graphql.model.rental.Rental;
import graphql.schema.DataFetchingEnvironment;

import static java.util.Objects.requireNonNull;
import static java.util.concurrent.CompletableFuture.completedFuture;

public class TestableCustomerResolver implements Customer.AsyncResolver {
  private Map<Long, Customer> customers;

  public TestableCustomerResolver() {
    this(new HashMap<>());
  }

  public TestableCustomerResolver(Map<Long, Customer> customers) {
    this.customers = customers;
  }

  @Override
  public CompletableFuture<Object> resolve(final DataFetchingEnvironment env) {
    if (env.containsArgument("id")) { // is query method
      return processArgumentsQuery(env);
    }

    if (env.getParentType().getName().equals("Rental")) { // is child
      return processRentalCustomer(env);
    }

    return completedFuture(customers.values()); // is query all
  }

  private CompletableFuture<Object> processRentalCustomer(final DataFetchingEnvironment env) {
    Rental rental = env.getSource();
    final Customer customer = requireNonNull(rental.getCustomer());
    if (customer.getClass().equals(Customer.Unresolved.class)) {
      final Long id = customer.getId();
      return completedFuture(customers.get(id));
    }
    return completedFuture(customer);
  }

  private CompletableFuture<Object> processArgumentsQuery(final DataFetchingEnvironment env) {
    final Long id = env.getArgument("id");
    return completedFuture(customers.get(id));
  }
}