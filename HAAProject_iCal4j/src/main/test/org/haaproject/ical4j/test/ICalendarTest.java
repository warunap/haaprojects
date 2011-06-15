package org.haaproject.ical4j.test;

import java.util.Properties;
import java.util.UUID;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.haaproject.ical4j.Config;

public class ICalendarTest {

	private static final String CRLF = "\n";

	public static void main(String[] args) throws Exception {
		Properties props = System.getProperties();
		props.setProperty("mail.smtp.host", Config.getConfig("mail.smtp.host"));

		Session session = Session.getDefaultInstance(props);

		// Define message
		MimeMessage message = new MimeMessage(session);
		message.addHeaderLine("method=REQUEST");
		message.addHeaderLine("charset=\"UTF-8\"");
		message.addHeaderLine("component=VEVENT");
		message.setFrom(new InternetAddress(Config.getConfig("mail.from")));
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(Config.getConfig("mail.recipient")));
		message.setSubject("Meeting Request Using JavaMail");

		Multipart multipart = new MimeMultipart();

		MimeBodyPart htmlBodyPart = new MimeBodyPart();
		htmlBodyPart.setContent(
				"<html><body>HTML : You are requested to participlate in the review meeting.</body></html>",
				"text/html");
		multipart.addBodyPart(htmlBodyPart);

		StringBuffer buffer = new StringBuffer();
		// VCALENDAR begin
		buffer.append("BEGIN:VCALENDAR" + CRLF);
		buffer.append("PRODID:-//Microsoft Corporation//Outlook 9.0 MIMEDIR//EN" + CRLF);
		buffer.append("VERSION:2.0" + CRLF);
		buffer.append("METHOD:REQUEST" + CRLF);

		buffer.append(buildVevent());

		// VCALENDAR end
		buffer.append("END:VCALENDAR");

		System.out.println(buffer.toString());
		// Part two is attachment
		// Create second body part
		MimeBodyPart attachPart = new MimeBodyPart();
		String filename = "invitation.ics";
		attachPart.setFileName(filename);
		attachPart.setHeader("Content-Class", "urn:content-classes:calendarmessage");
		attachPart.setHeader("Content-ID", "calendar_message");
		attachPart.setDataHandler(new DataHandler(new ByteArrayDataSource(buffer.toString(), "text/calendar")));
		// Add part two
		multipart.addBodyPart(attachPart);

		// Put parts in message
		message.setContent(multipart);
		// send message
		Transport.send(message);

		System.out.println("send mail over");

	}

	/**
	 * @return
	 */
	private static StringBuffer buildVevent() {
		StringBuffer buffer = new StringBuffer();
		// VEVENT begin
		buffer.append("BEGIN:VEVENT" + CRLF);
		/*
		 * When used to publish busy time, the "ORGANIZER" property specifies the calendar user associated with the
		 * published busy time;
		 */
		buffer.append("ORGANIZER:mailto:" + Config.getConfig("calendar.organizer") + CRLF);
		// 會議參加人員
		/*
		 * The property defines an "Attendee" within a calendar component.<br> This property MUST be specified in an
		 * iCalendar object that specifies a group scheduled calendar entity. This property MUST NOT be specified in an
		 * iCalendar object when publishing the calendar information (e.g., NOT in an iCalendar object that specifies
		 * the publication of a calendar user's busy time, event, to-do or journal). This property is not specified in
		 * an iCalendar object that specifies only a time zone definition or that defines calendar entities that are not
		 * group scheduled entities, but are entities only on a single user's calendar.
		 */
		buffer.append("ATTENDEE;ROLE=CHAIR;RSVP=TRUE:mailto:lelenole@163.com" + CRLF);
		// 會議開始日期時間
		buffer.append("DTSTART:20110617T083000Z" + CRLF);
		// 會議結束日期時間
		buffer.append("DTEND:20110617T093000Z" + CRLF);
		// 會議地點
		buffer.append("LOCATION:Conference room 11" + CRLF);
		buffer.append("DESCRIPTION:This the description of the meeting." + CRLF + CRLF);
		// 標題
		buffer.append("SUMMARY:The summary" + CRLF);
		/*
		 * "UID" and "DTSTAMP" properties are specified to assist in proper sequencing of multiple free/busy time
		 * replies.
		 */
		buffer.append("UID:" + UUID.randomUUID().toString() + CRLF);
		buffer.append("DTSTAMP:20110612T213000Z" + CRLF);
		buffer.append("CATEGORIES:Meeting" + CRLF);
		buffer.append("PRIORITY:5" + CRLF);
		buffer.append("CLASS:PUBLIC" + CRLF);
		buffer.append("TRANSP:OPAQUE" + CRLF);
		buffer.append("SEQUENCE:0" + CRLF);

		buffer.append(buildValarm());

		// VEVENT end
		buffer.append("END:VEVENT" + CRLF);
		return buffer;
	}

	/**
	 * @return
	 */
	private static StringBuffer buildValarm() {
		StringBuffer buffer = new StringBuffer();
		// VALARM begin
		buffer.append("BEGIN:VALARM" + CRLF);
		buffer.append("TRIGGER:PT1440M" + CRLF);
		buffer.append("ACTION:DISPLAY" + CRLF);
		buffer.append("DESCRIPTION:Reminder" + CRLF);
		// VALARM end
		buffer.append("END:VALARM" + CRLF);
		return buffer;
	}

}
