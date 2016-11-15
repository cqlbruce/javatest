package threadpool;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class TaskParam implements Comparable<TaskParam> {
	
	
    private TransCode transCode;//交易码
    private Map<String, Object> params;//运行需要的参数
    private long priority;//优先级，主要以时间戳为主
    private CountDownLatch latch;//同步执行完成后通知
    private boolean asyn;//是否异步
    private BankTaskResource taskResource;//哪个银行的资源，用于执行完成后归还前置机资源
    private Object result;//执行结果
    private Exception exception;
    private String distinct;//任务互斥变量，用于过滤重复的异步任务
    /**最高优先级*/
    public final static long PRIORITY_HIGH = 0;
    private AccountEntity accountEntity;
    
    
    public TaskParam(TransCode transCode , AccountEntity  accountEntity, long priority) {
    	this.transCode = transCode ; 
    	this.accountEntity = accountEntity;
    	this.priority = priority ; 
	}
    
    public TaskParam(TransCode transCode , AccountEntity accountEntity){
    	this(transCode , accountEntity , System.currentTimeMillis());
    }
    
    

	@Override
	public int compareTo(TaskParam o) {
		long r = this.priority - o.priority;
		if(r>0)
			return 1 ; 
		else if(r<0)
			return -1;
		else 	
			return 0;
	}


    public TransCode getTransCode() {
        return transCode;
    }

//    void setFrontEndConfig(FrontEndConfig frontEndConfig) {
//        this.frontEndConfig = frontEndConfig;
//    }
//
//    public FrontEndConfig getFrontEndConfig() {
//        return frontEndConfig;
//    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public void setParam(String key, Object o) {
        if (this.params == null) this.params = new HashMap<String, Object>();

        this.params.put(key, o);
    }

    void setAccountEntity(AccountEntity accountEntity) {
        this.accountEntity = accountEntity;
    }

    public AccountEntity getAccountEntity() {
        return accountEntity;
    }

    void setPriority(long priority) {
        this.priority = priority;
    }

    public long getPriority() {
        return priority;
    }

    void setTaskResource(BankTaskResource taskResource) {
        this.taskResource = taskResource;
    }

    /**
     * @return 返回true： 前置返回池正常；返回false: 有黑名单
     */
//    public boolean finish() {
//        return this.taskResource.finishTask(this);
//    }

    void setResult(Object result) {
        this.result = result;
    }

    public Object getResult() {
        return result;
    }

    void setAsyn(boolean asyn) {
        this.asyn = asyn;
    }

    public boolean isAsyn() {
        return asyn;
    }

    void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    void setException(Exception exception) {
        this.exception = exception;
    }

    public Exception getException() {
        return exception;
    }

    public void setDistinct(String distinct) {
        this.distinct = distinct;
    }

    public String getDistinct() {
        return distinct;
    }
}
