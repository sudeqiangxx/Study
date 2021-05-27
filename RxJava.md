RxJava 操作符
map ：转换事件，返回普通事件。
flatMap： 转换事件，返回Observable
concatMap: concatMap 与flatMap 的唯一区别就是concatMap保证了顺序
subscribeOn: 规定被观察者所在的线程
observeOn： 规定下面知心的消费者所在的线程。
take：接受一个long 型参数count ，代表至多接收count个数据
debounce ：去除发送频率过快的项，常在重复点击解决上，配合RxBinging使用效果
timer： 定时任务，多少时间后发送事件。
interval ：每隔一定时间执行一些任务。
skip 跳过前多少个事件。
distingct 去重
takeUntil 直到到一定条件的是停下，也可以接受另外一个被观察者，当这个被观察者结束之后则停止第一个被观察者。
zip 专用于合并事件，该合并不是连接（连接操作符后面会说），而是两两配对，也就意味着，最终配对出的Observable 发送事件数目只和少的那个相同。