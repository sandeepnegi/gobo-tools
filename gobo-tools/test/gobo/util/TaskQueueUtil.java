package gobo.util;

import gobo.model.GbControl;

import java.util.Date;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
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

	public static Key prepareDropControlKey(String kindName) {

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Key controlId = datastore.allocateIds("drop", 1).getStart();
		Key childKey = KeyFactory.createKey(controlId, GbControl.NAME, kindName);
		Entity control = new Entity(childKey);
		control.setProperty(GbControl.KIND_NAME, kindName);
		control.setProperty(GbControl.COUNT, 0);
		control.setProperty(GbControl.UPDATE_DATE, new Date());
		datastore.put(control);

		return childKey;
	}
	
	public static Key prepareDumpControlKey(String kindName, String ssKey, String token) {
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Key controlId = datastore.allocateIds("dump", 1).getStart();
		Key childKey = KeyFactory.createKey(controlId, GbControl.NAME, kindName);
		Entity control = new Entity(childKey);
		control.setProperty(GbControl.KIND_NAME, kindName);
		control.setProperty(GbControl.COUNT, 0);
		control.setProperty(GbControl.UPDATE_DATE, new Date());
		control.setProperty(GbControl.AUTH_SUB_TOKEN, token);
		control.setProperty(GbControl.SPREADSHEET_KEY, ssKey);
		
		datastore.put(control);
		return childKey;
	}

	public static Key prepareRestoreControlKey(String kindName, String ssKey, String token) {
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Key controlId = datastore.allocateIds("restore", 1).getStart();
		Key childKey = KeyFactory.createKey(controlId, GbControl.NAME, kindName);
		Entity control = new Entity(childKey);
		control.setProperty(GbControl.KIND_NAME, kindName);
		control.setProperty(GbControl.COUNT, 2); // NOT FROM 0
		control.setProperty(GbControl.UPDATE_DATE, new Date());
		control.setProperty(GbControl.AUTH_SUB_TOKEN, token);
		control.setProperty(GbControl.SPREADSHEET_KEY, ssKey);
		
		datastore.put(control);
		return childKey;
	}
}
