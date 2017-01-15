package com.julienviet.streams.impl;

import com.julienviet.streams.RxReadStream;
import io.vertx.core.Handler;
import io.vertx.core.streams.ReadStream;

import java.util.function.Predicate;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class FilterRxReadStream<T> extends RxReadStreamBase<T> {

  private static final Handler<Void> NOOP_HANDLER = v -> {};

  private final ReadStream<T> source;
  private final Predicate<? super T> predicate;
  private Handler<T> handler;
  private Handler<Void> endHandler = NOOP_HANDLER;
  private Status status = Status.READY;

  public FilterRxReadStream(ReadStream<T> source, Predicate<? super T> predicate) {
    this.source = source;
    this.predicate = predicate;
  }

  private synchronized void handleEnd(Void event) {
    status = Status.DONE;
    endHandler.handle(null);
  }

  @Override
  public RxReadStream<T> pause() {
    source.pause();
    return this;
  }

  @Override
  public RxReadStream<T> resume() {
    source.resume();
    return this;
  }

  @Override
  public synchronized RxReadStream<T> handler(Handler<T> h) {
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
        if (predicate.test(event)) {
          h.handle(event);
        }
      });
    }
    return this;
  }

  @Override
  public synchronized RxReadStream<T> endHandler(Handler<Void> handler) {
    if (status == Status.READY && handler != null) {
      endHandler = handler;
    }
    return this;
  }
}
