package com.github.bmsantos.graphql.utils;

import java.util.concurrent.CompletableFuture;

import rx.Observer;

public class CompletableObserver<T> implements Observer<T> {

  public static <T> Observer<T> completableObserver(final CompletableFuture<T> future) {
    return new CompletableObserver<>(future);
  }

  private CompletableFuture<T> future;

  public CompletableObserver(final CompletableFuture<T> future) {
    this.future = future;
  }

  @Override
  public void onCompleted() {
    // Empty
  }

  @Override
  public void onError(final Throwable t) {
    future.completeExceptionally(t);
  }

  @Override
  public void onNext(final T emission) {
    future.complete(emission);
  }
}
