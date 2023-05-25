package tech.realcpf.core;

public class RouteInfo {
  private String plugName;

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

  private String funcName;
  private Object[] param;
}
