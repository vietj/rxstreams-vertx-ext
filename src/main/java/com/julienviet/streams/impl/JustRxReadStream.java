package com.julienviet.streams.impl;

import com.julienviet.streams.RxReadStream;
import io.vertx.core.Handler;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class JustRxReadStream<T> extends RxReadStreamBase<T> {

  private static final Handler<Void> NOOP_HANDLER = v -> {};

  private Status status = Status.READY;
  private boolean paused;
  private Handler<T> handler;
  private Handler<Void> endHandler = NOOP_HANDLER;
  private final T value;

  public JustRxReadStream(T value) {
    this.value = value;
  }

  @Override
  public synchronized RxReadStream<T> pause() {
    paused = true;
    return this;
  }

  @Override
  public synchronized RxReadStream<T> resume() {
    if (paused) {
      paused = false;
      if (status == Status.READY) {
        status = Status.COMPLETED;
        handler.handle(value);
        handler = null;
      }
      if (!paused) {
        status = Status.DONE;
        endHandler.handle(null);
        endHandler = null;
      }
    }
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
      if (paused) {
        handler = h;
        return this;
      }
      status = Status.COMPLETED;
      h.handle(value);
      if (paused) {
        return this;
      }
      status = Status.DONE;
      endHandler.handle(null);
      endHandler = null;
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
