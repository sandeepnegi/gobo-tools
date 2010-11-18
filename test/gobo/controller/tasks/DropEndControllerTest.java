package gobo.controller.tasks;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import gobo.ControllerTester;
import gobo.TestBase;
import gobo.util.TaskQueueUtil;

import java.util.List;

import org.junit.Test;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.dev.LocalTaskQueue;
import com.google.appengine.api.labs.taskqueue.dev.QueueStateInfo;
import com.google.appengine.api.labs.taskqueue.dev.QueueStateInfo.TaskStateInfo;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;

public class DropEndControllerTest extends TestBase {

	@Test
	public void runTest() throws Exception {

		Key controlKey = TaskQueueUtil.prepareDropControlKey("TestKind1");

		ControllerTester tester = new ControllerTester();
		tester.request.setParameter("controlKey", KeyFactory.keyToString(controlKey));

		String run = tester.start("/tasks/dropEnd");
		assertNull(run);

		LocalTaskQueue ltq = LocalTaskQueueTestConfig.getLocalTaskQueue();
		String defaultQueueName = QueueFactory.getDefaultQueue().getQueueName();
		QueueStateInfo qsi = ltq.getQueueStateInfo().get(defaultQueueName);
		List<TaskStateInfo> taskInfo = qsi.getTaskInfo();
		
		// finish task chain
		assertThat(taskInfo.size(), is(0));
	}
}
