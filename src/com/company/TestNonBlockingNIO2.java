package com.company;

import com.sun.org.apache.bcel.internal.generic.Select;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;

/**
 * 测试非阻塞NIO2
 *
 * @author JOSWAY
 * @date 2021/3/3 16:16
 */
public class TestNonBlockingNIO2 {

    public static void main(String[] args) {

        TestNonBlockingNIO2 testNonBlockingNIO2 = new TestNonBlockingNIO2();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    testNonBlockingNIO2.receive();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    testNonBlockingNIO2.send();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void send() throws IOException {

        DatagramChannel datagramChannel = DatagramChannel.open();

        datagramChannel.configureBlocking(false);

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNext()) {
            String str = scanner.next();
            byteBuffer.put((new Date().toString() + ":\n" + str).getBytes(StandardCharsets.UTF_8));
            byteBuffer.flip();
            datagramChannel.send(byteBuffer, new InetSocketAddress("127.0.0.1", 9898));
            byteBuffer.clear();
        }

        datagramChannel.close();
    }

    public void receive() throws IOException {

        DatagramChannel datagramChannel = DatagramChannel.open();

        datagramChannel.configureBlocking(false);

        datagramChannel.bind(new InetSocketAddress(9898));

        Selector selector = Selector.open();

        datagramChannel.register(selector, SelectionKey.OP_READ);

        while (selector.select() > 0) {

            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

            while (iterator.hasNext()) {

                SelectionKey selectionKey = iterator.next();

                if (selectionKey.isReadable()) {

                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

                    datagramChannel.receive(byteBuffer);
                    byteBuffer.flip();
                    System.out.println(new String(byteBuffer.array(), 0, byteBuffer.limit()));
                    byteBuffer.clear();
                }
            }

            iterator.remove();
        }


    }

}
