package com.github.bmsantos.graphql.resolvers;

import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;

import com.github.bmsantos.graphql.model.customer.Customer;
import com.github.bmsantos.graphql.model.rental.Rental;
import com.github.bmsantos.graphql.rest.RestClient;
import graphql.schema.DataFetchingEnvironment;
import io.vertx.core.logging.Logger;
import me.escoffier.vertx.completablefuture.VertxCompletableFuture;

import static com.github.bmsantos.graphql.utils.CompletableObserver.completableObserver;
import static io.vertx.core.logging.LoggerFactory.getLogger;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.CompletableFuture.completedFuture;

public class CustomerResolver implements Customer.AsyncResolver {
  private static final Logger log = getLogger(CustomerResolver.class);

  @Inject
  private RestClient restClient;

  @Override
  public CompletableFuture<?> resolve(final DataFetchingEnvironment env) {
    if (env.getParentType().getName().equals("Rental")) {
      return processRentalCustomer(env);
    }
    return completedFuture(null);
  }

  private CompletableFuture<?> processRentalCustomer(final DataFetchingEnvironment env) {
    Rental rental = env.getSource();
    log.debug("Fetching customer for rental id: " + rental.getId());

    final Customer customer = requireNonNull(rental.getCustomer());
    if (customer.getClass().equals(Customer.Unresolved.class)) {
      final Long id = customer.getId();
      try {
        final VertxCompletableFuture<Customer> future = new VertxCompletableFuture<>();
        restClient.findCustomerById(id)
          .subscribe(completableObserver(future));
        return future;
      } catch (final Exception e) {
        log.error("Failed to fetch customer with id: " + id, e);
      }
    }

    return completedFuture(null);
  }
}