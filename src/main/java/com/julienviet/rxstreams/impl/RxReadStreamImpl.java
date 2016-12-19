package com.julienviet.rxstreams.impl;

import com.julienviet.rxstreams.RxReadStream;
import io.vertx.core.Handler;
import io.vertx.core.streams.ReadStream;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class RxReadStreamImpl<T> extends RxReadStreamBase<T> {

  private final ReadStream<T> stream;

  public RxReadStreamImpl(ReadStream<T> stream) {
    this.stream = stream;
  }

  @Override
  protected ReadStream<T> stream() {
    return stream;
  }

  @Override
  public RxReadStream<T> pause() {
    stream.pause();
    return this;
  }

  @Override
  public RxReadStream<T> resume() {
    stream.resume();
    return this;
  }

  @Override
  public RxReadStream<T> exceptionHandler(Handler<Throwable> handler) {
    stream.exceptionHandler(handler);
    return this;
  }

  @Override
  public RxReadStream<T> handler(Handler<T> handler) {
    stream.handler(handler);
    return this;
  }

  @Override
  public RxReadStream<T> endHandler(Handler<Void> endHandler) {
    stream.endHandler(endHandler);
    return this;
  }
}
