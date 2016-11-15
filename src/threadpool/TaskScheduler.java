package threadpool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class TaskScheduler {
	
	
	private static int threadcouts = 6 ; 
    //待处理数据的读写锁。调度线程用写锁，添加待处理数据用写锁，处理线程用读锁（可以并发）。
    private static final ReentrantLock taskLock = new ReentrantLock();
    //key:bank, value:银行待处理的数据
    private final static ConcurrentHashMap<String, BankTaskResource> waitingResourceMap = new ConcurrentHashMap<String, BankTaskResource>();
    //就绪队列，可以处理的任务，已申请到前置机资源
    private final static PriorityBlockingQueue<TaskParam> readyQueue = new PriorityBlockingQueue<TaskParam>();
    
    private static final Condition taskSignal = taskLock.newCondition();//如果没有符合条件的任务，则等待

    private static ExecutorService threadPool;

    private static AtomicInteger threadPoolPaymentCounter = new AtomicInteger(0);//用于辅助关闭工作线程(支付线程不要随便关闭，其他线程可以直接关闭)

	
	public static void init(){
		
		
		AppStatus.setRunning();
		//调度线程 
		TaskSchedulerThread taskSchedulerThread = new TaskSchedulerThread();
		taskSchedulerThread.setDaemon(true);
		taskSchedulerThread.setName("task-scheduler");
        taskSchedulerThread.start();
        
        SocketThread socketThread = new SocketThread();
        socketThread.setDaemon(true);
        socketThread.setName("socket-scheduler");
        socketThread.start();
		
		//工作线程
        threadPool = Executors.newFixedThreadPool(threadcouts);
		for(int i=0 ; i<threadcouts ; i++){
			threadPool.execute(new TaskRunner(i));
		}
		
	}
	
	public static Object scheduleTask(TransCode transCode , AccountEntity accountEntity,Map<String, Object> params , String distinct , String bank){
        CountDownLatch latch = null;
        TaskParam taskParam = new TaskParam(transCode, accountEntity);
        taskParam.setParams(params);
//        taskParam.setAsyn(asyn);
        taskParam.setDistinct(distinct);
        latch = new CountDownLatch(1);
        taskParam.setLatch(latch);
        taskLock.lock();
        try {
            BankTaskResource taskResource = new BankTaskResource();
            taskResource.scheduleTask(taskParam);
            taskSignal.signalAll();
        } finally {
            taskLock.unlock();
        }
        if (latch != null) {
            try {
                latch.await();//等等返回结果
            } catch (InterruptedException e) {
            	
            }
        }
        System.out.println("game over ...");
        return  taskParam.getResult();
		
	}
	
	
    private static class TaskSchedulerThread extends Thread {
    	public void run(){
    		while(AppStatus.isRunning()){
    			taskLock.lock();
    			try{
                    BankTaskResource taskResource = new BankTaskResource();
                    Set<Map.Entry<String , BankTaskResource>> resourceEntrySet = waitingResourceMap.entrySet();
                    List<TaskParam> tasks = null ; 
//                    for(Iterator<Map.Entry<String, BankTaskResource>> itr = resourceEntrySet.iterator() ; itr.hasNext() ; ){
//                    	taskResource = itr.next().getValue();
                        tasks = taskResource.getReadyTaskParams();
                        if(tasks==null)continue ;
                        for(TaskParam task : tasks){
                			System.out.println("task................ running");
                        	readyQueue.offer(task);
                        }
//                    }
                    try{
                    	taskSignal.await();
            			System.out.println("...after await ................ running");
                    }catch (InterruptedException e) {
                        //ignore
                    }
    			}catch(Exception e){
    				e.printStackTrace();
    			}finally{
    				taskLock.unlock();
    			}
    		}
            System.out.println("调度服务已停止..........");
    	}
    }
	
	private static class TaskRunner implements Runnable{
		
		private int index = 0 ;  
		
		public TaskRunner(int index){
			this.index = index;
		}

		@Override
		public void run() {
			Thread.currentThread().setName("task-worker-" + index);
            loopRun();
		}
		
		private void loopRun(){
			boolean isPay = true ; 
			while(AppStatus.isRunning()){
    			System.out.println("task-worker------"+index+"----- running");
				TaskParam taskParam ; 
				try{
                    taskParam = readyQueue.take();
				}catch(InterruptedException e){
					continue ; 
				}				
                //支付业务+1
                isPay = taskParam.getTransCode() == TransCode.PAY;
                if (isPay) threadPoolPaymentCounter.incrementAndGet();
                try {
                    Object result = doTask(taskParam);
                    taskParam.setResult(result);
                } catch (Exception e) {
                    taskParam.setException(e);
                    System.err.println("has an exception ...");
                } finally{
                    if (isPay) threadPoolPaymentCounter.decrementAndGet();
                    if (taskParam.getLatch() != null) {//同步的，通知结果.
                        taskParam.getLatch().countDown();//通知等待同步线程结果
                    }
                }
			}
		}
		
        private Object doTask(TaskParam taskParam) {
//            AccountEntity accountEntity = taskParam.getAccountEntity();
            System.out.println("i'm very happy ..." + taskParam.getDistinct()); 
            return "is your result";
        }
	}

	public static void main(String[] args) {
		init();
	}
	
	private static class SocketThread extends Thread{
		
		public void run(){
			try{
				openSocket();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		public void openSocket() throws IOException{
			ServerSocket ss = new ServerSocket(9999);
			while(true){
				Socket s = ss.accept();
				InputStream is = s.getInputStream(); 
				BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
				String str = null ; 
				while((str=br.readLine())!=null){
					System.out.println("a msg coming==============");
					AccountEntity accountEntity = new AccountEntity();
					accountEntity.setAccNo("0708030310");
					accountEntity.setName("账号");
					TaskScheduler.scheduleTask(TransCode.PAY, accountEntity , null, "1", "abc");
				}
			}
		}
	} 
	
}
