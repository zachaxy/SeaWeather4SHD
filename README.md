# 改版的大体思路

1. 将之前的代码全部复制过来,修改了全部的包名
2. 添加usb通信机制
3. 将之前双串口通信机制改为单usb通信机制

# 遇到的问题
1. 解析接收机参数时,如何检测尾c0?
2. 每个 Activity 都要重写back键的事件,主要是关闭 usb 设备的连接,已经其开启的线程,防止内存泄漏
3. 当接受参数和数据两种类型的数据时,解析参数要放在另一个线程中,该线程中使用一个阻塞队列<String>;
4. 观察使用同步方法接受数据的行为,如果接收数据的中间存在 read == 0 的情况,可以在收数据为 0 时 解析数据;
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


# 2017-03-22

位置再往坐上移动一点 (-5,-10);
定位到海区自动弹窗==>需要获取你的位置啊; 	
海区自动弹出,然后读取消息;设置为静态成员变量
重新启动,海区位置也显示出来==>退出的时候保存一下no就行;


# 2017-03-25
tts语音停顿,sb需求,停顿你mb啊,就用了...来设置停顿
关于天气图标开始时不显示,还没查出来原因,现在不按照海区显示了,全都显示出来;
gps又忘坐上偏移了一点,这次偏移的是 -8,-16,看看行不行;
天气图标的显示:根据当前的放大方式,设置不同的偏移值;


# 2017-03-28
GPS图标盖在气象图标上,调整一下绘制位置;  vvv
语音格式化消息,之前加.不行,那么只能添加,试一下;   vvv
新消息来了之后,dismiss之前的消息框,然后再读入新的内容;=.在收到新的天气之后,调用zoomImageView.showPopupWindow(Param.AREA_NO)即可
风力风向互换位置  vvv
气象图标不显示这么回事???  找到bug,图标不显示是因为在app初始化的时候进行将其全部初始化为000了!!!所以在初始化的时候修改一下就好;
WeatherHelp()数组之前是19个,好像应该是20个吧;


# 2017-03-29
新天气来了之后没读出来,换了天气之后调用的是:showPopupWindow(int index),没有读出来???
解决方法:在本Fragment中去读吧


傻逼啊,既然天气消息来的时候要读一次,而且当前海区也要读一次,那前面的天气消息读的毛线啊;


# 2017-03-29
修改了进入其他Fragment,popupWindow依然显示的bug
将天气图标的偏移量统一改为10,不再改了
将gps的显示内圈蓝色加深,外圈半径缩小;

