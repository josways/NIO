package com.company;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * 测试阻塞NIO
 * 一、使用 NIO 完成网络通信的三个核心:
 * 1.通道（Channel）：负责连接
 * java.nio.channels.Channel 接口:
 * |-SelectableChannel
 * |--SocketChannel
 * |--ServerSocketChannel
 * |--DatagramChannel
 * <p>
 * |--Pipe.SinkChannel
 * |--Pipe.SourceChannel
 * <p>
 * 2.缓冲区（Buffer）：负责数据的存取
 * 3.选择器（Selector）：是 SelectableChannel 的多路复用器。用于监控 SelectableChannel 的 IO 状况。
 *
 * @author JOSWAY
 * @date 2021/3/3 9:37
 */
public class TestBlockingNIO {
    public static void main(String[] args) {

        TestBlockingNIO testBlockingNIO = new TestBlockingNIO();

        new Thread(new Runnable() {
            @Override
            public void run() {
                testBlockingNIO.server();
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                testBlockingNIO.client();
            }
        }).start();

    }

    //客户端
    public void client() {
        try {
            //1.获取通道
            SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9898));

            FileChannel fileChannel = FileChannel.open(Paths.get("1.png"), StandardOpenOption.READ);

            //2.分配指定大小的缓冲区
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

            //3.读取本地文件，并发送到服务端
            while (fileChannel.read(byteBuffer) != -1) {
                byteBuffer.flip();
                socketChannel.write(byteBuffer);
                byteBuffer.clear();
            }

            //4.关闭通道
            fileChannel.close();
            socketChannel.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //服务端
    public void server() {
        try {
            //1.获取通道
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

            FileChannel fileChannel = FileChannel.open(Paths.get("2.png"), StandardOpenOption.CREATE, StandardOpenOption.WRITE);

            //2.绑定连接
            serverSocketChannel.bind(new InetSocketAddress(9898));

            //3.获取客户端连接的通道
            SocketChannel socketChannel = serverSocketChannel.accept();

            //4.分配指定大小的缓冲区
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

            //5.接受客户端的数据，并保存到本地
            while (socketChannel.read(byteBuffer) != -1) {
                byteBuffer.flip();
                fileChannel.write(byteBuffer);
                byteBuffer.clear();
            }

            //6.关闭通道
            socketChannel.close();
            fileChannel.close();
            serverSocketChannel.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

