# 改版的大体思路

1. 将之前的代码全部复制过来,修改了全部的包名
1. 添加usb通信机制
1. 将之前双串口通信机制改为单usb通信机制

# 遇到的问题
1. 解析接收机参数时,如何检测尾c0?
1. 每个 Activity 都要重写back键的事件,主要是关闭 usb 设备的连接,已经其开启的线程,防止内存泄漏
1. 当接受参数和数据两种类型的数据时,解析参数要放在另一个线程中,该线程中使用一个阻塞队列<String>;
1. 观察使用同步方法接受数据的行为,如果接收数据的中间存在 read == 0 的情况,可以在收数据为 0 时 解析数据;
    向LinkedBlockingQueue中添加数据的三个函数的区别:
    - add方法在添加元素的时候，若超出了度列的长度会直接抛出异常：
    - 对于put方法，若向队尾添加元素的时候发现队列已经满了会发生阻塞一直等待空间，以加入元素。
    - offer方法在添加元素时，如果发现队列已满无法添加的话，会直接返回false

# 修改坐标的两个位置
1. Tools.java 中的 `public static Locater transferLocate(Locater l)` 方法
2. Param.java 中的 `public static final Locater[] seaAreas` 数组;
3. 台风坐标的点:调用了 Tools.transferLocate()方法;



# 针对GPS
如果是显示实时位置,那么按照获取到的位置就是double;直接这样显示吧;

如果是显示海区区域按照整数来处理吧...




山东的坐标
```
2570,200
2718,362
2976,256
3002,565
3020,850
3380,1150
3020,1640
2365,2087
2910,2120
2710,2535
910,2570
1356,2500
1676,2455
2105,2444
1475,2970
2220,2950
1530,3535
2155,3535
```