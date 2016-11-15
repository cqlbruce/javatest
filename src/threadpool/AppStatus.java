package threadpool;

public class AppStatus {
	
	public static volatile Status appStatus = Status.stoped;
	
	
	public static boolean isStarting(){
		return AppStatus.appStatus == Status.starting;
	}
	
	public static boolean isRunning(){
		return AppStatus.appStatus == Status.running;
	}
	
	public static boolean isStoped(){
		return AppStatus.appStatus == Status.stoped;
	}
	
	public static void setRunning(){
		AppStatus.appStatus = Status.running;
	}
	
	public enum Status{
		starting("starting" , "启动"),running("running","运行"),stoped("stoped","停止");
		
		private String code ; 
		private String desc ; 
		
		private Status(String code , String desc){
			this.code=code ;
			this.desc=desc ; 
		}

		public String getCode() {
			return code;
		}

		public String getDesc() {
			return desc;
		}
		
	}

}
