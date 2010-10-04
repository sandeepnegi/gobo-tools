package gobo.service;

import java.io.IOException;

import com.google.appengine.api.mail.MailService;
import com.google.appengine.api.mail.MailServiceFactory;
import com.google.appengine.api.mail.MailService.Message;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class GbMailService {

	public static void sendMail(long controlId, String task) {

		UserService us = UserServiceFactory.getUserService();
		User user = us.getCurrentUser();
		//String to = (user != null) ? user.getEmail() : "a@b.c";
		String to = "a@b.c";

		MailService mailService = MailServiceFactory.getMailService();
		Message message1 = new Message();
		message1.setTo(to);
		message1.setSubject("["+ controlId + "]" + task + "終了");
		message1.setSender("gobo@appspot.com");
		message1.setTextBody("["+ controlId + "]" + task + "終了");
//		try {
//			//mailService.send(message1);
//		} catch (IOException e) {
//			System.err.println(e.getMessage());;
//		}
	}
}
