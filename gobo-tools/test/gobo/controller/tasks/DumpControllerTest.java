package gobo.controller.tasks;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import gobo.ControllerTester;
import gobo.TestBase;
import gobo.util.SpreadsheetUtil;
import gobo.util.TaskQueueUtil;
import gobo.util.TestDataUtil;

import java.util.List;
import java.util.ResourceBundle;

import org.junit.Test;

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
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;

public class DumpControllerTest extends TestBase {

	ResourceBundle bundle = ResourceBundle.getBundle("authSub");
	final String authSubToken = bundle.getString("token");

	@Test
	public void runFirstTimeTest() throws Exception {

		List<Entity> bulkData = TestDataUtil.bulkData("TestKind1", 10);
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		datastore.put(bulkData);

		String[] kinds = { "TestKind1" };
		SpreadsheetEntry ssEntry = SpreadsheetUtil.createBlunkSpreadsheet(authSubToken, kinds);
		try {
			ControllerTester tester = new ControllerTester();
			Key controlKey =
				TaskQueueUtil.prepareDumpControlKey("TestKind1", ssEntry.getKey(), authSubToken);
			tester.request.setParameter("controlKey", KeyFactory.keyToString(controlKey));
			String run = tester.start("/tasks/dump");
			assertNull(run);

			LocalTaskQueue ltq = LocalTaskQueueTestConfig.getLocalTaskQueue();
			String defaultQueueName = QueueFactory.getDefaultQueue().getQueueName();
			QueueStateInfo qsi = ltq.getQueueStateInfo().get(defaultQueueName);
			List<TaskStateInfo> taskInfo = qsi.getTaskInfo();
			assertThat(taskInfo.get(0).getUrl(), equalTo("/tasks/dump.gobo?controlKey="
				+ KeyFactory.keyToString(controlKey)));

			TaskQueueUtil.removeTasks();

			// 2nd time.
			run = tester.start("/tasks/dump");
			assertNull(run);

			ltq = LocalTaskQueueTestConfig.getLocalTaskQueue();
			defaultQueueName = QueueFactory.getDefaultQueue().getQueueName();
			qsi = ltq.getQueueStateInfo().get(defaultQueueName);
			taskInfo = qsi.getTaskInfo();
			assertThat(taskInfo.get(0).getUrl(), equalTo("/tasks/dumpEnd.gobo?controlKey="
				+ KeyFactory.keyToString(controlKey)));

			TaskQueueUtil.removeTasks();

		} finally {
			SpreadsheetUtil.deleteSpreadsheet(authSubToken, ssEntry);
		}
	}

	@Test
	public void runWithDataTest() throws Exception {

		List<Entity> bulkData = TestDataUtil.bulkData("TestKind1", 10);
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		datastore.put(bulkData);

		String[] kinds = { "TestKind1" };
		SpreadsheetEntry ssEntry = SpreadsheetUtil.createSpreadsheet(authSubToken, kinds);

		try {
			ControllerTester tester = new ControllerTester();
			Key controlKey =
				TaskQueueUtil.prepareDumpControlKey("TestKind1", ssEntry.getKey(), authSubToken);
			tester.request.setParameter("controlKey", KeyFactory.keyToString(controlKey));
			String run = tester.start("/tasks/dump");
			assertNull(run);

			LocalTaskQueue ltq = LocalTaskQueueTestConfig.getLocalTaskQueue();
			String defaultQueueName = QueueFactory.getDefaultQueue().getQueueName();
			QueueStateInfo qsi = ltq.getQueueStateInfo().get(defaultQueueName);
			List<TaskStateInfo> taskInfo = qsi.getTaskInfo();

			// continue task chain
			assertThat(taskInfo.get(0).getUrl(), equalTo("/tasks/dump.gobo?controlKey="
				+ KeyFactory.keyToString(controlKey)));

			TaskQueueUtil.removeTasks();
		} finally {
			SpreadsheetUtil.deleteSpreadsheet(authSubToken, ssEntry);
		}
	}

}
