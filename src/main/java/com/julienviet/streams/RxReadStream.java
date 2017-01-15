package com.julienviet.streams;

import com.julienviet.streams.impl.JustRxReadStream;
import com.julienviet.streams.impl.RangeRxReadStream;
import com.julienviet.streams.impl.RxReadStreamImpl;
import io.vertx.core.Handler;
import io.vertx.core.streams.ReadStream;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public interface RxReadStream<T> extends ReadStream<T> {

  static <V> RxReadStream<V> create(ReadStream<V> stream) {
    return new RxReadStreamImpl<>(stream);
  }

  static <V> RxReadStream<V> just(V value) {
    return new JustRxReadStream<>(value);
  }

  static RxReadStream<Integer> range(int from, int to) {
    return new RangeRxReadStream(from, to);
  }

  <R> RxReadStream<R> concatMap(Function<? super T, ? extends ReadStream<? extends R>> mapper);

  <R> RxReadStream<R> flatMap(Function<? super T, ? extends ReadStream<? extends R>> mapper);

  <R> RxReadStream<R> map(Function<? super T, ? extends R> mapper);

  RxReadStream<T> filter(Predicate<? super T> predicate);

  RxReadStream<List<T>> buffer(long timespan, TimeUnit unit, int count, boolean restartTimerOnMaxSize);

  @Override
  RxReadStream<T> exceptionHandler(Handler<Throwable> handler);

  @Override
  RxReadStream<T> handler(Handler<T> handler);

  @Override
  RxReadStream<T> pause();

  @Override
  RxReadStream<T> resume();

  @Override
  RxReadStream<T> endHandler(Handler<Void> endHandler);

}
