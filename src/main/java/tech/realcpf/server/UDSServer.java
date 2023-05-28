package tech.realcpf.server;

import tech.realcpf.core.RouteInfo;
import tech.realcpf.listener.RouteListener;

import java.io.IOException;
import java.net.StandardProtocolFamily;
import java.net.UnixDomainSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class UDSServer {
  private final ExecutorService executor;

  private static final AtomicBoolean running = new AtomicBoolean(Boolean.TRUE);
  private static final ByteBuffer BLOCK_BUFFER = ByteBuffer.allocate(10240);
  private final String SERVER_PATH;
  private static final Selector selector;
  private final RouteListener routeListener;
  private final BlockingQueue<RouteInfo> queue;
  private final ServerSocketChannel serverChannel;

  {
    try {
      serverChannel = ServerSocketChannel.open(StandardProtocolFamily.UNIX);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  static {
    try {
      selector = Selector.open();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public UDSServer(String path, int poolSize) {
    this.SERVER_PATH = path;
    this.executor = LSExecutors.newFixedThreadPool(poolSize);
    this.queue = new LinkedBlockingQueue<>();
    this.routeListener = new RouteListener(this.queue);
  }

  public void run() {
    executor.execute(() -> {
      this.routeListener.run();
    });
    executor.execute(() -> {
      try {
        Path path = Path.of(SERVER_PATH);
        if (Files.exists(path)) {
          Files.deleteIfExists(path);
        }

        serverChannel.bind(UnixDomainSocketAddress.of(path));
        serverChannel.configureBlocking(false);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("starting... on " + SERVER_PATH);
        loopSelector();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });
  }

  private void read(SelectionKey key) throws IOException {
    final SocketChannel socketChannel = (SocketChannel) key.channel();
    boolean first = true;
    final List<byte[]> list = new LinkedList<>();
    do {
      int c = socketChannel.read(BLOCK_BUFFER);
      if (c < 1) {
        checkClosed(socketChannel, first);
        break;
      }
      BLOCK_BUFFER.flip();
      byte[] bytes = new byte[BLOCK_BUFFER.limit()];
      BLOCK_BUFFER.get(bytes);
      list.add(bytes);
      first = false;
    } while (true);
    BLOCK_BUFFER.clear();
    if (list.size() > 0) {
      executor.execute(() -> {
        decodeAndProcess(list, socketChannel);
      });
    }
    if (!first) {
      socketChannel.configureBlocking(false);
      socketChannel.register(selector, SelectionKey.OP_READ);
    }
  }

  private void decodeAndProcess(List<byte[]> data, SocketChannel channel) {
    int totalLen = data.stream().map(arr -> arr.length).reduce(0, (a, b) -> a + b).intValue();
    ByteBuffer total = ByteBuffer.allocate(totalLen);
    fillBytes(data, total);
    RouteInfo info = parse2Route(total);
    System.out.println(info);
    try {
      this.queue.put(info);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private static RouteInfo parse2Route(ByteBuffer total) {
    RouteInfo routeInfo = new RouteInfo();

    int plugLen = total.getInt();
    int funcLen = total.getInt();
    byte[] plugByte = new byte[plugLen];
    total.get(plugByte);
    String plugName = new String(plugByte);
    byte[] funcByte = new byte[funcLen];
    total.get(funcByte);
    String funcName = new String(funcByte);
    routeInfo.setPlugName(plugName);
    routeInfo.setFuncName(funcName);
    int last = total.limit() - total.position();
    byte[] param = new byte[last];
    total.get(param);
    String paramStr = new String(param);
    routeInfo.setParam(new Object[]{paramStr});
    System.out.println(routeInfo);
    return routeInfo;
  }

  private static void fillBytes(List<byte[]> data, ByteBuffer total) {
    for (byte[] bb : data) {
      total.put(bb);
    }
    total.flip();
  }

  private static void checkClosed(SocketChannel socketChannel, boolean first) throws IOException {
    if (first) {
      socketChannel.close();
      SelectionKey key = socketChannel.keyFor(selector);
      if (key != null) {
        key.cancel();
      }
    }
  }

  private void loopSelector() {
    while (running.get()) {
      try {
        selector.select();
        Iterator<SelectionKey> it = selector.selectedKeys().iterator();
        while (it.hasNext()) {
          SelectionKey key = it.next();
          it.remove();
          if (key.isValid()) {
            if (key.isAcceptable()) {
              accept(key);
            } else if (key.isReadable()) {
              read(key);
            }
          } else {
            System.out.println("exit");
            running.set(Boolean.FALSE);

          }
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  private void accept(SelectionKey key) {
    ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

    SocketChannel socketChannel = null;
    while (true) {
      try {
        if (!(null != (socketChannel = serverSocketChannel.accept()))) break;


        socketChannel.configureBlocking(false);

        socketChannel.register(selector, SelectionKey.OP_READ);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

    }
  }
}
