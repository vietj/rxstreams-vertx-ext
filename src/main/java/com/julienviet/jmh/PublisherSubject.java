package com.julienviet.jmh;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.function.Consumer;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class PublisherSubject<T> implements Publisher<T>, Consumer<T> {

  private Subscriber<? super T> subscriber;
  private long requested;

  @Override
  public void accept(T t) {
    if (subscriber == null || requested < 1) {
      throw new IllegalStateException();
    }
    subscriber.onNext(t);
  }

  @Override
  public void subscribe(Subscriber<? super T> s) {
    subscriber = s;
    s.onSubscribe(new Subscription() {
      @Override
      public void request(long n) {
        requested = n;
      }
      @Override
      public void cancel() {
        subscriber = null;
        requested = 0;
      }
    });
  }
}
