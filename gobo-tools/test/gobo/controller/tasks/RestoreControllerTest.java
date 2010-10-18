package gobo.controller.tasks;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import gobo.ControllerTester;
import gobo.TestBase;
import gobo.util.SpreadsheetUtil;
import gobo.util.TaskQueueUtil;

import java.util.List;
import java.util.ResourceBundle;

import org.junit.Test;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.dev.LocalTaskQueue;
import com.google.appengine.api.labs.taskqueue.dev.QueueStateInfo;
import com.google.appengine.api.labs.taskqueue.dev.QueueStateInfo.TaskStateInfo;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;

public class RestoreControllerTest extends TestBase {

	ResourceBundle bundle = ResourceBundle.getBundle("authSub");
	final String authSubToken = bundle.getString("token");

	@Test
	public void runWithoutDataTest() throws Exception {

		String[] kinds = { "TestKind1" };
		SpreadsheetEntry ssEntry = SpreadsheetUtil.createSpreadsheet(authSubToken, kinds);
		try {
			Key controlKey =
				TaskQueueUtil.prepareRestoreControlKey("TestKind1", ssEntry.getKey(), authSubToken);

			ControllerTester tester = new ControllerTester();
			tester.request.setParameter("controlKey", KeyFactory.keyToString(controlKey));
			String run = tester.start("/tasks/restore");
			assertNull(run);

			LocalTaskQueue ltq = LocalTaskQueueTestConfig.getLocalTaskQueue();
			String defaultQueueName = QueueFactory.getDefaultQueue().getQueueName();
			QueueStateInfo qsi = ltq.getQueueStateInfo().get(defaultQueueName);
			List<TaskStateInfo> taskInfo = qsi.getTaskInfo();

			// finish task chain
			assertThat(taskInfo.get(0).getUrl(), equalTo("/tasks/restoreEnd.gobo?controlKey="
				+ KeyFactory.keyToString(controlKey)));

		} finally {
			TaskQueueUtil.removeTasks();
			SpreadsheetUtil.deleteSpreadsheet(authSubToken, ssEntry);
		}

	}

	@Test
	public void runWithDataTest() throws Exception {

		String[] kinds = { "TestKind1" };
		SpreadsheetEntry ssEntry = SpreadsheetUtil.createAndDumpSpreadsheet(authSubToken, kinds);
		try {
			Key controlKey =
				TaskQueueUtil.prepareRestoreControlKey("TestKind1", ssEntry.getKey(), authSubToken);

			ControllerTester tester = new ControllerTester();
			tester.request.setParameter("controlKey", KeyFactory.keyToString(controlKey));
			String run = tester.start("/tasks/restore");
			assertNull(run);

			LocalTaskQueue ltq = LocalTaskQueueTestConfig.getLocalTaskQueue();
			String defaultQueueName = QueueFactory.getDefaultQueue().getQueueName();
			QueueStateInfo qsi = ltq.getQueueStateInfo().get(defaultQueueName);
			List<TaskStateInfo> taskInfo = qsi.getTaskInfo();

			// finish task chain
			assertThat(taskInfo.get(0).getUrl(), equalTo("/tasks/restore.gobo?controlKey="
				+ KeyFactory.keyToString(controlKey)));

		} finally {
			TaskQueueUtil.removeTasks();
			SpreadsheetUtil.deleteSpreadsheet(authSubToken, ssEntry);
		}
	}
}
