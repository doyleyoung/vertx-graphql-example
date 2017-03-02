package com.github.bmsantos.graphql.utils;

import java.util.concurrent.CompletableFuture;

import io.vertx.core.Future;
import rx.Observer;

import static java.util.Objects.isNull;

public class CompletableObserver<T> implements Observer<T> {

  public static <T> Observer<T> completableObserver(final CompletableFuture<T> future) {
    return new CompletableObserver<>(future);
  }

  public static <T> Observer<T> completableObserver(final Future<T> future) {
    return new CompletableObserver<>(future);
  }

  private CompletableFuture<T> completableFuture;
  private Future<T> future;

  public CompletableObserver(final CompletableFuture<T> completableFuture) {
    this.completableFuture = completableFuture;
  }
  public CompletableObserver(final Future<T> future) {
    this.future = future;
  }

  @Override
  public void onCompleted() {
    // Empty
  }

  @Override
  public void onError(final Throwable t) {
    if (isNull(completableFuture)) {
      future.fail(t);
    } else {
      completableFuture.completeExceptionally(t);
    }
  }

  @Override
  public void onNext(final T emission) {
    if (isNull(completableFuture)) {
      future.complete(emission);
    } else {
      completableFuture.complete(emission);
    }
  }
}
