package com.julienviet.streams;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class ConcatMapTest {

  @Test
  public void testSubscribe() {
    ReadStreamSubject<Integer> subject = new ReadStreamSubject<>();
    RxReadStream<Integer> stream = RxReadStream.create(subject).concatMap(RxReadStream::just);
    List<Integer> obj = new ArrayList<>();
    stream.endHandler(v -> {
      obj.add(null);
    });
    assertEquals(0, obj.size());
    stream.handler(t -> {
      assertTrue(subject.isPaused());
      obj.add(t);
    });
    subject.handle(0);
    assertEquals(Arrays.asList(0), obj);
    subject.end();
    assertEquals(Arrays.asList(0, null), obj);
  }
}
