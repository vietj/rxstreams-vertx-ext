package com.julienviet.jmh;

import io.vertx.core.Handler;
import org.openjdk.jmh.infra.Blackhole;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class BlackholeHandler<T> implements Handler<T> {

  final Blackhole blackhole;

  public BlackholeHandler(Blackhole blackhole) {
    this.blackhole = blackhole;
  }

  @Override
  public void handle(T event) {
    blackhole.consume(event);
  }
}
