package com.julienviet.streams.impl;

import com.julienviet.streams.RxReadStream;
import io.vertx.core.Handler;
import io.vertx.core.streams.ReadStream;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public abstract class RxReadStreamBase<T> implements RxReadStream<T> {

  @Override
  public final <R> RxReadStream<R> concatMap(Function<? super T, ? extends ReadStream<? extends R>> mapper) {
    return new ConcatMapRxReadStream<>(source(), mapper);
  }

  @Override
  public <R> RxReadStream<R> flatMap(Function<? super T, ? extends ReadStream<? extends R>> mapper) {
    return new FlatMapRxReadStream<>(source(), mapper);
  }

  @Override
  public <R> RxReadStream<R> map(Function<? super T, ? extends R> mapper) {
    return new MapRxReadStream<>(source(), mapper);
  }

  @Override
  public RxReadStream<T> filter(Predicate<? super T> predicate) {
    return new FilterRxReadStream<>(source(), predicate);
  }

  @Override
  public RxReadStream<List<T>> buffer(long timespan, TimeUnit unit, int count, boolean restartTimerOnMaxSize) {
    return new BufferRxReadStream<T>(source(), timespan, unit, count, restartTimerOnMaxSize);
  }

  protected ReadStream<T> source() {
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
