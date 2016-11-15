package threadpool;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class TestThread {
	
	public static void main(String[] args) throws Exception{
//		AccountEntity accountEntity = new AccountEntity();
//		accountEntity.setAccNo("0708030310");
//		accountEntity.setName("账号");
//		TaskScheduler.scheduleTask(TransCode.PAY, accountEntity , null, "1", "abc");
		while(true){
			Socket s = null ; 
			OutputStream os = null ; 
			try{
				s = new Socket("127.0.0.1", 9999);
				os = s.getOutputStream();
				os.write("hello".getBytes());
				Thread.currentThread().sleep(3000);
			}finally{
				os.close();
				s.close();
			}
		}
	}

}
