package gobo.controller.tasks;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import gobo.ControllerTester;
import gobo.TestBase;
import gobo.util.TaskQueueUtil;
import gobo.util.TestDataUtil;

import java.util.List;

import org.junit.Test;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.dev.LocalTaskQueue;
import com.google.appengine.api.labs.taskqueue.dev.QueueStateInfo;
import com.google.appengine.api.labs.taskqueue.dev.QueueStateInfo.TaskStateInfo;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;

public class DropControllerTest extends TestBase {

	@Test
	public void runWithoutDataTest() throws Exception {

		Key controlKey = TaskQueueUtil.prepareDropControlKey("TestKind1");

		ControllerTester tester = new ControllerTester();
		tester.request.setParameter("controlKey", KeyFactory.keyToString(controlKey));

		String run = tester.start("/tasks/drop");
		assertNull(run);

		LocalTaskQueue ltq = LocalTaskQueueTestConfig.getLocalTaskQueue();
		String defaultQueueName = QueueFactory.getDefaultQueue().getQueueName();
		QueueStateInfo qsi = ltq.getQueueStateInfo().get(defaultQueueName);
		List<TaskStateInfo> taskInfo = qsi.getTaskInfo();

		// finish task chain
		assertThat(taskInfo.get(0).getUrl(), equalTo("/tasks/dropEnd.gobo?controlKey="
			+ KeyFactory.keyToString(controlKey)));

		TaskQueueUtil.removeTasks();
	}

	@Test
	public void runWithDataTest() throws Exception {

		List<Entity> bulkData = TestDataUtil.bulkData("TestKind1", 10);
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		datastore.put(bulkData);

		Key controlKey = TaskQueueUtil.prepareDropControlKey("TestKind1");

		ControllerTester tester = new ControllerTester();
		tester.request.setParameter("controlKey", KeyFactory.keyToString(controlKey));

		String run = tester.start("/tasks/drop");
		assertNull(run);

		LocalTaskQueue ltq = LocalTaskQueueTestConfig.getLocalTaskQueue();
		String defaultQueueName = QueueFactory.getDefaultQueue().getQueueName();
		QueueStateInfo qsi = ltq.getQueueStateInfo().get(defaultQueueName);
		List<TaskStateInfo> taskInfo = qsi.getTaskInfo();

		// continue task chain
		assertThat(taskInfo.get(0).getUrl(), equalTo("/tasks/drop.gobo?controlKey="
			+ KeyFactory.keyToString(controlKey)));
		TaskQueueUtil.removeTasks();

	}

	@Test(expected = EntityNotFoundException.class)
	public void runOverRetryCountTest() throws Exception {

		Key controlKey = TaskQueueUtil.prepareDropControlKey("TestKind1");

		ControllerTester tester = new ControllerTester();
		tester.request.setHeader("X-AppEngine-TaskRetryCount", "6");
		tester.request.setParameter("controlKey", KeyFactory.keyToString(controlKey));
		String run = tester.start("/tasks/drop");
		assertNull(run);

		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		ds.get(controlKey);
	}
}
