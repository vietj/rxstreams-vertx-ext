package com.julienviet.rxstreams.impl;

import com.julienviet.rxstreams.RxReadStream;
import io.vertx.core.Handler;
import io.vertx.core.streams.ReadStream;

import java.util.function.Function;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public abstract class RxReadStreamBase<T> implements RxReadStream<T> {

  @Override
  public final <R> RxReadStream<R> concatMap(Function<? super T, ? extends ReadStream<? extends R>> mapper) {
    return new ConcatMapRxReadStream<>(stream(), mapper);
  }

  protected ReadStream<T> stream() {
    return this;
  }

  @Override
  public RxReadStream<T> pause() {
    throw new UnsupportedOperationException(getClass().getSimpleName() + " does not implement pause()");
  }

  @Override
  public RxReadStream<T> resume() {
    throw new UnsupportedOperationException(getClass().getSimpleName() + " does not implement resume()");
  }

  @Override
  public RxReadStream<T> exceptionHandler(Handler<Throwable> handler) {
    throw new UnsupportedOperationException();
  }

  @Override
  public RxReadStream<T> handler(Handler<T> handler) {
    throw new UnsupportedOperationException();
  }

  @Override
  public RxReadStream<T> endHandler(Handler<Void> endHandler) {
    throw new UnsupportedOperationException(getClass().getSimpleName() + " does not implement endHandler(Handler<Void>)");
  }
}
