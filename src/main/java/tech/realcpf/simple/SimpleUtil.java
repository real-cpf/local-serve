package tech.realcpf.simple;

import tech.realcpf.core.CallFunc;
import tech.realcpf.core.PlugIns;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentSkipListMap;

public class SimpleUtil implements PlugIns {
  private static final Map<String, CallFunc> funcRouteMap = new ConcurrentSkipListMap<>();
  @Override
  public String toString() {
    return "SimpleUtil";
  }

  @Override
  public void loadFunc() {
    funcRouteMap.put("f1",new F1());
  }

  @Override
  public String plugName() {
    return "SimpleUtil";
  }

  @Override
  public CompletableFuture action(String funcName, Object[] param) {
    if (funcRouteMap.containsKey(funcName)) {
      return funcRouteMap.get(funcName).call(param);
    }
    return CompletableFuture.supplyAsync(()->"");
  }
}
