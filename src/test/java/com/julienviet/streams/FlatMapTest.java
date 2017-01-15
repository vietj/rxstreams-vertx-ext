package com.julienviet.streams;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class FlatMapTest {

  @Test
  public void testSubscribe() {
    Map<Integer, ReadStreamSubject<Integer>> mapped = new HashMap<>();
    ReadStreamSubject<Integer> subject = new ReadStreamSubject<>();
    RxReadStream<Integer> stream = RxReadStream.create(subject).flatMap(v -> {
      ReadStreamSubject<Integer> sub = new ReadStreamSubject<>();
      mapped.put(v, sub);
      return sub;
    });
    List<Integer> obj = new ArrayList<>();
    stream.endHandler(v -> {
      obj.add(null);
    });
    assertEquals(0, obj.size());
    stream.handler(t -> {
      assertFalse(subject.isPaused());
      obj.add(t);
    });
    subject.handle(0);
    assertEquals(Arrays.asList(), obj);
    mapped.get(0).handle(4);
    assertEquals(Arrays.asList(4), obj);
    subject.handle(1);
    assertEquals(Arrays.asList(4), obj);
    mapped.get(1).handle(5);
    assertEquals(Arrays.asList(4, 5), obj);
    subject.end();
    assertEquals(Arrays.asList(4, 5), obj);
    mapped.get(1).end();
    assertEquals(Arrays.asList(4, 5), obj);
    mapped.get(0).handle(6);
    assertEquals(Arrays.asList(4, 5, 6), obj);
    mapped.get(0).end();
    assertEquals(Arrays.asList(4, 5, 6, null), obj);
  }

  @Test
  public void testPauseWithConcurrency() {
    Map<Integer, ReadStreamSubject<Integer>> mapped = new HashMap<>();
    ReadStreamSubject<Integer> subject = new ReadStreamSubject<>();
    RxReadStream<Integer> stream = RxReadStream.create(subject).flatMap(v -> {
      ReadStreamSubject<Integer> sub = new ReadStreamSubject<>();
      mapped.put(v, sub);
      return sub;
    });
    List<Integer> obj = new ArrayList<>();
    stream.endHandler(v -> {
      obj.add(null);
    });
    assertEquals(0, obj.size());
    stream.handler(t -> {
      assertFalse(subject.isPaused());
      obj.add(t);
    });
    subject.handle(0);
    assertFalse(subject.isPaused());
    subject.handle(1);
    assertFalse(subject.isPaused());
    subject.handle(2);
    assertFalse(subject.isPaused());
    subject.handle(3);
    assertTrue(subject.isPaused());
    mapped.get(0).end();
    assertTrue(subject.isPaused());
    mapped.get(1).end();
    assertFalse(subject.isPaused());
  }

  @Test
  public void testSendMany() {
    ReadStreamSubject<Integer> subject = new ReadStreamSubject<>();
    RxReadStream<Integer> stream = RxReadStream.create(subject).flatMap(v -> RxReadStream.range(0, 10));
    List<Integer> obj = new ArrayList<>();
    AtomicInteger done = new AtomicInteger();
    stream.endHandler(v -> done.incrementAndGet());
    stream.handler(v -> {
      assertFalse(subject.isPaused());
      obj.add(v);
    });
    for (int i = 0;i < 10;i++) {
      subject.handle(i);
    }
    assertEquals(0, done.get());
    List<Integer> expected = new ArrayList<>();
    for (int i = 0;i < 10;i++) {
      for (int j = 0;j < 10;j++) {
        expected.add(j);
      }
    }
    assertEquals(expected, obj);
    subject.end();
    assertEquals(1, done.get());
  }

  @Test
  public void testPause() {
    Map<Integer, ReadStreamSubject<Integer>> mapped = new HashMap<>();
    ReadStreamSubject<Integer> subject = new ReadStreamSubject<>();
    RxReadStream<Integer> stream = RxReadStream.create(subject).flatMap(v -> {
      ReadStreamSubject<Integer> sub = new ReadStreamSubject<>();
      mapped.put(v, sub);
      return sub;
    });
    List<Integer> obj = new ArrayList<>();
    stream.endHandler(v -> {
      obj.add(null);
    });
    assertEquals(0, obj.size());
    stream.handler(t -> {
      assertFalse(subject.isPaused());
      obj.add(t);
    });
    subject.handle(0);
    assertFalse(subject.isPaused());
    subject.handle(1);
    assertFalse(subject.isPaused());
    subject.handle(2);
    assertFalse(subject.isPaused());
    subject.handle(3);
    assertTrue(subject.isPaused());
    stream.resume();
    assertTrue(subject.isPaused());
/*
    mapped.get(0).end();
    assertTrue(subject.isPaused());
    mapped.get(1).end();
    assertFalse(subject.isPaused());
*/
  }
}
