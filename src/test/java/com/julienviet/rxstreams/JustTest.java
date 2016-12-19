package com.julienviet.rxstreams;

import com.julienviet.rxstreams.RxReadStream;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class JustTest {

  @Test
  public void testSubscribe() {
    RxReadStream<Integer> stream = RxReadStream.just(1);
    List<Integer> obj = new ArrayList<>();
    stream.endHandler(v -> {
      obj.add(null);
    });
    assertEquals(0, obj.size());
    stream.handler(obj::add);
    assertEquals(Arrays.asList(1, null), obj);
  }

  @Test
  public void testPause() {
    RxReadStream<Integer> stream = RxReadStream.just(1);
    stream.pause();
    List<Integer> obj = new ArrayList<>();
    stream.endHandler(v -> {
      obj.add(null);
    });
    stream.handler(obj::add);
    assertEquals(0, obj.size());
    stream.resume();
    assertEquals(Arrays.asList(1, null), obj);
  }

  @Test
  public void testPauseInHandler() {
    RxReadStream<Integer> stream = RxReadStream.just(1);
    List<Integer> obj = new ArrayList<>();
    stream.endHandler(v -> {
      obj.add(null);
    });
    stream.handler(i -> {
      stream.pause();
      obj.add(i);
    });
    assertEquals(1, obj.size());
    assertEquals(Arrays.asList(1), obj);
    stream.resume();
    assertEquals(Arrays.asList(1, null), obj);
  }
}
