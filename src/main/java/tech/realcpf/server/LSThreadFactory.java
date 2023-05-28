package tech.realcpf.server;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class LSThreadFactory implements ThreadFactory {
  private static final AtomicInteger poolNumber = new AtomicInteger(1);
  private final AtomicInteger threadNumber = new AtomicInteger(1);
  private final String namePrefix;

  public LSThreadFactory() {
    namePrefix = "LSpool-" +
      poolNumber.getAndIncrement() +
      "-thread-";
  }

  @Override
  public Thread newThread(Runnable r) {
    Thread t = new Thread(Thread.currentThread().getThreadGroup(), r,
      namePrefix + threadNumber.getAndIncrement(),
      0);
    if (t.isDaemon())
      t.setDaemon(false);
    if (t.getPriority() != Thread.NORM_PRIORITY)
      t.setPriority(Thread.NORM_PRIORITY);
    return t;
  }
}
