package com.github.bmsantos.graphql.apigen.queries;

import java.util.List;

import com.github.bmsantos.graphql.model.rental.QueryRentals;
import com.github.bmsantos.graphql.model.rental.Rental;
import com.github.bmsantos.graphql.model.rental.Rental.Unresolved;

import static java.util.Collections.emptyList;

public class QueryRentalsImpl implements QueryRentals {
  @Override
  public List<Rental> getRentals() {
    return emptyList();
  }

  @Override
  public Rental rental(final RentalArgs args) {
    return new Unresolved(args.getId());
  }
}
