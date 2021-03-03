package com.company;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

/**
 * 测试通道
 * 一、通道（Channel）：用于源节点和目标节点的连接。在 Java NIO 中负责缓冲区中数据的传输。Channel 本身不存储数据，因此需要配合缓冲区进行传输。
 * <p>
 * 二、通道的主要实现类：
 * java.nio.channels.Channel 接口：
 * |--FileChannel
 * |--SocketChannel
 * |--ServerSocketChannel
 * |--DatagramChannel
 * <p>
 * 三、获取通道：
 * 1. Java 针对支持通道的类提供了 getChannel() 方法
 * 本地IO：
 * |--FileInputStream/FileOutputStream
 * |--RandomAccessFile
 * 网络IO：
 * |--Socket
 * |--ServerSocket
 * |--DatagramSocket
 * <p>
 * 2.在 JDK 1.7 中的 NIO.2 针对各个通道提供了静态方法 open()
 * 3.在 JDK 1.7 中的 NIO.2 的 Files 工具类的 newByteChannel()
 * <p>
 * 四、通道之间的数据传输
 * transferFrom()
 * transferTo()
 * <p>
 * 五、分散(Scatter)与聚集(Gather)
 * 分散读取（Scattering Reads）：将通道中的数据分散到多个缓冲区中
 * 聚集写入（Gathering Writes）：将多个缓冲区中的数据聚集到通道中
 * <p>
 * 六、字符集：Charset
 * 编码：字符串 -> 字节数组
 * 解码：字节数组  -> 字符串
 *
 * @author JOSWAY
 * @date 2021/2/27 16:11
 */
public class TestChannel {

    public static void main(String[] args) {

//        test1();
//        test2();
//        test3();
//        test4();
//        test5();
        test6();

    }

    //一、利用通道完成文件的复制 （非直接缓冲区）
    public static void test1() {//10874-10953

        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        FileChannel fileInputStreamChannel = null;
        FileChannel fileOutputStreamChannel = null;

        try {

            fileInputStream = new FileInputStream("1.png");
            fileOutputStream = new FileOutputStream("2.png");

            //1.获取通道
            fileInputStreamChannel = fileInputStream.getChannel();
            fileOutputStreamChannel = fileOutputStream.getChannel();

            //2.分配指定大小的缓冲区
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

            //3.将通道中的数据存入缓冲区中
            while (fileInputStreamChannel.read(byteBuffer) != -1) {
                //切换成读取数据的模式
                byteBuffer.flip();

                //4.将缓冲区中的数据写入通道
                fileOutputStreamChannel.write(byteBuffer);

                //清空缓冲区
                byteBuffer.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                //关闭
                fileOutputStreamChannel.close();
                fileInputStreamChannel.close();
                fileOutputStream.close();
                fileInputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    //二、使用直接缓冲区完成文件的复制 （内存映射文件）
    public static void test2() {//2127-1902-1777

        try {
            FileChannel fileChannelIn = FileChannel.open(Paths.get("avatar.jpg"), StandardOpenOption.READ);
            FileChannel fileChannelOut = FileChannel.open(Paths.get("avatar-copy.jpg"), StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE);

            //内存映射文件
            MappedByteBuffer mappedByteBufferIn = fileChannelIn.map(FileChannel.MapMode.READ_ONLY, 0, fileChannelIn.size());
            MappedByteBuffer mappedByteBufferOut = fileChannelOut.map(FileChannel.MapMode.READ_WRITE, 0, fileChannelIn.size());

            //直接对缓冲区进行数据的读写操作
            byte[] bytes = new byte[mappedByteBufferIn.limit()];
            mappedByteBufferIn.get(bytes);
            mappedByteBufferOut.put(bytes);

            mappedByteBufferIn.clear();
            mappedByteBufferOut.clear();
            fileChannelIn.close();
            fileChannelOut.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //四、通道之间的数据传输(直接缓冲区)
    public static void test3() {
        try {
            FileChannel fileChannelIn = FileChannel.open(Paths.get("avatar.jpg"), StandardOpenOption.READ);
            FileChannel fileChannelOut = FileChannel.open(Paths.get("avatar-copy.jpg"), StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE);

//            fileChannelIn.transferTo(0, fileChannelIn.size(), fileChannelOut);
            fileChannelOut.transferFrom(fileChannelIn, 0, fileChannelIn.size());

            fileChannelIn.close();
            fileChannelOut.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //五、分散(Scatter)与聚集(Gather)
    public static void test4() {
        try {
            RandomAccessFile randomAccessFileIn = new RandomAccessFile("1.txt", "rw");

            //1.获取通道
            FileChannel randomAccessFileInChannel = randomAccessFileIn.getChannel();

            //2.分配指定大小的缓冲区
            ByteBuffer byteBuffer1 = ByteBuffer.allocate(101);
            ByteBuffer byteBuffer2 = ByteBuffer.allocate(101);

            //3.分散读取
            ByteBuffer[] byteBuffers = {byteBuffer1, byteBuffer2};
            randomAccessFileInChannel.read(byteBuffers);

            for (ByteBuffer byteBuffer : byteBuffers) {
                byteBuffer.flip();
                System.out.println(new String(byteBuffer.array(), 0, byteBuffer.limit()));
                System.out.println("-----------");
            }

            //4.聚集写入
            RandomAccessFile randomAccessFileOut = new RandomAccessFile("2.txt", "rw");
            FileChannel randomAccessFileOutChannel = randomAccessFileOut.getChannel();

            randomAccessFileOutChannel.write(byteBuffers);


            for (ByteBuffer byteBuffer : byteBuffers) {
                byteBuffer.clear();
            }
            randomAccessFileInChannel.close();
            randomAccessFileOutChannel.close();
            randomAccessFileIn.close();
            randomAccessFileOut.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //六、字符集：Charset
    public static void test5() {
        SortedMap<String, Charset> stringCharsetSortedMap = Charset.availableCharsets();
        Set<Map.Entry<String, Charset>> entries = stringCharsetSortedMap.entrySet();
        for (Map.Entry<String, Charset> stringCharsetEntry : entries) {
            System.out.println(stringCharsetEntry.getKey() + " : " + stringCharsetEntry.getValue());
        }
    }

    //六、字符集：Charset
    public static void test6() {

        try {

            Charset cs1 = Charset.forName("GBK");

            //获取编码器
            CharsetEncoder charsetEncoder = cs1.newEncoder();

            //获取解码器
            CharsetDecoder charsetDecoder = cs1.newDecoder();

            CharBuffer charBuffer = CharBuffer.allocate(1024);
            charBuffer.put("测试字符集");
            charBuffer.flip();

            //编码
            ByteBuffer byteBuffer = charsetEncoder.encode(charBuffer);

            for (int i = 0; i < 10; i++) {
                System.out.println(byteBuffer.get());
            }

            //解码
            byteBuffer.flip();
            CharBuffer charBuffer1 = charsetDecoder.decode(byteBuffer);
            System.out.println(charBuffer1.toString());

            System.out.println("-----------------------------");

            Charset cs2 = Charset.forName("GBK");
            byteBuffer.flip();
            CharBuffer charBuffer2 = cs2.decode(byteBuffer);
            System.out.println(charBuffer2.toString());

        } catch (CharacterCodingException e) {
            e.printStackTrace();
        }

    }
}
