/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.julienviet.jmh;

import com.julienviet.streams.ReadStreamSubject;
import com.julienviet.streams.RxReadStream;
import io.reactivex.Flowable;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Thread)
public class FlatMapBenchmark extends BenchmarkBase {

  @State(Scope.Thread)
  public static class RxJava2BaselineState {

    private Integer i = 1;
    private PublisherSubject<Integer> subject;
    private Flowable<Integer> flowable;

    @Setup
    public void setup(Blackhole blackhole) {
      subject = new PublisherSubject<>();
      flowable = Flowable.unsafeCreate(subject);
      flowable.subscribe(new BlackholeSubscriber(blackhole));
    }
  }

  /**
   * Note this benchmark performs much better because RxJava2 casts Flowable.just(x) as
   * a Callable that returns x and no subscription ever occurs.
   */
  @State(Scope.Thread)
  public static class RxJava2FlatMapJustState {

    private Integer i = 1;
    private PublisherSubject<Integer> subject;
    private Flowable<Integer> flowable;

    @Setup
    public void setup(Blackhole blackhole) {
      subject = new PublisherSubject<>();
      flowable = Flowable.unsafeCreate(subject).flatMap(Flowable::just);
      flowable.subscribe(new BlackholeSubscriber(blackhole));
    }
  }

  @State(Scope.Thread)
  public static class RxJava2FlatMapRangeState {

    private Integer i = 1;
    private PublisherSubject<Integer> subject;
    private Flowable<Integer> flowable;

    @Setup
    public void setup(Blackhole blackhole) {
      subject = new PublisherSubject<>();
      flowable = Flowable.unsafeCreate(subject).flatMap(v -> Flowable.range(1, 10));
      flowable.subscribe(new BlackholeSubscriber(blackhole));
    }
  }

  @State(Scope.Thread)
  public static class RxStreamsBaselineState {

    private Integer i = 1;
    private ReadStreamSubject<Integer> subject;
    private RxReadStream<Integer> stream;

    @Setup
    public void setup(Blackhole blackhole) {
      subject = new ReadStreamSubject<>();
      stream = RxReadStream.create(subject);
      stream.handler(new BlackholeHandler<>(blackhole));
    }
  }

  @State(Scope.Thread)
  public static class RxStreamsFlatMapJustState {

    private Integer i = 1;
    private ReadStreamSubject<Integer> subject;
    private RxReadStream<Integer> stream;

    @Setup
    public void setup(Blackhole blackhole) {
      subject = new ReadStreamSubject<>();
      stream = RxReadStream.create(subject).flatMap(RxReadStream::just);
      stream.handler(new BlackholeHandler<>(blackhole));
    }
  }

  @State(Scope.Thread)
  public static class RxStreamsFlatMapRangeState {

    private Integer i = 1;
    private ReadStreamSubject<Integer> subject;
    private RxReadStream<Integer> stream;

    @Setup
    public void setup(Blackhole blackhole) {
      subject = new ReadStreamSubject<>();
      stream = RxReadStream.create(subject).flatMap(v -> RxReadStream.range(1, 10));
      stream.handler(new BlackholeHandler<>(blackhole));
    }
  }

  @Benchmark
  public void rxJava2Baseline(RxJava2BaselineState state) {
    state.subject.accept(state.i);
  }

  @Benchmark
  public void rxJava2FlatMapJust(RxJava2FlatMapJustState state) {
    state.subject.accept(state.i);
  }

  @Benchmark
  public void rxJava2FlatMapRange(RxJava2FlatMapRangeState state) {
    state.subject.accept(state.i);
  }

  @Benchmark
  public void rxStreamsBaseline(RxStreamsBaselineState state) {
    state.subject.handle(state.i);
  }

  @Benchmark
  public void rxStreamsFlatMapJust(RxStreamsFlatMapJustState state) {
    state.subject.handle(state.i);
  }

  @Benchmark
  public void rxStreamsFlatMapRange(RxStreamsFlatMapRangeState state) {
    state.subject.handle(state.i);
  }
}
