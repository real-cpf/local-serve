package tech.realcpf;


import tech.realcpf.core.PlugManager;
import tech.realcpf.core.RouteInfo;

import java.util.concurrent.*;

public class Main {

  public static void main(String[] args) {
    RouteInfo routeInfo = new RouteInfo();
    routeInfo.setPlugName("SimpleUtil");
    routeInfo.setFuncName("f1");
    routeInfo.setParam(new Object[]{"param for f1"});
    CompletableFuture future = PlugManager.doRoute(routeInfo);
    future.whenComplete((res,err) ->{
      if (err == null){
        System.out.println(res);
      }else {
        System.out.println(err);
      }

    });
  }
}
