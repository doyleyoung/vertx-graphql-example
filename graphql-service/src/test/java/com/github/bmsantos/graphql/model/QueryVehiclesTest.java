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
public class QueryVehiclesTest extends BaseGraphQLTest {

  private Injector injector;

  @Before
  public void setup(TestContext context) throws Exception {
    super.setup(context);
    injector = setupVehiclesInjector();
    initGraphQLAsync(injector, "QueryVehicles");
  }

  @Test
  public void shouldRetrieveAllVehicles(TestContext context) throws Exception {
    // Given
    String query = "{ vehicles { id brand } }";

    // When
    CompletionStage<ExecutionResult> future =
      graphQL.executeAsync(query, null, null, emptyMap());

    // Then
    printAndValidate(future, doc -> {
      context.assertTrue(doc.contains("brand\":\"Tesla"));
      context.assertTrue(doc.contains("brand\":\"Toyota"));
    });
  }

  @Test
  public void shouldRetrieveByVehicleId(TestContext context) throws Exception {
    // Given
    String query = "{ vehicle(id:1) { id brand } }";

    // When
    CompletionStage<ExecutionResult> future =
      graphQL.executeAsync(query, null, null, emptyMap());

    // Then
    printAndValidate(future, doc -> {
      context.assertFalse(doc.contains("brand\":\"Tesla"));
      context.assertTrue(doc.contains("brand\":\"Toyota"));
    });
  }
}
