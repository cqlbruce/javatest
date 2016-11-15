
package threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolTest {
	
    private static ExecutorService threadPool;

    public static void main(String[] args) {
    	
        threadPool = Executors.newFixedThreadPool(10);
//      	1.线程池状态
//      　　2.任务的执行
//      　　3.线程池中的线程初始化
//      　　4.任务缓存队列及排队策略
//      　　5.任务拒绝策略
//      　　6.线程池的关闭
//      　　7.线程池容量的动态调整
        
        

	}

}
