package com.github.bmsantos.graphql.resolvers;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;

import com.github.bmsantos.graphql.model.customer.Customer;
import com.github.bmsantos.graphql.model.customer.Customer.Unresolved;
import com.github.bmsantos.graphql.rest.RestClient;
import com.google.common.collect.Lists;
import io.vertx.core.logging.Logger;
import me.escoffier.vertx.completablefuture.VertxCompletableFuture;

import static com.github.bmsantos.graphql.utils.CompletableObserver.completableObserver;
import static com.github.bmsantos.graphql.utils.VertxCompletableFutureUtils.completedVertxCompletableFuture;
import static io.vertx.core.logging.LoggerFactory.getLogger;
import static java.util.Objects.requireNonNull;

public class CustomerResolver implements Customer.AsyncResolver {
  private static final Logger log = getLogger(CustomerResolver.class);

  @Inject
  private RestClient restClient;

  @Override
  public CompletableFuture<List<Customer>> resolve(final List<Customer> unresolved) {
    if (!requireNonNull(unresolved).isEmpty()) {
      final Customer customer = unresolved.get(0);
      return processRentalCustomer(requireNonNull(customer));
    }
    return completedVertxCompletableFuture(null);
  }

  private CompletableFuture<List<Customer>> processRentalCustomer(final Customer customer) {
    if (customer.getClass().equals(Unresolved.class)) {
      final Long id = customer.getId();
      log.debug("Fetching customer for rental id: " + id);
      try {
        final VertxCompletableFuture<List<Customer>> future = new VertxCompletableFuture<>();
        restClient.findCustomerById(id)
          .map(Lists::newArrayList)
          .subscribe(completableObserver(future));
        return future;
      } catch (final Exception e) {
        log.error("Failed to fetch customer with id: " + id, e);
      }
    }
    return completedVertxCompletableFuture(null);
  }}