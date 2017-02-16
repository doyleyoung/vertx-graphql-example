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

public class CustomerDeserializer extends JsonDeserializer<Customer> {
  @Override
  public Customer deserialize(final JsonParser parser, final DeserializationContext ctx) throws IOException {
    final ObjectCodec oc = parser.getCodec();
    final JsonNode node = oc.readTree(parser);

    return new Customer.Builder()
      .withId(node.get("id").asLong())
      .withName(node.get("name").asText())
      .withAddress(node.get("address").asText())
      .withCity(node.get("city").asText())
      .withState(node.get("state").asText())
      .withCountry(node.get("country").asText())
      .build();
  }
}
