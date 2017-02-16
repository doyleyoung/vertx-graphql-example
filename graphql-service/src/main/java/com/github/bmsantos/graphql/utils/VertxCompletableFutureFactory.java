package com.github.bmsantos.graphql.utils;

import java.util.concurrent.CompletableFuture;

import graphql.execution.async.CompletableFutureFactory;
import me.escoffier.vertx.completablefuture.VertxCompletableFuture;

public class VertxCompletableFutureFactory implements CompletableFutureFactory {
  @Override
  public <T> CompletableFuture<T> future() {
    return new VertxCompletableFuture();
  }
}
