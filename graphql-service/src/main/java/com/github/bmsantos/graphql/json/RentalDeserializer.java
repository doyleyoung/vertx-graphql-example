package com.github.bmsantos.graphql.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.bmsantos.graphql.model.customer.Customer;
import com.github.bmsantos.graphql.model.rental.Rental;
import com.github.bmsantos.graphql.model.vehicles.Vehicle;

public class RentalDeserializer extends JsonDeserializer<Rental> {
  @Override
  public Rental deserialize(final JsonParser parser, final DeserializationContext ctx) throws IOException {
    final ObjectCodec oc = parser.getCodec();
    final JsonNode node = oc.readTree(parser);

    final Long id = node.get("id").asLong();
    final Long customerId = node.get("customer").asLong();
    final Long vehicleId = node.get("vehicle").asLong();
    return new Rental.Builder()
      .withId(id)
      .withCustomer(new Customer.Unresolved(customerId))
      .withVehicle(new Vehicle.Unresolved(vehicleId))
      .build();
  }
}
