package com.naukma.cauliflower.mail;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import com.naukma.cauliflower.entities.User;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class EmailSender {
	private final static String USER_NAME = "provider.cauliflower.1@gmail.com";
	private final static String PASSWORD = "u'rebeautiful";
	private final static String INTERNET_PROVIDER = "INTERNET_PROVIDER";
	private static Session session;
	
	static {

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(USER_NAME, PASSWORD);
			}
		});
	}

	
	public static void sendEmail( User user, String subject,String body, Template template) {
		try {
			Message message = new MimeMessage(session);

			message.setFrom(new InternetAddress(USER_NAME));
			message.setRecipients(Message.RecipientType.TO,
			InternetAddress.parse(user.getEmail()));
			message.setSubject(subject);

			Map<String, String> rootMap = new HashMap<String, String>();
			StringBuilder to = new StringBuilder();
			to.append(user.getFirstName()).append(" ").append(user.getLastName());
			rootMap.put("to", to.toString());
			 rootMap.put("body", body);
			// TODO
			rootMap.put("from", INTERNET_PROVIDER);
			Writer out = new StringWriter();
			try {
				template.process(rootMap, out);
			} catch (TemplateException e) {

				System.out.println("error occured when filling template");
			} catch (IOException e) {

				e.printStackTrace();
			}

			message.setContent(out.toString(), "text/html");
			Transport.send(message);
			System.out.println("DONE");

		} catch (MessagingException e) {
			e.printStackTrace();
		}

	
	}


	public static Template getTemplate(String templatePath,String tempateDir){
		Configuration cfg = new Configuration();
		try {
			cfg.setDirectoryForTemplateLoading(new java.io.File(
					tempateDir));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Template template = null;
		try {
			template = cfg.getTemplate(templatePath);

		} catch (IOException e) {
			System.out.println("error occured when getting template file");
		}
		return template;
	}
}
