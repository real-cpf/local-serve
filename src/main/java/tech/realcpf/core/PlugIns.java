package tech.realcpf.core;

import java.util.concurrent.CompletableFuture;

public interface PlugIns {

  void loadFunc();
  String plugName();
  CompletableFuture action(String funcName, Object[] param);
}
