package com.julienviet.streams.impl;

import com.julienviet.streams.RxReadStream;
import io.vertx.core.Handler;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class RangeRxReadStream extends RxReadStreamBase<Integer> {

  private static final Handler<Void> NOOP_HANDLER = v -> {};

  private Status status = Status.READY;
  private boolean paused;
  private final int from;
  private final int to;
  private int current;
  private Handler<Void> endHandler = NOOP_HANDLER;
  private Handler<Integer> handler;

  public RangeRxReadStream(int from, int to) {
    this.from = from;
    this.to = to;
  }

  @Override
  public synchronized RxReadStream<Integer> pause() {
    paused = true;
    return this;
  }

  @Override
  public synchronized RxReadStream<Integer> resume() {
    if (paused) {
      paused = false;
      checkPending();
    }
    return this;
  }

  private void checkPending() {
    while (current < to && !paused) {
      handler.handle(current++);
    }
    if (current == to) {
      handler = null;
      status = Status.COMPLETED;
      if (!paused) {
        status = Status.DONE;
        endHandler.handle(null);
        endHandler = null;
      }
    }
  }

  @Override
  public synchronized RxReadStream<Integer> handler(Handler<Integer> h) {
    if (h != null) {
      if (status != Status.READY) {
        throw new IllegalStateException();
      }
      if (handler != null) {
        throw new IllegalStateException();
      }
      handler = h;
      current = from;
      checkPending();
      return this;
    }
    return this;
  }

  @Override
  public RxReadStream<Integer> endHandler(Handler<Void> handler) {
    if (status == Status.READY && handler != null) {
      endHandler = handler;
    }
    return this;
  }
}
