package server;

import tech.realcpf.server.UDSServer;

import java.io.IOException;
import java.net.StandardProtocolFamily;
import java.net.UnixDomainSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class TestClient {
  public static void main(String[] args) throws IOException {
    String p = "/tmp/server.sock";
    try(SocketChannel client = SocketChannel.open(StandardProtocolFamily.UNIX)) {
      client.connect(UnixDomainSocketAddress.of(p));
      ByteBuffer buffer = ByteBuffer.allocate(1024);
      String plugName = "SimpleUtil";
      byte[] plugByte = plugName.getBytes(StandardCharsets.UTF_8);
      String funcName = "f1";
      byte[] funcByte = funcName.getBytes(StandardCharsets.UTF_8);
      String param = "params";
      byte[] paramByte = param.getBytes(StandardCharsets.UTF_8);
      buffer.putInt(plugByte.length);
      buffer.putInt(funcByte.length);
      buffer.put(plugByte);
      buffer.put(funcByte);
      buffer.put(paramByte);
      buffer.flip();
      client.write(buffer);
      System.out.println("send done");
    }

  }
}
