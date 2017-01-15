package com.julienviet.streams.impl;

import com.julienviet.streams.RxReadStream;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.streams.ReadStream;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class BufferRxReadStream<T> extends RxReadStreamBase<List<T>> {

  private final ReadStream<T> source;
  private final long periodMillis;
  private final int count;
  private final boolean restartTimerOnMaxSize;
  private Handler<List<T>> handler;
  private Status status = Status.READY;

  public BufferRxReadStream(ReadStream<T> source, long timespan, TimeUnit unit, int count, boolean restartTimerOnMaxSize) {
    this.source = source;
    this.periodMillis = unit.toMillis(timespan);
    this.count = count;
    this.restartTimerOnMaxSize = restartTimerOnMaxSize;
  }

  @Override
  public RxReadStream<List<T>> handler(Handler<List<T>> h) {
    if (h != null) {
      if (status != Status.READY) {
        throw new IllegalStateException();
      }
      if (handler != null) {
        throw new IllegalStateException();
      }
      Context ctx = Vertx.currentContext();
      if (ctx == null) {
        throw new IllegalStateException();
      }
      Vertx vertx = ctx.owner();
      handler = h;
      return this;
    }
    return this;
  }
}
