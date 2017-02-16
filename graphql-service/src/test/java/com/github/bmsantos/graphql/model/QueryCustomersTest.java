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
public class QueryCustomersTest extends BaseGraphQLTest {
  private static String query = "{ customers { id name } }";

  private Injector injector;

  @Before
  public void setup(TestContext context) throws Exception {
    super.setup(context);
    injector = setupCustomersInjector();
    initGraphQLAsync(injector, "QueryCustomers");
  }

  @Test
  public void shouldRetrieveAllCustomers(TestContext context) throws Exception {
    // Given
    String query = "{ customers { id name } }";

    // When
    CompletionStage<ExecutionResult> future =
      graphQL.executeAsync(query, null, null, emptyMap());

    // Then
    printAndValidate(future, doc -> {
      context.assertTrue(doc.contains("name\":\"Albert Einstein"));
      context.assertTrue(doc.contains("name\":\"Isaac Newton"));
    });
  }

  @Test
  public void shouldRetrieveByCustomerId(TestContext context) throws Exception {
    // Given
    String query = "{ customer(id:2) { id name } }";

    // When
    CompletionStage<ExecutionResult> future =
      graphQL.executeAsync(query, null, null, emptyMap());

    // Then
    printAndValidate(future, doc -> {
      context.assertFalse(doc.contains("name\":\"Albert Einstein"));
      context.assertTrue(doc.contains("name\":\"Isaac Newton"));
    });
  }
}