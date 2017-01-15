package com.julienviet.streams;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class RangeTest {

  @Test
  public void testSubscribe() {
    RxReadStream<Integer> stream = RxReadStream.range(0, 5);
    List<Integer> obj = new ArrayList<>();
    stream.endHandler(v -> {
      obj.add(null);
    });
    assertEquals(0, obj.size());
    stream.handler(obj::add);
    assertEquals(Arrays.asList(0, 1, 2, 3, 4, null), obj);
  }

  @Test
  public void testPause() {
    RxReadStream<Integer> stream = RxReadStream.range(0, 5);
    stream.pause();
    List<Integer> obj = new ArrayList<>();
    stream.endHandler(v -> {
      obj.add(null);
    });
    stream.handler(obj::add);
    assertEquals(0, obj.size());
    stream.resume();
    assertEquals(Arrays.asList(0, 1, 2, 3, 4, null), obj);
  }

  @Test
  public void testPauseInHandler() {
    RxReadStream<Integer> stream = RxReadStream.range(0, 5);
    List<Integer> obj = new ArrayList<>();
    stream.endHandler(v -> {
      obj.add(null);
    });
    stream.handler(i -> {
      obj.add(i);
      stream.pause();
    });
    List<Integer> expected = new ArrayList<>();
    for (int i = 0;i < 5;i++) {
      expected.add(i);
      assertEquals(expected, obj);
      stream.resume();
    }
    expected.add(null);
    assertEquals(expected, obj);
  }
}
