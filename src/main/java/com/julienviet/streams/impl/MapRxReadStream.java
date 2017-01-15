package com.julienviet.streams.impl;

import com.julienviet.streams.RxReadStream;
import io.vertx.core.Handler;
import io.vertx.core.streams.ReadStream;

import java.util.function.Function;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class MapRxReadStream<T, R> extends RxReadStreamBase<R> {

  private static final Handler<Void> NOOP_HANDLER = v -> {};

  private final ReadStream<T> source;
  private final Function<? super T, ? extends R> mapper;
  private Handler<R> handler;
  private Handler<Void> endHandler = NOOP_HANDLER;
  private Status status = Status.READY;

  public MapRxReadStream(ReadStream<T> source, Function<? super T, ? extends R> mapper) {
    this.source = source;
    this.mapper = mapper;
  }

  private synchronized void handleEnd(Void event) {
    status = Status.DONE;
    endHandler.handle(null);
  }

  @Override
  public RxReadStream<R> pause() {
    source.pause();
    return this;
  }

  @Override
  public RxReadStream<R> resume() {
    source.resume();
    return this;
  }

  @Override
  public synchronized RxReadStream<R> handler(Handler<R> h) {
    if (h != null) {
      if (status != Status.READY) {
        throw new IllegalStateException();
      }
      if (handler != null) {
        throw new IllegalStateException();
      }
      handler = h;
      source.endHandler(this::handleEnd);
      source.handler(event -> {
        R result = mapper.apply(event);
        h.handle(result);
      });
    }
    return this;
  }

  @Override
  public synchronized RxReadStream<R> endHandler(Handler<Void> handler) {
    if (status == Status.READY && handler != null) {
      endHandler = handler;
    }
    return this;
  }
}
