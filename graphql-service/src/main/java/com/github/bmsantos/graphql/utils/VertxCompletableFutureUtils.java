package com.github.bmsantos.graphql.utils;

import me.escoffier.vertx.completablefuture.VertxCompletableFuture;

public class VertxCompletableFutureUtils {
  public static <T> VertxCompletableFuture<T> completedVertxCompletableFuture(final T value) {
    final VertxCompletableFuture<T> future = new VertxCompletableFuture<>();
    future.complete(value);
    return future;
  }
}

