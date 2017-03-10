package com.github.bmsantos.graphql.apigen.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.bmsantos.graphql.model.vehicles.Vehicle;

import static java.util.Objects.nonNull;

public class VehicleDeserializer extends JsonDeserializer<Vehicle> {
  @Override
  public Vehicle deserialize(final JsonParser parser, final DeserializationContext ctx) throws IOException {
    final ObjectCodec oc = parser.getCodec();
    final JsonNode node = oc.readTree(parser);

    final List<String> extras = new ArrayList<>();
    JsonNode extrasJN = node.get("extras");
    if (nonNull(extrasJN)) {
      extrasJN.forEach( it -> extras.add(it.asText()));
    }

    return new Vehicle.Builder()
      .withId(node.get("id").asLong())
      .withBrand(node.get("brand").asText())
      .withModel(node.get("model").asText())
      .withType(node.get("type").asText())
      .withYear(node.get("year").asInt())
      .withMileage(node.get("mileage").asLong())
      .withExtras(extras)
      .build();
  }
}
