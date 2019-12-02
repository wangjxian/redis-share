# redis-share

## 开篇介绍 
```
Redis 是互联网技术领域使用最为广泛的存储中间件，它是「Remote Dictionary Service」的首字母缩写，
也就是「远程字典服务」。Redis 以其超高的性能、完美的文档、 简洁易懂的源码和丰富的客户端库支持在开源中间件领域广受好评。
国内外很多大型互联网 公司都在使用 Redis。
Redis 由意大利人 Salvatore Sanfilippo 开发.
手机键盘字母「MERZ」 的位置决定的。「MERZ」在 Antirez 的朋友 圈语言中是「愚蠢」的代名词，默认端口是6379
```

## Redis基础知识

### 1.redis是什么
```
Redis是⼀一个开源的使⽤用ANSI C语⾔言编写、⽀支持⽹网络、可基于内存亦可持久化的⽇日志型、Key-Value数据库
```

redis有0-15共16号db，默认使⽤用0号数据库
但是在集群模式下，仅开启0号数据库。 客户端通过select n 命令切换，因此推荐关闭其他数据库，仅使用0号库

### 2.redis的特点
```
1.⾼高性能
2.⽀支持数据类型丰富 
3.原⼦子性操作，因此分布式场景下应⽤用⼴广泛 
4.⽀支持持久化
```

### 3.redis为什什么速度快
```
1.纯内存操作 
2.单线程操作，避免频繁的切换上下⽂文 
3.采⽤用了了⾮非阻塞I/O多路路复⽤用机制
```


## Redis基础数据结构

Redis 有 5 种基础数据结构，分别为:string (字符串)、list (列表)、set (集合)、hash (哈 希) 和 zset (有序集合)

### 1.字符串 string
字符串 string 是 Redis 最简单的数据结构。Redis 所有的数据结构都是以唯一的 key 字符串作为名称，然后通过这个唯一 key 值来获取相应的 value 数据。不同类型的数据结 构的差异就在于 value 的结构不一样。

![1](https://img.asman.com.cn/kv.jpg)

字符串结构使用非常广泛，一个常见的用途就是缓存用户信息。我们将用户信息结构体 使用 JSON 序列化成字符串，然后将序列化后的字符串塞进 Redis 来缓存。同样，取用户 信息会经过一次反序列化的过程。

![2](https://img.asman.com.cn/str.jpg)

Redis 的字符串是动态字符串，是可以修改的字符串，内部结构实现上类似于 Java 的 ArrayList，采用预分配冗余空间的方式来减少内存的频繁分配，如图中所示，内部为当前字 符串实际分配的空间 capacity 一般要高于实际字符串长度 len。当字符串长度小于 1M 时， 扩容都是加倍现有的空间，如果超过 1M，扩容时一次只会多扩 1M 的空间。需要注意的是 字符串最大长度为 512M。

#### 1)键值对操作

    > set name codehole OK
    > get name "codehole"
    > exists name
    (integer) 1
    > del name
    (integer) 1
    > get name
    (nil)
    
#### 2)批量键值对
可以批量对多个字符串进行读写，节省网络耗时开销。

    > set name1 codehole
    OK
    > set name2 holycoder
    OK
	> mget name1 name2 name3 # 返回一个列表 
	1) 	"codehole"
	2) "holycoder"
	3) (nil)
	> mset name1 boy name2 girl name3 unknown 
	> mget name1 name2 name3
	1) "boy"
	2) "girl"
	3) "unknown"
	
#### 3)过期和 set 命令扩展
可以对 key 设置过期时间，到点自动删除，这个功能常用来控制缓存的失效时间

	> set name codehole
	> get name "codehole"
	> expire name 5 # 5s 后过期
	... # wait for 5s
	> get name
	(nil)
	> setex name 5 codehole # 5s 后过期，等价于 	set+expire > get name
	"codehole"
	... # wait for 5s
	> get name
	(nil)
	> setnx name codehole # 如果 name 不存在就执行 set创建 (integer) 1
	> get name
	"codehole"
	> setnx name holycoder
	(integer) 0 # 因为 name 已经存在，所以 set 创建不成功
	> get name
	"codehole" # 没有改变
	
#### 4)计数
如果 value 值是一个整数，还可以对它进行自增操作。自增是有范围的，它的范围是 signed long 的最大最小值，超过了这个值，Redis 会报错。redis会将字符串串类型转换成数值。 由于INCR等指令本身就具有原⼦子操作的特性，所以 我们完全可以利利⽤用redis的INCR、INCRBY、DECR、DECRBY等指令来实现原⼦子计数的效果

	> set age 30
	OK
	> incr age (integer) 31
	> incrby age 5 (integer) 36
	> incrby age -5 (integer) 31
	> set codehole 9223372036854775807 # Long.Max
    OK
    > incr codehole
    (error) ERR increment or decrement would overflow
    
Tips:字符串是由多个字节组成，每个字节又是由 8 个 bit 组成，如此便可以将一个字符串看成很多 bit 的组合，这便是 bitmap「位图」数据结构

#### 5)应⽤用场景:
```	
1.常用的缓存
2.统计计数: 在某种场景下有3个客户端同时读取了了num的值(值为2)，
然后对其同时进⾏行行了了加1的操作，那么，最后num的值 ⼀一定是5(原⼦子性)
```

若配置了了key的序列列化⽅方案为string，则 redis中存储为⼆二进制byte字节， byte字节 对⽐比hash和json格式，存储效率更⾼高，占⽤用内存更更⼩小，但不不便便于在 可视化⼯工具(rdm)中直观查看数据