### OkHttp
1.初始化，用的是Builder建造者模式，把SocketFactory ，HostVerifier 配置设置好了，建造者模式：解决传递多个参数进行对象的创建，使其我们的

构造方法繁琐，不能一目了然。

2.发送请求：同步和异步请求，异步需要看equeue的内部实现，线程池。

3. 如何发起请求，如何响应处理等，都是需要了解okhttp中的拦截器，就是责任链模式，重要的几个拦截器 ConnectInterceptor ，CacheInterceptor


4.OkHttp 大概流程是这样的：首先就会新建一个call 来帮我们请求做事，这里是RealCall 这里面有两个方式请求，一个是同步请求

一个是异步请求。这里的同步执行的话， 直接就返回一个 getResponseWithInterceptorChain() 这个请求结果了，这个方法里很重要，

他完成了所有责任链的事，最后把响应结果返回回去，所以，这里肯定是耗时的。

另一个是异步请求：异步请求就复杂一些了：调用异步请求，会调用RealCall中的enqueue() 方法，首先会调用check(executed.compareAndSet(false, true)) { "Already Executed" }

是否已经发起了。然后会调用client.dispatcher.enqueue(AsyncCall(responseCallback)) 这里的这个方法 会直接新建一个异步任务 AsyncCall 传递一个

responseCallback 进去来进行回调，AsyncCall 是RealCall中的一个内部类。当然，okhttp已经换成了kotlin来进行实现，然而

AsyncCall 实现了一个Runnable 接口，那就明显了，肯定是仍给线程了，那我们就探探究竟了，到底是扔到什么地方去了。

AsyncCall中有一个方法 executeOn(service:ExecutorService) 很明显，线程池。可以大概看一下这个线程池的创建

'
@get:JvmName("executorService") val executorService: ExecutorService
    get() {
      if (executorServiceOrNull == null) {
        executorServiceOrNull = ThreadPoolExecutor(0, Int.MAX_VALUE, 60, TimeUnit.SECONDS,
            SynchronousQueue() //同步队列, threadFactory("$okHttpName Dispatcher", false))
      }
      return executorServiceOrNull!!
    }
'

紧接着 就会调用   executorService.execute(this) 这个方法开始执行，那现在就可以看run方法中的回调了，run方法中最终还是调用了

 val response = getResponseWithInterceptorChain() 这个方法来获取响应，所以，getResponseWithInterceptorChain 在okhttp

 中是重中之重。分析一下这个方法吧：


   @Throws(IOException::class)
     internal fun getResponseWithInterceptorChain(): Response {
       // 111111111111111111111111111111
       val interceptors = mutableListOf<Interceptor>()
       interceptors += client.interceptors       //           (1)
       interceptors += RetryAndFollowUpInterceptor(client)
       interceptors += BridgeInterceptor(client.cookieJar)
       interceptors += CacheInterceptor(client.cache)
       interceptors += ConnectInterceptor
       if (!forWebSocket) {
         interceptors += client.networkInterceptors
       }
       interceptors += CallServerInterceptor(forWebSocket)
       // 111111111111111111111111111111

       val chain = RealInterceptorChain(
           call = this,
           interceptors = interceptors,
           index = 0,
           exchange = null,
           request = originalRequest,
           connectTimeoutMillis = client.connectTimeoutMillis,
           readTimeoutMillis = client.readTimeoutMillis,
           writeTimeoutMillis = client.writeTimeoutMillis
       )

       // 222222222222222222222222222222222222
       var calledNoMoreExchanges = false
       try {
         val response = chain.proceed(originalRequest)
         if (isCanceled()) {
           response.closeQuietly()
           throw IOException("Canceled")
         }
         return response
       } catch (e: IOException) {
         calledNoMoreExchanges = true
         throw noMoreExchanges(e) as Throwable
       } finally {
         if (!calledNoMoreExchanges) {
           noMoreExchanges(null)
         }
       }

        // 222222222222222222222222222222222222
     }


 上面代码1处就是添加把各种拦截器添加上去包括开发者添加的一些拦截器都加进去了,2 处是真正实现链式调用的 ，把我们的原始请求
 添加到责任链中去执行，每个拦截器完成不一样的工作，最后把从服务器中拿到的响应返回给我们，责任链模式完好清晰的完成该完成的工作，
 分工明确，达到了单一职责
