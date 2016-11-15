package threadpool;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class BankTaskResource {
    private static PriorityTaskQueue<TaskParam> waitingTaskDataMap = new PriorityTaskQueue<TaskParam>();

	
	
	
    public List<TaskParam> getReadyTaskParams() {
        List<TaskParam> readyTaskParams = new LinkedList<TaskParam>();
        if (waitingTaskDataMap.size() > 0) {
            for (Iterator<TaskParam> taskItr = waitingTaskDataMap.iterator(); taskItr.hasNext();) {
                TaskParam taskParam = taskItr.next();
                generateReadyTask(readyTaskParams, taskParam);
                taskItr.remove();//找到，出队
                break;
            }
        }
        if (readyTaskParams.size() > 0)
            return readyTaskParams;
        else return null;
    }
	
    public void scheduleTask(TaskParam taskParam) {
        if (taskParam != null) {
            boolean permit = true;
            if (taskParam.isAsyn() && taskParam.getDistinct() != null) {//过滤存在的重复的任务
                for (TaskParam existTaskParam : waitingTaskDataMap) {
                    if (taskParam.getDistinct() != null && existTaskParam.getDistinct() != null && taskParam.getDistinct().equals(existTaskParam.getDistinct())) {
                        permit = false;
                        break;
                    }
                }
            }
            if (permit) waitingTaskDataMap.add(taskParam);
        }
    }
    
    
    
    private void generateReadyTask(List<TaskParam> readyTaskParams, TaskParam taskParam) {
        taskParam.setTaskResource(this);
        readyTaskParams.add(taskParam);
    }
	

}
