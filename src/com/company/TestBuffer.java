package com.company;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * 测试Buffer
 * 一、缓冲区（Buffer）：在 Java NIO 中负责数据的存取。缓冲区就是数组。用于存储不同数据类型的数据。
 * <p>
 * 根据数据类型不同（boolean 除外）,提供了相应类型的缓冲区：
 * ByteBuffer
 * CharBuffer
 * ShortBuffer
 * IntBuffer
 * LongBuffer
 * FloatBuffer
 * DoubleBuffer
 * <p>
 * 上述缓冲区的管理方式几乎一致，获取 allocate() 获取缓冲区。
 * <p>
 * 二、缓冲区存取数据的两个和兴方法：
 * put()：存入数据到缓冲区中
 * get()：获取缓冲区中的数据
 * <p>
 * 三、缓冲区中的四个核心属性：
 * capacity：容量，表示缓冲区中最大存储数据的容量，一旦声明不能改变。
 * limit：界限，表示缓冲区中可以操作数据的大小。（limit 后的数据是不能进行读写的）
 * position：位置，表示缓冲区中正在操作数据的位置。
 * <p>
 * mark：标记，表示记录当前 position 的位置。可以通过 reset() 恢复到 mark 的位置。
 * <p>
 * 0<= mark <= position <= limit <= capacity
 * <p>
 * 四、直接缓冲区与非直接缓冲区：
 * 非直接缓冲区：通过 allocate() 方法分配缓冲区，将缓冲区建立在 JVM 的内存中
 * 直接缓冲区：通过 allocateDirect() 方法分配直接缓冲区，将缓冲区建立在物理内存中。可以提高效率
 *
 * @author JOSWAY
 * @date 2021/2/27 10:47
 */
public class TestBuffer {

    public static void main(String[] args) {

//        test1();
//        test2();
        test3();

    }

    public static void test1() {
        String str = "abcde";

        //1.分配指定大小的缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        System.out.println("allocate()-----");
        System.out.println(byteBuffer.position());
        System.out.println(byteBuffer.limit());
        System.out.println(byteBuffer.capacity());

        //2.利用 put() ，存入数据到缓冲区
        byteBuffer.put(str.getBytes(StandardCharsets.UTF_8));
        System.out.println("put()-----");
        System.out.println(byteBuffer.position());
        System.out.println(byteBuffer.limit());
        System.out.println(byteBuffer.capacity());

        //3. flip() ，切换到读取数据模式
        byteBuffer.flip();
        System.out.println("flip()-----");
        System.out.println(byteBuffer.position());
        System.out.println(byteBuffer.limit());
        System.out.println(byteBuffer.capacity());

        //4.利用 get() ，读取缓冲区中的数据
        byte[] bytes = new byte[byteBuffer.limit()];
        byteBuffer.get(bytes);
        System.out.println(new String(bytes, 0, bytes.length));
        System.out.println("get()-----");
        System.out.println(byteBuffer.position());
        System.out.println(byteBuffer.limit());
        System.out.println(byteBuffer.capacity());

        //5. rewind() , 可重复读数据
        byteBuffer.rewind();
        System.out.println("rewind()-----");
        System.out.println(byteBuffer.position());
        System.out.println(byteBuffer.limit());
        System.out.println(byteBuffer.capacity());

        //6. clear ，清空缓冲区，但是缓冲区中的数据依然存在，但是处于“被遗忘”状态
        byteBuffer.clear();
        System.out.println("clear()-----");
        System.out.println(byteBuffer.position());
        System.out.println(byteBuffer.limit());
        System.out.println(byteBuffer.capacity());
        System.out.println();
        System.out.println((char) byteBuffer.get());
        System.out.println("-------------------------");
    }

    public static void test2() {

        String str = "abcde";

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        byteBuffer.put(str.getBytes(StandardCharsets.UTF_8));

        byteBuffer.flip();

        byte[] bytes = new byte[byteBuffer.limit()];
        byteBuffer.get(bytes, 0, 2);
        System.out.println(new String(bytes, 0, 2));
        System.out.println(byteBuffer.position());

        //mark()：标记
        byteBuffer.mark();

        byteBuffer.get(bytes, 2, 2);
        System.out.println(new String(bytes, 2, 2));
        System.out.println(byteBuffer.position());

        //reset()：恢复到 mark 的位置
        byteBuffer.reset();
        System.out.println(byteBuffer.position());
        System.out.println();

        //判断缓冲区是否还有剩余数据
        if (byteBuffer.hasRemaining()) {
            //获取缓冲区中可以操作的数量
            System.out.println(byteBuffer.remaining());
        }
        System.out.println("-------------------------");

    }

    public static void test3() {
        //分配直接缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);

        System.out.println(byteBuffer.isDirect());

    }

}
