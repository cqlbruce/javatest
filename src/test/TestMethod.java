package test;

import java.lang.reflect.Method;

public class TestMethod {
	
	public static final String methods = "test1,test2";
	
	public static void main(String[] args) {
		TestMethod tm = new TestMethod();
		tm.testMethod();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testMethod() {
		try{
			Class clazz = this.getClass();  
			System.out.println(clazz);
			String[] methodList = methods.split(",");
			for(String method : methodList){
				Method m = clazz.getMethod(method);
				m.invoke(this);
			}
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("has an Exception to TestMethod");
		}
		
	}
	
	public void test1(){
		System.out.println("coming test1 ...");
	}
	public void test2(){
		System.out.println("coming test2 ...");
	}

}
