# NIO

java.nio


## 1、Java NIO 简介

Java NIO（New IO）是从Java 1.4版本开始引入的
一个新的IO API，可以替代标准的Java IO API。
NIO与原来的IO有同样的作用和目的，但是使用
的方式完全不同，NIO支持面向缓冲区的、基于
通道的IO操作。NIO将以更加高效的方式进行文
件的读写操作。


## 2、Java NIO 与 IO 的主要区别

IO | NIO
面向流(Stream Oriented) | 面向缓冲区(Buffer Oriented)
阻塞IO(Blocking IO) | 非阻塞IO(Non Blocking IO)
(无) | 选择器(Selectors)


## 3、缓冲区(Buffer)和通道(Channel)

Java NIO系统的核心在于：通道(Channel)和缓冲区 (Buffer)。通道表示打开到 IO 设备(例如：文件、 套接字)的连接。若需要使用 NIO 系统，需要获取 用于连接 IO 设备的通道以及用于容纳数据的缓冲 区。然后操作缓冲区，对数据进行处理。


### 简而言之，Channel 负责传输， Buffer 负责存储

### 缓冲区

缓冲区（Buffer）：一个用于特定基本数据类 型的容器。由 java.nio 包定义的，所有缓冲区 都是 Buffer 抽象类的子类。
Java NIO 中的 Buffer 主要用于与 NIO 通道进行 交互，数据是从通道读入缓冲区，从缓冲区写 入通道中的 。
Buffer 就像一个数组，可以保存多个相同类型的数据。根 据数据类型不同(boolean 除外) ，有以下 Buffer 常用子类：
ByteBuffer
CharBuffer
ShortBuffer
IntBuffer
LongBuffer
FloatBuffer
DoubleBuffer
上述 Buffer 类 他们都采用相似的方法进行管理数据，只是各自 管理的数据类型不同而已。都是通过如下方法获取一个 Buffer 对象：
static XxxBuffer allocate(int capacity) : 创建一个容量为 capacity 的 XxxBuffer 对象



- 缓冲区的基本属性

  Buffer 中的重要概念：
  容量 (capacity) ：表示 Buffer 最大数据容量，缓冲区容量不能为负，并且创 建后不能更改。
  限制 (limit)：第一个不应该读取或写入的数据的索引，即位于 limit 后的数据 不可读写。缓冲区的限制不能为负，并且不能大于其容量。
  位置 (position)：下一个要读取或写入的数据的索引。缓冲区的位置不能为 负，并且不能大于其限制
  标记 (mark)与重置 (reset)：标记是一个索引，通过 Buffer 中的 mark() 方法 指定 Buffer 中一个特定的 position，之后可以通过调用 reset() 方法恢复到这 个 position.
  标记、位置、限制、容量遵守以下不变式： 0 <= mark <= position <= limit <= capacity

- Buffer 的常用方法

- 缓冲区的数据操作

  Buffer 所有子类提供了两个用于数据操作的方法：get() 与 put() 方法
  获取 Buffer 中的数据
  get() ：读取单个字节
  get(byte[] dst)：批量读取多个字节到 dst 中
  get(int index)：读取指定索引位置的字节(不会移动 position)
  放入数据到 Buffer 中
  put(byte b)：将给定单个字节写入缓冲区的当前位置
  put(byte[] src)：将 src 中的字节写入缓冲区的当前位置
  put(int index, byte b)：将指定字节写入缓冲区的索引位置(不会移动 position)

- 直接与非直接缓冲区

  字节缓冲区要么是直接的，要么是非直接的。如果为直接字节缓冲区，则 Java 虚拟机会尽最大努力直接在 此缓冲区上执行本机 I/O 操作。也就是说，在每次调用基础操作系统的一个本机 I/O 操作之前（或之后）， 虚拟机都会尽量避免将缓冲区的内容复制到中间缓冲区中（或从中间缓冲区中复制内容）。
  直接字节缓冲区可以通过调用此类的 allocateDirect() 工厂方法来创建。此方法返回的缓冲区进行分配和取消 分配所需成本通常高于非直接缓冲区。直接缓冲区的内容可以驻留在常规的垃圾回收堆之外，因此，它们对 应用程序的内存需求量造成的影响可能并不明显。所以，建议将直接缓冲区主要分配给那些易受基础系统的 本机 I/O 操作影响的大型、持久的缓冲区。一般情况下，最好仅在直接缓冲区能在程序性能方面带来明显好 处时分配它们。
  直接字节缓冲区还可以通过 FileChannel 的 map() 方法 将文件区域直接映射到内存中来创建。该方法返回 MappedByteBuffer 。Java 平台的实现有助于通过 JNI 从本机代码创建直接字节缓冲区。如果以上这些缓冲区 中的某个缓冲区实例指的是不可访问的内存区域，则试图访问该区域不会更改该缓冲区的内容，并且将会在 访问期间或稍后的某个时间导致抛出不确定的异常。
  字节缓冲区是直接缓冲区还是非直接缓冲区可通过调用其 isDirect() 方法来确定。提供此方法是为了能够在 性能关键型代码中执行显式缓冲区管理。


	- 非直接缓冲区
	- 直接缓冲区

### 通道

通道（Channel）：由 java.nio.channels 包定义 的。Channel 表示 IO 源与目标打开的连接。 Channel 类似于传统的“流”。只不过 Channel 本身不能直接访问数据，Channel 只能与 Buffer 进行交互。
Java 为 Channel 接口提供的最主要实现类如下：
FileChannel：用于读取、写入、映射和操作文件的通道。
DatagramChannel：通过 UDP 读写网络中的数据通道。
SocketChannel：通过 TCP 读写网络中的数据。
ServerSocketChannel：可以监听新进来的 TCP 连接，对每一个新进来 的连接都会创建一个 SocketChannel。


- 通道

- 通道

- 通道

- 获取通道

  获取通道的一种方式是对支持通道的对象调用 getChannel() 方法。支持通道的类如下：
  FileInputStream
  FileOutputStream
  RandomAccessFile
  DatagramSocket
  Socket
  ServerSocket
  获取通道的其他方式是使用 Files 类的静态方法 newByteChannel() 获 取字节通道。或者通过通道的静态方法 open() 打开并返回指定通道。

- 通道的数据传输

- 分散(Scatter)和聚集(Gather)




- 分散读取（Scattering Reads）

  是指从 Channel 中读取的数据“分 散”到多个 Buffer 中。
  注意：按照缓冲区的顺序，从 Channel 中读取的数据依次将 Buffer 填满。

- 聚集写入（Gathering Writes）

  是指将多个 Buffer 中的数据“聚集” 到 Channel。
  注意：按照缓冲区的顺序，写入 position 和 limit 之间的数据到 Channel 。

- transferFrom()

  将数据从源通道传输到其他 Channel 中：


- transferTo()

  将数据从源通道传输到其他 Channel 中：


## 4、文件通道(FileChannel)

### FileChannel 的常用方法

## 5、NIO 的非阻塞式网络通信

传统的 IO 流都是阻塞式的。也就是说，当一个线程调用 read() 或 write() 时，该线程被阻塞，直到有一些数据被读取或写入，该线程在此期间不 能执行其他任务。因此，在完成网络通信进行 IO 操作时，由于线程会 阻塞，所以服务器端必须为每个客户端都提供一个独立的线程进行处理， 当服务器端需要处理大量客户端时，性能急剧下降。
Java NIO 是非阻塞模式的。当线程从某通道进行读写数据时，若没有数 据可用时，该线程可以进行其他任务。线程通常将非阻塞 IO 的空闲时 间用于在其他通道上执行 IO 操作，所以单独的线程可以管理多个输入 和输出通道。因此，NIO 可以让服务器端使用一个或有限几个线程来同 时处理连接到服务器端的所有客户端。


### 1、选择器(Selector)

选择器（Selector） 是 SelectableChannle 对象的多路复用器，Selector 可 以同时监控多个 SelectableChannel 的 IO 状况，也就是说，利用 Selector 可使一个单独的线程管理多个 Channel。Selector 是非阻塞 IO 的核心。


- 选择器（Selector）的应用

    - 子主题 1
    - 子主题 2

- SelectionKey

    - 子主题 1
    - 子主题 2

- Selector 的常用方法

### 2、SocketChannel、ServerSocketChannel、DatagramChannel

- SocketChannel

  Java NIO中的SocketChannel是一个连接到TCP网 络套接字的通道。
  操作步骤：
  打开 SocketChannel
  读写数据  
  关闭 SocketChannel

  Java NIO中的 ServerSocketChannel 是一个可以 监听新进来的TCP连接的通道，就像标准IO中 的ServerSocket一样。

- DatagramChannel

  Java NIO中的DatagramChannel是一个能收发 UDP包的通道。
  操作步骤：
  打开 DatagramChannel
  接收/发送数据

## 6、管道(Pipe)

Java NIO 管道是2个线程之间的单向数据连接。 Pipe有一个source通道和一个sink通道。数据会 被写到sink通道，从source通道读取。


### 向管道写数据

### 从管道读取数据

## 7、Java NIO2 (Path、Paths 与 Files )

随着 JDK 7 的发布，Java对NIO进行了极大的扩 展，增强了对文件处理和文件系统特性的支持， 以至于我们称他们为 NIO.2。因为 NIO 提供的 一些功能，NIO已经成为文件处理中越来越重 要的部分。


### Path 与 Paths

java.nio.file.Path 接口代表一个平台无关的平台路径，描述了目 录结构中文件的位置。


### Files 类

java.nio.file.Files 用于操作文件或目录的工具类。


### 自动资源管理

Java 7 增加了一个新特性，该特性提供了另外 一种管理资源的方式，这种方式能自动关闭文 件。这个特性有时被称为自动资源管理 (Automatic Resource Management, ARM)， 该特 性以 try 语句的扩展版为基础。自动资源管理 主要用于，当不再需要文件（或其他资源）时， 可以防止无意中忘记释放它们。 

