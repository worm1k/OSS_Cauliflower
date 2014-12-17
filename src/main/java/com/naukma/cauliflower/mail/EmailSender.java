package com.naukma.cauliflower.mail;

import com.naukma.cauliflower.dao.DAO;
import com.naukma.cauliflower.dao.TaskName;
import com.naukma.cauliflower.dao.UserRole;
import com.naukma.cauliflower.entities.User;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.SQLException;
import java.util.*;

public class EmailSender {

	private final static String USER_NAME = "provider.cauliflower.1@gmail.com";
	private final static String PASSWORD = "u'rebeautiful";
	private final static String INTERNET_PROVIDER = "Thanks for joining us! Internet provider \"CauliFlower\"";
	private final static String ENGINEERS = "Engineers";
	public final static String BAN_ACCOUNT="Your account is temporarily unavailable due to security reasons";
	public final static String SUBJECT_REGISTRATION="Registration in Cauliflower ";
	public final static String SUBJECT_BANNED="Your account has been blocked";
	public final static String SUBJECT_NEW_TASK="New Task";
	public final static String CHANGE_PASSWORD="Password has been changed";

	private static Session session;
	
	static {

		Properties props = new Properties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", "true");//If true, attempt to authenticate the user using the AUTH command. Defaults to false.
		props.put("mail.smtp.starttls.enable", "true"); //If true, enables the use of the STARTTLS command  to switch the connection to a TLS-protected connection before issuing any login commands.
		props.put("mail.smtp.host",EmailProperties.GMIAL.getHost());     //The SMTP server to connect to.
		props.put("mail.smtp.port", EmailProperties.GMIAL.getPort());//The SMTP server port to connect to
		props.put("mail.smtp.ssl.trust",EmailProperties.GMIAL.getHost());
		//If set, and a socket factory hasn't been specified, enables use of a MailSSLSocketFactory. If set to "*", all hosts are trusted.
		// If set to a whitespace separated list of hosts, those hosts are trusted.
		// Otherwise, trust depends on the certificate the server presents.

		session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(USER_NAME, PASSWORD);
			}
		});

	}
	//
	/**
	 *
	 * @param users  - group of users
	 * @param subject - subject of email
	 * @param body     - body of email
	 * @param template - appropriate template
	 */
	public static void sendEmailToGroup(List<User> users, String subject,String body, Template template){
			ArrayList<String> emails= new ArrayList<String>(users.size());

				for(User user:users){
					emails.add(user.getEmail());
				}

		   send(emails,ENGINEERS,subject,body,template);
	}

	/**
	 *
	 * @param user  -recipient
	 * @param subject - subject of email
	 * @param body   - body of email
	 * @param template  - appropriate template
	 */
	public static void sendEmail(final User user, String subject,String body, Template template){

		StringBuilder to = new StringBuilder();
		to.append(user.getFirstName()).append(" ").append(user.getLastName());

		send(new ArrayList<String>(){{add(user.getEmail());}},to.toString(),subject,body,template);
	}

	/**
	 *
	 * @param emails
	 * @param personName
	 * @param subject of the email
	 * @param body    of the email
	 * @param template
	 */
	private static void send( ArrayList<String> emails,String personName, String subject,String body, Template template) {
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(USER_NAME));
			//setting recipient
			for(String email:emails) {
				message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse(email));
			}
			message.setSubject(subject);
			//define constant to fill template
			final String to="to";
			final String bodyValue="body";
			final String from="from";

			Map<String, String> rootMap = new HashMap<String, String>();
			rootMap.put(to, personName);
			rootMap.put(bodyValue, body);
			rootMap.put(from, INTERNET_PROVIDER);
			Writer out = new StringWriter();
			try {
				template.process(rootMap, out);
			} catch (TemplateException e) {
				//todo logger
				System.out.println("error occured when filling template");
			} catch (IOException e) {
				//todo logger
				e.printStackTrace();
			}

			message.setContent(out.toString(), "text/html");
			Transport.send(message);

		} catch (MessagingException e) {
			e.printStackTrace();
			//todo logger
		}

	
	}

	/**
	 *
	 * @param templateName   - template name
	 * @param tempateDir - to get template path you should invoke context.getRealPath("/WEB-INF/mail/") ;
	 * @return
	 */
	public static Template getTemplate(String templateName,String tempateDir){
		Configuration cfg = new Configuration();
		try {
			cfg.setDirectoryForTemplateLoading(new java.io.File(
					tempateDir));
		} catch (IOException e) {
			e.printStackTrace();
			//todo logger
		}
		Template template = null;
		try {
			template = cfg.getTemplate(templateName);

		} catch (IOException e) {
			//todo logger
			System.out.println("error occured when getting template file");
		}

		return template;
	}

	public static void sendNotification(String fullPath, UserRole role, TaskName taskName) throws SQLException {
		StringBuilder message= new StringBuilder();
		message.append("<p>New task has been created!</p> <p style=\"text-transform:none;\">TaskName: <b>");
		message.append(taskName.toString());
		message.append("</b></p>");
		sendEmailToGroup(DAO.INSTANCE.getUsersByUserRole(role), EmailSender.SUBJECT_NEW_TASK, message.toString(), EmailSender.getTemplate("/mailTemplate.ftl", fullPath));
	}
}
