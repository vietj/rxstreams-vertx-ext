package com.julienviet.streams;

import com.julienviet.jmh.PublisherSubject;
import io.reactivex.Flowable;
import io.vertx.core.Handler;
import org.junit.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class SomeTest {

  @Test
  public void testBar() {
    ReadStreamSubject<Integer> subject = new ReadStreamSubject<>();
    RxReadStream<Integer> stream = RxReadStream.create(subject).map(i -> i * 2);
    stream.handler(new Handler<Integer>() {
      @Override
      public void handle(Integer event) {
      }
    });
    subject.handle(3);
  }

  @Test
  public void testFoo() {



    PublisherSubject<Integer> subject = new PublisherSubject<>();
    Flowable<Integer> flowable = Flowable.unsafeCreate(subject).map(i -> i * 2).map(i -> i / 2).map(i -> i + 1);
    flowable.subscribe(new Subscriber<Integer>() {
      @Override
      public void onSubscribe(Subscription s) {
        s.request(100000000);
      }
      @Override
      public void onNext(Integer o) {

      }

      @Override
      public void onError(Throwable t) {

      }

      @Override
      public void onComplete() {

      }
    });
    subject.accept(3);
  }
}
