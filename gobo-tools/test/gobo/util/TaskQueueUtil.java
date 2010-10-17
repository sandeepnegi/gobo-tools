package gobo.util;

import java.util.List;

import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.dev.LocalTaskQueue;
import com.google.appengine.api.labs.taskqueue.dev.QueueStateInfo;
import com.google.appengine.api.labs.taskqueue.dev.QueueStateInfo.TaskStateInfo;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;

public class TaskQueueUtil {

	public static void removeTasks() {
		LocalTaskQueue ltq = LocalTaskQueueTestConfig.getLocalTaskQueue();
		String defaultQueueName = QueueFactory.getDefaultQueue().getQueueName();
		QueueStateInfo qsi = ltq.getQueueStateInfo().get(defaultQueueName);
		List<TaskStateInfo> taskInfo = qsi.getTaskInfo();
		for (TaskStateInfo task : taskInfo) {
			ltq.deleteTask(defaultQueueName, task.getTaskName());
		}
	}
}
