package tech.realcpf.simple;

import tech.realcpf.core.CallFunc;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class F1 implements CallFunc {
  @Override
  public CompletableFuture call(Object[] param) {
    final String s = String.format("f1:[%s]",Arrays.toString(param));
    return CompletableFuture.supplyAsync(()->{
      return s;
    });
  }
}
