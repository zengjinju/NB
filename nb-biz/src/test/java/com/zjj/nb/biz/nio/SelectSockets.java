package com.zjj.nb.biz.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * Created by jinju.zeng on 2017/4/8.
 * 简单的服务器代码
 */
public class SelectSockets {

    private static final int PORT = 8080;
    private static ByteBuffer buffer = ByteBuffer.allocateDirect(1024);

    public static void main(String[] args) {
        go();
    }

    public static void go() {
        try {
            ServerSocketChannel serverChannel = ServerSocketChannel.open();
            ServerSocket socket = serverChannel.socket();
            //创建一个选择器
            Selector selector = Selector.open();
            //监听8080端口
            socket.bind(new InetSocketAddress(PORT));
            serverChannel.configureBlocking(false);
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            while (true) {
                //阻塞到至少有一个通道在你注册的事件上就绪了
                int n = selector.select();
                if (n == 0) {
                    continue;
                }
                Iterator it = selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey key = (SelectionKey) it.next();
                    //通道是否已准备好接受新进来的连接
                    if (key.isAcceptable()) {
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        SocketChannel channel = server.accept();
                        //向选择器中注册新的通道
                        registerChannel(selector, channel, SelectionKey.OP_READ);
                        sayHello(channel);
                    }
                    //此密钥通道是否已准备好读取操作
                    if (key.isReadable()) {
                        readDataFromSocket(key);
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void registerChannel(Selector selector, SelectableChannel channel, int ops) throws IOException {
        if (channel == null) {
            return;
        }
        channel.configureBlocking(false);
        channel.register(selector, ops);
    }

    private static void sayHello(SocketChannel channel) throws IOException {
        buffer.clear();
        buffer.put("hello zjj".getBytes());
        buffer.flip();
        channel.write(buffer);
    }

    private static void readDataFromSocket(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        buffer.clear();
        WritableByteChannel out=Channels.newChannel(System.out);
        while(channel.read(buffer)!=-1){
            buffer.flip();
            while(buffer.hasRemaining()){
                out.write(buffer);
            }
            buffer.clear();
        }
    }
}
