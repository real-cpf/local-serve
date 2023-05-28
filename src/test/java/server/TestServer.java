package server;

import tech.realcpf.server.UDSServer;

public class TestServer {
  public static void main(String[] args) {
    UDSServer server = new UDSServer("/tmp/server.sock",4);
    server.run();
  }
}
