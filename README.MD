# HttpRouter

A fast and lightweight HTTP router for Java, designed for high-throughput and low-allocation routing in microservices, 
APIs, and real-time systems.

## Features

- **Performance**  
  Fast immutable tree build & lookup algorithm that avoids unnecessary substring allocations.

- **Memory**  
  Runtime allocations minimized to around 50-100 bytes on average, keeping your GC in peace.

- **Lightweight**  
  Uses only `java.lang`, `java.util` packages with no external dependencies.

- **Size**  
  The entire library has only 6 classes and fits into a single 10KB jar.

## Performance

### Throughput

#### 1) Workstation Machine

**Target**: maximum throughout ops/ms.   
**Environment**: MacBook Pro (Apple M1 Pro, 10-core CPU, 32GB RAM), Amazon JDK 22.  
**Threading**: single-threaded  
  ```text
Benchmark                                                Mode  Cnt       Score     Error   Units
HttpRouterBenchmark.hit_one_depth                       thrpt  100  128922,353 ± 764,232  ops/ms
HttpRouterBenchmark.hit_two_depth                       thrpt  100   44882,135 ± 140,090  ops/ms
HttpRouterBenchmark.hit_three_depth                     thrpt  100   32481,811 ± 173,771  ops/ms
HttpRouterBenchmark.hit_four_depth                      thrpt  100   27381,213 ± 102,517  ops/ms
HttpRouterBenchmark.hit_two_depth_last_parameterized    thrpt  100   20023,861 ±  83,079  ops/ms
HttpRouterBenchmark.hit_three_depth_last_parameterized  thrpt  100   13732,372 ±  35,894  ops/ms
HttpRouterBenchmark.miss_one_depth_last_unknown         thrpt  100  135652,036 ± 232,657  ops/ms
HttpRouterBenchmark.miss_two_depth_last_unknown         thrpt  100   41189,774 ± 558,631  ops/ms
HttpRouterBenchmark.miss_three_depth_last_unknown       thrpt  100   33782,991 ±  71,319  ops/ms
```

_Note: All benchmarks use ops/ms as the unit for more readable output._ 

#### 2) Node Machine  

**Target**: approximately throughput ops/ms in a typical Kubernetes/AWS node environment. 
_Note: The node used for this test is a legacy machine; modern cloud instances will typically offer significantly better performance._  
**Environment**: Node (AMD Phenom X4 955 BE, 4-core CPU, 8GB RAM), Amazon JDK 22.  
**Threading**: single-threaded
```text
Benchmark                                               Mode  Cnt      Score     Error   Units
HttpRouterBenchmark.hit_one_depth                      thrpt  100  17677,044 ± 657,112  ops/ms
HttpRouterBenchmark.hit_two_depth                      thrpt  100   9623,715 ±  56,123  ops/ms
HttpRouterBenchmark.hit_three_depth                    thrpt  100   7966,197 ± 165,554  ops/ms
HttpRouterBenchmark.hit_four_depth                     thrpt  100   6823,209 ± 101,651  ops/ms
HttpRouterBenchmark.hit_two_depth_last_parameterized   thrpt  100   7776,998 ±  75,561  ops/ms
HttpRouterBenchmark.hit_three_depth_last_parameterized thrpt  100   6147,185 ±  35,011  ops/ms
HttpRouterBenchmark.miss_one_depth_last_unknown        thrpt  100  40999,688 ± 236,441  ops/ms
HttpRouterBenchmark.miss_two_depth_last_unknown        thrpt  100   9620,996 ± 525,120  ops/ms
HttpRouterBenchmark.miss_three_depth_last_unknown      thrpt  100   8562,979 ±  67,534  ops/ms
```
_Note: All benchmarks use ops/ms as the unit for more readable output._

### GC

**Target**: approximate memory footprint in bytes per operation (B/op). _Note: Focuses on per-operation memory allocation rather than overall heap consumption._  
**Environment**: not environment dependent  _(isolated mode)_.  
**Threading**: single-threaded _(isolated mode)_.
```text
Benchmark                                                                    Mode  Cnt     Score     Error   Units
HttpRouterBenchmark.hit_one_depth:gc.alloc.rate.norm                        thrpt  100     24,000 ±   0,001  B/op
HttpRouterBenchmark.hit_two_depth:gc.alloc.rate.norm                        thrpt  100     24,000 ±   0,001  B/op
HttpRouterBenchmark.hit_three_depth:gc.alloc.rate.norm                      thrpt  100     24,000 ±   0,001  B/op
HttpRouterBenchmark.hit_four_depth:gc.alloc.rate.norm                       thrpt  100     24,000 ±   0,001  B/op
HttpRouterBenchmark.hit_two_depth_last_parameterized:gc.alloc.rate.norm     thrpt  100    184,000 ±   0,001  B/op
HttpRouterBenchmark.hit_three_depth_last_parameterized:gc.alloc.rate.norm   thrpt  100    184,001 ±   0,001  B/op
HttpRouterBenchmark.miss_one_depth_last_unknown:gc.alloc.rate.norm          thrpt  100     24,000 ±   0,001  B/op
HttpRouterBenchmark.miss_two_depth_last_unknown:gc.alloc.rate.norm          thrpt  100     24,000 ±   0,001  B/op
HttpRouterBenchmark.miss_three_depth_last_unknown:gc.alloc.rate.norm        thrpt  100     24,000 ±   0,001  B/op
```

_Note: All benchmarks use B/op as the unit for more readable output._