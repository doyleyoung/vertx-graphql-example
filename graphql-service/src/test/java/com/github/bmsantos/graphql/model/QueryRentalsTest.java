package com.github.bmsantos.graphql.model;

import java.util.concurrent.CompletionStage;

import com.google.inject.Injector;
import graphql.ExecutionResult;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static java.util.Collections.emptyMap;

@RunWith(VertxUnitRunner.class)
public class QueryRentalsTest extends BaseGraphQLTest {

  private Injector injector;

  @Before
  public void setup(TestContext context) throws Exception {
    super.setup(context);
    injector = setupRentalsInjector();
    initGraphQLAsync(injector, "QueryRentals");
  }

  @Test
  public void shouldRetrieveAllRentals(TestContext context) throws Exception {
    // Given
    String query = "{ rentals { id customer { id name } vehicle { id brand }} }";

    // When
    CompletionStage<ExecutionResult> future =
      graphQL.executeAsync(query, null, null, emptyMap());

    // Then
    printAndValidate(future, doc -> {
      context.assertTrue(doc.contains("name\":\"Albert Einstein"));
      context.assertTrue(doc.contains("brand\":\"Toyota"));

      context.assertTrue(doc.contains("name\":\"Isaac Newton"));
      context.assertTrue(doc.contains("brand\":\"Tesla"));
    });
  }

  @Test
  public void shouldRetrieveRentalById(TestContext context) throws Exception {
    // Given
    String query = "{ rental(id: 2) { id customer { id name } vehicle { id brand }} }";

    // When
    CompletionStage<ExecutionResult> future =
      graphQL.executeAsync(query, null, null, emptyMap());

    // Then
    printAndValidate(future, doc -> {
      context.assertFalse(doc.contains("name\":\"Albert Einstein"));
      context.assertFalse(doc.contains("brand\":\"Toyota"));

      context.assertTrue(doc.contains("name\":\"Isaac Newton"));
      context.assertTrue(doc.contains("brand\":\"Tesla"));
    });
  }
}
