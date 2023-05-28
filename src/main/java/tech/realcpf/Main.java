package tech.realcpf;


import tech.realcpf.core.PlugManager;
import tech.realcpf.core.RouteInfo;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.StandardProtocolFamily;
import java.net.UnixDomainSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.concurrent.*;

public class Main {

  public static void main1(String[] args) {
    RouteInfo routeInfo = new RouteInfo();
    routeInfo.setPlugName("SimpleUtil");
    routeInfo.setFuncName("f1");
    routeInfo.setParam(new Object[]{"param for f1"});
    CompletableFuture future = PlugManager.doRoute(routeInfo);
    future.whenComplete((res, err) -> {
      if (err == null) {
        System.out.println(res);
      } else {
        System.out.println(err);
      }

    });
  }
}
