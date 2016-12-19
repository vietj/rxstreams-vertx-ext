## Rx Streams for Vert.x

Simple stream composition for Vert.x playground.

RxJava2 uses non blocking / volatile synchronization, this uses synchronized blocks, assuming this runs
 under biased locking and the stream is accessed by the same thread.

### Running benchmarks

```
> mvn clean package
> java -jar target/benchmarks.jar
```