package tech.realcpf.server;

import java.util.concurrent.*;

public class LSExecutors {
  private static final BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(128);

  public static ExecutorService newFixedThreadPool(int nThreads) {
    return new ThreadPoolExecutor(nThreads, nThreads,
      0L, TimeUnit.MILLISECONDS,
      workQueue, new LSThreadFactory(), (r, executor) -> executor.execute(r));
  }
}
