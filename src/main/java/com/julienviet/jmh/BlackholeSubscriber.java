package com.julienviet.jmh;

import org.openjdk.jmh.infra.Blackhole;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class BlackholeSubscriber implements Subscriber<Object> {

  final Blackhole blackhole;

  public BlackholeSubscriber(Blackhole blackhole) {
    this.blackhole = blackhole;
  }

  @Override
  public void onSubscribe(Subscription s) {
    s.request(Long.MAX_VALUE);
  }

  @Override
  public void onNext(Object o) {
    blackhole.consume(o);
  }

  @Override
  public void onError(Throwable t) {
    blackhole.consume(t);
  }

  @Override
  public void onComplete() {
    blackhole.consume(true);
  }
}
