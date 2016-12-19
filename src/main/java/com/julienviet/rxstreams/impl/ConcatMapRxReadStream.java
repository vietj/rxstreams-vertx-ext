package com.julienviet.rxstreams.impl;

import com.julienviet.rxstreams.RxReadStream;
import io.vertx.core.Handler;
import io.vertx.core.streams.ReadStream;

import java.util.function.Function;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class ConcatMapRxReadStream<T, R> extends RxReadStreamBase<R> {

  private static final Handler<Void> NOOP_HANDLER = v -> {};

  private final ReadStream<T> source;
  private final Function<? super T, ? extends ReadStream<? extends R>> mapper;
  private Handler<R> handler;
  private Handler<Void> endHandler = NOOP_HANDLER;
  private ReadStream<? extends R> current;
  private Status status = Status.READY;
  private boolean paused;

  public ConcatMapRxReadStream(ReadStream<T> source, Function<? super T, ? extends ReadStream<? extends R>> mapper) {
    this.source = source;
    this.mapper = mapper;
  }

  public synchronized void handleSourceEvent(T event) {
    current = mapper.apply(event);
    source.pause();
    current.endHandler(this::handleEnd);
    if (paused) {
      current.pause();
    }
    current.handler(this::handleEvent);
  }

  private synchronized void handleSourceEnd(Void v) {
    handler = null;
    if (paused) {
      status = Status.COMPLETED;
      return;
    }
    status = Status.DONE;
    endHandler.handle(null);
    endHandler = null;
  }

  private synchronized void handleEvent(R event) {
    if (paused) {
      throw new UnsupportedOperationException("Not implemented");
    }
    handler.handle(event);
  }

  private synchronized void handleEnd(Void v) {
    current = null;
    if (!paused) {
      source.resume();
    }
  }

  @Override
  public synchronized RxReadStream<R> pause() {
    if (!paused) {
      paused = true;
      if (current != null) {
        current.pause();
      } else {
        source.pause();
      }
    }
    return this;
  }

  @Override
  public synchronized RxReadStream<R> resume() {
    if (paused) {
      paused = false;
      if (current != null) {
        current.resume();
      } else {
        source.resume();
      }
    }
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
      if (paused) {
        source.pause();
      } else {
        source.resume();
      }
      source.endHandler(this::handleSourceEnd);
      source.handler(this::handleSourceEvent);
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
