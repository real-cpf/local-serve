package tech.realcpf.listener;

import tech.realcpf.core.PlugManager;
import tech.realcpf.core.RouteInfo;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class RouteListener {
  private BlockingQueue<RouteInfo> queue;
  private AtomicBoolean running;
  public RouteListener(BlockingQueue<RouteInfo> queue) {
    this.queue = queue;
    this.running = new AtomicBoolean(Boolean.TRUE);
  }
  public void run(){
    while (this.running.get()){
      try {
        System.out.println("take");
        RouteInfo info = queue.take();
        CompletableFuture.runAsync(()->{PlugManager.doRoute(info);});
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
