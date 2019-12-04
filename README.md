# redis-share

## 开篇介绍 
```
Redis 是互联网技术领域使用最为广泛的存储中间件，它是「Remote Dictionary Service」的首字母缩写，
也就是「远程字典服务」。Redis 以其超高的性能、完美的文档、 简洁易懂的源码和丰富的客户端库支持在开源中间件领域广受好评。
国内外很多大型互联网 公司都在使用 Redis。
Redis 由意大利人 Salvatore Sanfilippo 开发.
默认端口是6379是由手机键盘字母「MERZ」 的位置决定的。「MERZ」在 Antirez 的朋友 圈语言中是「愚蠢」的代名词
```

## NOSQL

### 1.什么是nosql
NoSQL（not only sql，不仅仅是SQL），泛指非关系型数据库
常见的nosql 有
 
	1.键值(Key-Value)存储数据库  如redis memchche
	2.列存储数据库 如 hbase
	3.文档型数据库 如 mongodb
	4.图式数据库 Neo4J
	
### 2.nosql 和 mysql 的区别

NoSQL数据库 具有 易扩展，大数据量，高性能，灵活的数据模型，高可用 等特点

### 3.redis和其他nosql的对比
| 数据库类型 | 数据模型 | 优点 |缺点 |
| ------ | ------ | ------ |------ | 
| redis | K-V键值对| 查找速度快,支持持久化,数据结构丰富，集成复制、lua脚本等多种功能，功能更全| 基于内存，存储量跟机器内存大小息息相关，单线程 无法充分利用多核cpu的性能|
| memchche | K-V键值对 | 多线程操作 |不支持持久化，v最大值只有1M|
| mongodb |K-V键值对 | 数据结构要求不严格 |查询性能不高，缺少统一查询的语法 |
| hbase | 以列簇式存储，将同一列数据存在一起 | 快速查询，扩展性强 |基于hadop的hdfs，功能相对局限 |



## Redis基础知识

### 1.redis是什么
```
Redis是⼀一个开源的使⽤用ANSI C语⾔言编写、⽀支持⽹网络、可基于内存亦可持久化的日志型、Key-Value数据库
```

TIPS:redis有0-15共16号db，默认使⽤0号数据库
但是在集群模式下，仅开启0号数据库。 客户端通过select n 命令切换，因此推荐关闭其他数据库，仅使用0号库

### 2.redis的特点
```
1.高性能 每秒可以处理超过 10万次读写操作，是已知性能最快的Key-Value型DB
2.支持数据类型丰富 5种基础数据类型加上多种高级数据
3.支持事务，操作都原子性，因此分布式场景下应⽤广泛 
4.支持持久化
5.拥有丰富的特性 可用于缓存，消息，按key设置过期时间，过期后将会自动删除
```

### 3.redis为什什么速度快(后续补充介绍)
```
1.纯内存操作 
2.单线程操作，避免频繁的切换上下文 
3.采⽤了非阻塞I/O多路复用机制
```


## Redis基础数据结构

Redis 有 5 种基础数据结构，分别为:string (字符串)、list (列表)、set (集合)、hash (哈 希) 和 zset (有序集合)
以及多种高级数据结构(后续讲解)

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
	> setex name 5 codehole # 5s 后过期，等价于 	set+expire 
	> get name
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
如果 value 值是一个整数，还可以对它进行自增操作。自增是有范围的，它的范围是 signed long 的最大最小值，超过了这个值，Redis 会报错。redis会将字符串串类型转换成数值。 由于INCR等指令本身就具有原⼦子操作的特性，所以 我们完全可以利利⽤用redis的INCR、INCRBY、DECR、DECRBY等指令来实现原⼦子计数的效果。

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
然后对其同时进⾏了加1的操作，那么，最后num的值 ⼀一定是5(原⼦子性)
```

若配置了key的序列化方案为string，则 redis中存储为二进制byte字节， byte字节 对⽐比hash和json格式，存储效率更⾼高，占⽤用内存更更小，但不便于在 可视化工具(rdm)中直观查看数据

### 2.列表 list

Redis 的列表相当于 Java 语言里面的 LinkedList，注意它是链表而不是数组。这意味着 list 的插入和删除操作非常快，时间复杂度为 O(1)，但是索引定位很慢，时间复杂度为 O(n)，这点让人非常意外。
当列表弹出了最后一个元素之后，该数据结构自动被删除，内存被回收。
Redis 的列表结构常用来做异步队列使用。将需要延后处理的任务结构体序列化成字符 串塞进 Redis 的列表，另一个线程从这个列表中轮询数据进行处理。

#### 1)右边进左边出:队列

	> rpush books python java golang 
	(integer) 3
	> llen books
	(integer) 3
	> lpop books 
	"python"
	> lpop books 
	"java"
	> lpop books 
	"golang"
	> lpop books 
	(nil)
	
#### 2)右边进右边出:栈

	> rpush books python java golang 
	(integer) 3
	> rpop books
	"golang"
	> rpop books 
	"java"
	> rpop books 
	"python"
	> rpop books
	(nil)
	
#### 3)慢操作
lindex 相当于 Java 链表的 get(int index)方法，它需要对链表进行遍历，性能随着参数index 增大而变差。 
ltrim 和字面上的含义不太一样，个人觉得它叫 lretain(保留) 更合适一些，因为 ltrim 跟的两个参数 start_index 和end_index 定义了一个区间，在这个区间内的值，ltrim 要保留，区间之外统统砍掉。我们可以通过 ltrim 来实现一个定长的链表，这一点非常有用。index 可以为负数，index=-1 表示倒数第一个元素，同样 index=-2 表示倒数第二个元素。

 
	> rpush books python java golang 
	(integer) 3
	> lindex books 1 # O(n) 慎用 
	"java"
	> lrange books 0 -1 # 获取所有元素，O(n) 慎用 
	1) 	"python"
	2) "java"
	3) "golang"
	> ltrim books 1 -1 # O(n) 慎用 
	OK
	> lrange books 0 -1
	1) "java"
	2) "golang"
	> ltrim books 1 0 # 这其实是清空了整个列表，因为区间范围长度为负 
	OK
	> llen books
	(integer) 0

#### 4)应⽤用场景:

	1.我们可以利用list来实现⼀一个消息队列，而且可以确保先后顺序，
	不必像MySQL那样还需要通过ORDER BY 来进⾏排序。
	2.利利⽤用LRANGE还可以很⽅方便便的实现分⻚页的功能。 
	3.在博客系统中，每⽚片博⽂文的评论也可以存⼊入⼀一个单独的list中。
	4.记录前N个最新登陆的⽤用户Id列列表
	
### 3.hash (字典)
Redis 的字典相当于 Java 语言里面的 HashMap，它是无序字典。内部实现结构上同 Java 的 HashMap 也是一致的，同样的数组 + 链表二维结构。第一维 hash 的数组位置碰撞 时，就会将碰撞的元素使用链表串接起来。
![3](https://img.asman.com.cn/hashmap.jpg)

不同的是，Redis 的字典的值只能是字符串，另外它们 rehash 的方式不一样，因为 Java 的 HashMap 在字典很大时，rehash 是个耗时的操作，需要一次性全部 rehash。Redis 为了高性能，不能堵塞服务，所以采用了渐进式 rehash 策略。

渐进式 rehash 会在 rehash 的同时，保留新旧两个 hash 结构，查询时会同时查询两个 hash 结构，然后在后续的定时任务中以及 hash 的子指令中，循序渐进地将旧 hash 的内容 一点点迁移到新的 hash 结构中。
![4](https://img.asman.com.cn/rehash.jpg)

当 hash 移除了最后一个元素之后，该数据结构自动被删除，内存被回收。

hash 结构也可以用来存储用户信息，不同于字符串一次性需要全部序列化整个对象， hash 可以对用户结构中的每个字段单独存储。这样当我们需要获取用户信息时可以进行部分 获取。而以整个字符串的形式去保存用户信息的话就只能一次性全部读取，这样就会比较浪 费网络流量。

hash 也有缺点，hash 结构的存储消耗要高于单个字符串，到底该使用 hash 还是字符串，需要根据实际情况再三权衡。


   	> hset books java "think in java" # 命令行的字符串如果包含空格，要用引号括起来 
   	(integer) 1
	> hset books golang "concurrency in go"
	(integer) 1
	> hset books python "python cookbook"
	(integer) 1
	> hgetall books # entries()，key 和 value 间隔出现
	1) "java"
	2) "think in java"
	3) "golang"
	4) "concurrency in go"
	5) "python"
	6) "python cookbook"
	> hlen books
	(integer) 3
	> hget books java
	"think in java"
	> hset books golang "learning go programming" # 因为是更新操作，所以返回 0 
	(integer) 0
	> hget books golang 
	"learning go programming"
	> hmset books java "effective java" python "learning 	python" golang "modern golang
	programming" # 批量 set OK
	
同字符串一样，hash 结构中的单个子 key 也可以进行计数，它对应的指令是 hincrby， 和 incr 使用基本一样。

	# 又老了一岁
	> hincrby user-laoqian age 1 (integer) 30

 
### 4.set (集合)
Redis 的集合相当于 Java 语言里面的 HashSet，它内部的键值对是无序的唯一的。它的 内部实现相当于一个特殊的字典，字典中所有的 value 都是一个值 NULL。

当集合中最后一个元素移除之后，数据结构自动删除，内存被回收。 set 结构可以用来 存储活动中奖的用户 ID，因为有去重功能，可以保证同一个用户不会中奖两次。

	> sadd books python
	(integer) 1
	> sadd bookspython # 重复
	(integer) 0
	> sadd books java golang
	(integer) 2
	> smembers books # 注意顺序，和插入的并不一致，因为 set 是无序的
	1) "java"
	2) "python"
	3) "golang"
	> sismember books java # 查询某个 value 是否存在，相当于 contains(o) (integer) 1
	> sismember books rust
	(integer) 0
	> scard books # 获取长度相当于 count()
	(integer) 3
	> spop books # 弹出一个
	"java"
	
### 4.zset (有序列表)
zset 可能是 Redis 提供的最为特色的数据结构它类似于 Java 的 SortedSet 和 HashMap 的结合体，一方面它是一个 set，保证了内部 value 的唯一性，另一方面它可以给每个 value 赋予一个 score，代表这个 value 的排序权 重。它的内部实现用的是一种叫着「跳跃列表」的数据结构。

zset 中最后一个 value 被移除后，数据结构自动删除，内存被回收。

zset 可以用来存 粉丝列表，value 值是粉丝的用户 ID，score 是关注时间。我们可以对粉丝列表按关注时间 进行排序。
 
zset 还可以用来存储学生的成绩，value 值是学生的 ID，score 是他的考试成绩。我们 可以对成绩按分数进行排序就可以得到他的名次。
 
	> zadd books 9.0 "think in java"
	(integer) 1
	> zadd books 8.9 "java concurrency"
	(integer) 1
	> zadd books 8.6 "java cookbook"
	(integer) 1
	> zrange books 0 -1 # 按 score 排序列出，参数区间为排名范围
	1) "java cookbook"
	2) "java concurrency"
	3) "think in java"
	> zrevrange books 0 -1 # 按 score 逆序列出，参数区间为排名范围
	1) "think in java"
	2) "java concurrency"
	3) "java cookbook"
	> zcard books # 相当于 count()
	(integer) 3
	> zscore books "java concurrency" # 获取指定 value 的 score
	"8.9000000000000004" # 内部 score 使用 double 类型进行存储，所以存在小数点精度问题
	> zrank books "java concurrency" # 排名
	(integer) 1
	> zrangebyscore books 0 8.91 # 根据分值区间遍历 zset
	1) "java cookbook"
	2) "java concurrency"
	> zrangebyscore books -inf 8.91 withscores # 根据分值区间 (-∞, 8.91] 遍历 zset，同时返回分值。inf 代表 infinite，无穷大的意思。
	1) "java cookbook"
	2) "8.5999999999999996"
	3) "java concurrency"
	4) "8.9000000000000004"
	> zrem books "java concurrency" # 删除 value (integer) 1
	> zrange books 0 -1
	1) "java cookbook"
	2) "think in java"
	
#### 1)跳跃列表
zset 内部的排序功能是通过「跳跃列表」数据结构来实现的，它的结构非常特殊，也比 较复杂。
因为 zset 要支持随机的插入和删除，所以它不好使用数组来表示。我们先看一个普通的 链表结构。
![5](https://img.asman.com.cn/zset.jpg)

我们需要这个链表按照 score 值进行排序。这意味着当有新元素需要插入时，要定位到 特定位置的插入点，这样才可以继续保证链表是有序的。通常我们会通过二分查找来找到插 入点，但是二分查找的对象必须是数组，只有数组才可以支持快速位置定位，链表做不到， 那该怎么办?

想想一个创业公司，刚开始只有几个人，团队成员之间人人平等，都是联合创始人。随 着公司的成长，人数渐渐变多，团队沟通成本随之增加。这时候就会引入组长制，对团队进 行划分。每个团队会有一个组长。开会的时候分团队进行，多个组长之间还会有自己的会议 安排。公司规模进一步扩展，需要再增加一个层级 —— 部门，每个部门会从组长列表中推 选出一个代表来作为部长。部长们之间还会有自己的高层会议安排。

跳跃列表就是类似于这种层级制，最下面一层所有的元素都会串起来。然后每隔几个元 素挑选出一个代表来，再将这几个代表使用另外一级指针串起来。然后在这些代表里再挑出 二级代表，再串起来。最终就形成了金字塔结构。 想想你老家在世界地图中的位置:亚洲- ->中国->安徽省->宣城市->泾县->茂林镇->XXX村->XXX号，也是这样一个类似的结构。
![6](https://img.asman.com.cn/skiplist.jpg)

「跳跃列表」之所以「跳跃」，是因为内部的元素可能「身兼数职」，比如上图中间的 这个元素，同时处于 L0、L1 和 L2 层，可以快速在不同层次之间进行「跳跃」。

定位插入点时，先在顶层进行定位，然后下潜到下一级定位，一直下潜到最底层找到合 适的位置，将新元素插进去。你也许会问，那新插入的元素如何才有机会「身兼数职」呢?

跳跃列表采取一个随机策略来决定新元素可以兼职到第几层。

首先 L0 层肯定是 100% 了，L1 层只有 50% 的概率，L2 层只有 25% 的概率，L3 层只有 12.5% 的概率，一直随机到最顶层 L31 层。绝大多数元素都过不了几层，只有极少数元素可以深入到顶层。列表中的元素越多，能够深入的层次就越深，能进入到顶层的概率 就会越大。这还挺公平的，能不能进入中央不是靠拼爹，而是看运气。

#### 2)应用场景
```
1.积分排行
2.延迟队列
```

#### 3)容器型数据结构的通用规则
list/set/hash/zset 这四种数据结构是容器型数据结构，它们共享下面两条通用规则:

1、create if not exists

	如果容器不存在，那就创建一个，再进行操作。
	比如 rpush 操作刚开始是没有列表的，
	Redis 就会自动创建一个，然后再 rpush 进去新元素。
	
2、drop if no elements 

	如果容器里元素没有了，那么立即删除元素，释放内存。
	这意味着 lpop 操作到最后一个元素，列表就消失了。
	
	
3、过期时间

	Redis 所有的数据结构都可以设置过期时间，时间到了，Redis 会自动删除相应的对象。 
	需要注意的是过期是以对象为单位，比如一个 hash 结构的过期是整个 hash 对象的过期， 而不是其中的某个子 key。

还有一个需要特别注意的地方是如果一个字符串已经设置了过期时间，然后你调用了set 方法修改了它，它的过期时间会消失。

	> set codehole yoyo OK
	> expire codehole 600 (integer) 1
	> ttl codehole (integer) 597
	> set codehole yoyo OK
	> ttl codehole (integer) -1
	
## Redis的应用

### 1.分布式锁


分布式锁本质上要实现的目标就是在 Redis 里面占一个“茅坑”，当别的进程也要来占 时，发现已经有人蹲在那里了，就只好放弃或者稍后再试。
占坑一般是使用 setnx(set if not exists) 指令，只允许被一个客户端占坑。先来先占， 用 完了，再调用 del 指令释放茅坑。


	> setnx lock-codehole true OK
	... do something critical ... 
	> del lock-codehole (integer) 1
	
#### 死锁问题
但是有个问题，如果逻辑执行到中间出现异常了，可能会导致 del 指令没有被调用，这样 就会陷入死锁，锁永远得不到释放。
于是我们在拿到锁之后，再给锁加上一个过期时间，比如 5s，这样即使中间出现异常也 可以保证 5 秒之后锁会自动释放。

	> setnx lock-codehole true OK
	> expire lock:codehole 5
	... do something critical ... 
	> del lock-codehole (integer) 1
	
但是以上逻辑还有问题。如果在 setnx 和 expire 之间服务器进程突然挂掉了，可能是因 为机器掉电或者是被人为杀掉的，就会导致 expire 得不到执行，也会造成死锁。

这种问题的根源就在于 setnx 和 expire 是两条指令而不是原子指令。如果这两条指令可 以一起执行就不会出现问题。也许你会想到用 Redis 事务来解决。但是这里不行，因为 expire 是依赖于 setnx 的执行结果的，如果 setnx 没抢到锁，expire 是不应该执行的。事务里没有 if- else 分支逻辑，事务的特点是一口气执行，要么全部执行要么一个都不执行。

![6](https://img.asman.com.cn/lock.jpg)

#### 超时问题

Redis 的分布式锁不能解决超时问题，如果在加锁和释放锁之间的逻辑执行的太长，以至 于超出了锁的超时限制，就会出现问题。因为这时候锁过期了，第二个线程重新持有了这把锁， 但是紧接着第一个线程执行完了业务逻辑，就把锁给释放了，第三个线程就会在第二个线程逻 辑执行完之间拿到了锁。

有一个安全的方案是为 set 指令的 value 参数设置为一个随机数，释放锁时先匹配 随机数是否一致，然后再删除 key。但是匹配 value 和删除 key 不是一个原子操作，Redis 也 没有提供类似于 delifequals 这样的指令，这就需要使用 Lua 脚本来处理了，因为 Lua 脚本可 以保证连续多个指令的原子性执行。

	#lua 脚本 原子删除key
	if redis.call("get",KEYS[1]) == ARGV[1] then
	return redis.call("del",KEYS[1])
	else
	return 0
	end
	
但是在redis集群环境下，从库的key过期完全依赖主库的同步,主从数据库存在数据同步的延迟问题,因此在集群环境下的redis分布式锁用此种方式依然存在问题。我们可以借鉴其他的优秀开源框架比如redisson

### 2.延迟队列

延时队列可以通过 Redis 的 zset(有序列表) 来实现。我们将消息序列化成一个字符串作 为 zset 的 value，这个消息的到期处理时间作为 score，然后用多个线程轮询 zset 获取到期 的任务进行处理，多个线程是为了保障可用性，万一挂了一个线程还有其它线程可以继续处 理。因为有多个线程，所以需要考虑并发争抢任务，确保任务不能被多次执行。




