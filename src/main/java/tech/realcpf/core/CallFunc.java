package tech.realcpf.core;

import java.util.concurrent.CompletableFuture;

public interface CallFunc {
  CompletableFuture call(Object[] param);
}
