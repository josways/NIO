package com.company;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * 测试阻塞NIO2
 *
 * @author JOSWAY
 * @date 2021/3/3 10:35
 */
public class TestBlockingNIO2 {

    public static void main(String[] args) {

        TestBlockingNIO2 testBlockingNIO2 = new TestBlockingNIO2();

        new Thread(new Runnable() {
            @Override
            public void run() {
                testBlockingNIO2.server();
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                testBlockingNIO2.client();
            }
        }).start();
    }


    //客户端
    public void client() {

        try {

            SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9898));

            FileChannel fileChannel = FileChannel.open(Paths.get("1.png"), StandardOpenOption.READ);

            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

            while (fileChannel.read(byteBuffer) != -1) {
                byteBuffer.flip();
                socketChannel.write(byteBuffer);
                byteBuffer.clear();
            }

            socketChannel.shutdownOutput();

            //接收服务器的反馈
            int len = 0;
            while ((len = socketChannel.read(byteBuffer)) != -1) {
                byteBuffer.flip();
                System.out.println(new String(byteBuffer.array(), 0, len));
                byteBuffer.clear();
            }

            fileChannel.close();
            socketChannel.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //服务端
    public void server() {

        try {

            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

            FileChannel fileChannel = FileChannel.open(Paths.get("2.png"), StandardOpenOption.CREATE, StandardOpenOption.WRITE);

            serverSocketChannel.bind(new InetSocketAddress(9898));

            SocketChannel socketChannel = serverSocketChannel.accept();

            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

            while (socketChannel.read(byteBuffer) != -1) {
                byteBuffer.flip();
                fileChannel.write(byteBuffer);
                byteBuffer.clear();
            }

            //发送反馈给客户端
            byteBuffer.put("服务端接收成功".getBytes(StandardCharsets.UTF_8));
            byteBuffer.flip();
            socketChannel.write(byteBuffer);

            socketChannel.close();
            fileChannel.close();
            serverSocketChannel.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
