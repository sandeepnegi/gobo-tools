package gobo.service;

import java.io.IOException;

import com.google.appengine.api.datastore.Email;
import com.google.appengine.api.mail.MailService;
import com.google.appengine.api.mail.MailServiceFactory;
import com.google.appengine.api.mail.MailService.Message;
import com.google.apphosting.api.ApiProxy;

public class GbMailService {

	public static void sendMail(Email email, long controlId, String task) {

		MailService mailService = MailServiceFactory.getMailService();
		Message message1 = new Message();
		message1.setTo(email.getEmail());
		message1.setSubject("[" + controlId + "]" + task + " Ended");
		message1.setSender("gobo-tools@" + ApiProxy.getCurrentEnvironment().getAppId() + ".appspotmail.com");
		message1.setTextBody("[" + controlId + "]" + task + " Ended.");
		try {
			mailService.send(message1);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			;
		}
	}
}
