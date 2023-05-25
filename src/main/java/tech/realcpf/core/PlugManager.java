package tech.realcpf.core;

import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class PlugManager {
  private static volatile boolean initFlag;
  private static final CopyOnWriteArrayList<PlugIns> loadedPlugInsList = new CopyOnWriteArrayList<>();
  private static final Map<String,PlugIns> plugRouteMap = new ConcurrentSkipListMap<>();

  public static CompletableFuture doRoute(RouteInfo info){
    init();
    String plugName = info.getPlugName();
    if (plugRouteMap.containsKey(plugName)) {
      PlugIns plugIns = plugRouteMap.get(plugName);
      return plugIns.action(info.getFuncName(),info.getParam());
    }
    return new CompletableFuture();
  }
  private static void init(){
    if (initFlag) {
      return;
    }
    ServiceLoader<PlugIns> serviceLoader = ServiceLoader.load(PlugIns.class);
    Iterator<PlugIns> plugInsIterator = serviceLoader.iterator();
    while (plugInsIterator.hasNext()) {
      PlugIns plugIns = plugInsIterator.next();
      plugIns.loadFunc();
      loadedPlugInsList.add(plugIns);
      plugRouteMap.put(plugIns.plugName(),plugIns);
    }
    initFlag = true;
  }
}
