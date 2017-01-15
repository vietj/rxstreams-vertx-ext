package com.julienviet.streams.impl;

import com.julienviet.streams.RxReadStream;
import io.vertx.core.Handler;
import io.vertx.core.streams.ReadStream;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class FlatMapRxReadStream<T, R> extends RxReadStreamBase<R> {

  private static final Handler<Void> NOOP_HANDLER = v -> {};

  private static final int CONCURRENT_STREAMS_HIGH = 4;
  private static final int CONCURRENT_STREAMS_LOW = CONCURRENT_STREAMS_HIGH / 2;

  private final ReadStream<T> source;
  private final Function<? super T, ? extends ReadStream<? extends R>> mapper;
  private Handler<R> handler;
  private Handler<Void> endHandler = NOOP_HANDLER;
  private Set<ReadStream<? extends R>> streams = new HashSet<>(CONCURRENT_STREAMS_HIGH);
  private Status status = Status.READY;
  private boolean paused;

  public FlatMapRxReadStream(ReadStream<T> source, Function<? super T, ? extends ReadStream<? extends R>> mapper) {
    this.source = source;
    this.mapper = mapper;
  }

  private synchronized void handleSourceEvent(T sourceEvent) {
    ReadStream<? extends R> mapped = mapper.apply(sourceEvent);
    streams.add(mapped);
    if (streams.size() == CONCURRENT_STREAMS_HIGH) {
      source.pause();
    }
    mapped.endHandler(v -> {
      synchronized (FlatMapRxReadStream.this) {
        streams.remove(mapped);
        if (streams.size() == CONCURRENT_STREAMS_LOW) {
          source.resume();
        } else {
          checkEnd();
        }
      }
    });
    mapped.handler(event -> {
      synchronized (FlatMapRxReadStream.this) {
        handler.handle(event);
      }
    });
    mapped.resume();
  }

  private synchronized  void handleSourceEnd(Void v) {
    status = Status.COMPLETED;
    checkEnd();
  }

  private void checkEnd() {
    if (status == Status.COMPLETED && streams.isEmpty()) {
      status = Status.DONE;
      endHandler.handle(null);
      endHandler = null;
    }
  }

  @Override
  public synchronized RxReadStream<R> pause() {
    if (!paused) {
      paused = true;
      source.pause();
      streams.forEach(ReadStream::pause);
    }
    return this;
  }

  @Override
  public synchronized RxReadStream<R> resume() {
    if (paused) {
      paused = false;
      streams.forEach(ReadStream::resume);
      if (streams.size() <= CONCURRENT_STREAMS_LOW) {
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
