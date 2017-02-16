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
public class MutateVehiclesTest extends BaseGraphQLTest {

  private Injector injector;

  @Before
  public void setup(TestContext context) throws Exception {
    super.setup(context);
    injector = setupVehiclesInjector();
    initGraphQLAsync(injector, "QueryVehicles", "MutateVehicles");
  }

  @Test
  public void shouldCreateNewVehicle(TestContext context) throws Exception {
    // Given
    String query =
      "mutation { createVehicle(vehicle: { brand: \"Ford\" model: \"Mustang\" type: \"Car\" year: 2016 }) { id model brand }}";

    // When
    CompletionStage<ExecutionResult> future =
      graphQL.executeAsync(query, null, null, emptyMap());

    // Then
    printAndValidate(future, doc -> {
      context.assertTrue(doc.contains("id\":3"));
      context.assertTrue(doc.contains("brand\":\"Ford"));
      context.assertTrue(doc.contains("model\":\"Mustang"));
    });
  }
}
