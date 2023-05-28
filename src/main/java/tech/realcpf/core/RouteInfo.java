package tech.realcpf.core;

import java.util.Arrays;

public class RouteInfo {
  private String plugName;
  private String funcName;
  private Object[] param;

  public String getPlugName() {
    return plugName;
  }

  public void setPlugName(String plugName) {
    this.plugName = plugName;
  }

  public String getFuncName() {
    return funcName;
  }

  public void setFuncName(String funcName) {
    this.funcName = funcName;
  }

  public Object[] getParam() {
    return param;
  }

  public void setParam(Object[] param) {
    this.param = param;
  }

  @Override
  public String toString() {
    return "RouteInfo{" +
      "plugName='" + plugName + '\'' +
      ", funcName='" + funcName + '\'' +
      ", param=" + Arrays.toString(param) +
      '}';
  }
}
