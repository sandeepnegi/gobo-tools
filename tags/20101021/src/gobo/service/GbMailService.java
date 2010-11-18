package gobo.service;

import java.io.IOException;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Email;
import com.google.appengine.api.mail.MailService;
import com.google.appengine.api.mail.MailServiceFactory;
import com.google.appengine.api.mail.MailService.Message;
import com.google.apphosting.api.ApiProxy;

public class GbMailService {

	private static final Logger logger = Logger.getLogger(GbMailService.class.getName());

	public static void sendMail(Email email, long controlId, String message) {

		MailService mailService = MailServiceFactory.getMailService();
		Message message1 = new Message();
		message1.setTo(email.getEmail());
		message1.setSubject("The task no [" + controlId + "]" + message);
		message1.setSender("gobo-tools@"
			+ ApiProxy.getCurrentEnvironment().getAppId()
			+ ".appspotmail.com");
		message1.setTextBody("The task no [" + controlId + "]" + message);
		try {
			mailService.send(message1);
		} catch (IOException e) {
			logger.warning(e.getMessage());
		}
	}
}
