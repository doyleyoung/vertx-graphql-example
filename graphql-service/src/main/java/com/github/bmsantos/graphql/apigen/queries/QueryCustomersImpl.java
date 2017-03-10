package com.github.bmsantos.graphql.apigen.queries;

import java.util.List;

import com.github.bmsantos.graphql.model.customer.Customer;
import com.github.bmsantos.graphql.model.customer.Customer.Unresolved;
import com.github.bmsantos.graphql.model.customer.QueryCustomers;

import static java.util.Collections.emptyList;

public class QueryCustomersImpl implements QueryCustomers {
  @Override
  public List<Customer> getCustomers() {
    return emptyList();
  }

  @Override
  public Customer customer(final CustomerArgs args) {
    return new Unresolved(args.getId());
  }
}
