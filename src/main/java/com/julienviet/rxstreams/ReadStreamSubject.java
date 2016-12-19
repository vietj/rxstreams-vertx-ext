package com.julienviet.rxstreams;

import com.julienviet.rxstreams.impl.Status;
import io.vertx.core.Handler;
import io.vertx.core.streams.ReadStream;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class ReadStreamSubject<T> implements ReadStream<T>, Handler<T> {

  private Handler<T> handler;
  private Handler<Void> endHandler;
  private boolean paused;
  private Status status = Status.READY;

  @Override
  public synchronized void handle(T event) {
    if (handler == null) {
      throw new IllegalStateException();
    }
    if (paused) {
      throw new IllegalStateException();
    }
    handler.handle(event);
  }

  public synchronized boolean isPaused() {
    return paused;
  }

  public void end() {
    if (status == Status.READY) {
      status = Status.DONE;
      if (endHandler != null) {
        endHandler.handle(null);
        endHandler = null;
      }
    }
  }

  @Override
  public synchronized ReadStream<T> handler(Handler<T> h) {
    handler = h;
    return this;
  }

  @Override
  public ReadStream<T> exceptionHandler(Handler<Throwable> handler) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ReadStream<T> pause() {
    paused = true;
    return this;
  }

  @Override
  public ReadStream<T> resume() {
    paused = false;
    return this;
  }

  @Override
  public ReadStream<T> endHandler(Handler<Void> handler) {
    endHandler = handler;
    return this;
  }
}
