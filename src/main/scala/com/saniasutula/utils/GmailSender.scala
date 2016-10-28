package com.saniasutula.utils

import java.util._
import javax.mail._
import javax.mail.internet._

import com.saniasutula.Boot

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object GmailSender {
  def installRegularlyMail(email: String) {
    val scheduler = Boot.system.scheduler
    val task = new Runnable {
      override def run() {
        GmailSender.sendMain(email, "Subject", "Body")
      }
    }
    scheduler.schedule(1.day.fromNow.timeLeft, 1.day, task)
  }

  def sendMain(email: String, subject: String, body: String) {
    val properties = new Properties()
    properties.put("mail.smtp.host", "localhost")
    val session = Session.getDefaultInstance(properties)
    val message = new MimeMessage(session)

    message.setFrom(new InternetAddress(ConfigUtils.senderMail))
    message.addRecipients(Message.RecipientType.TO, email)
    message.setSubject(subject)
    message.setText(body)

    Transport.send(message, ConfigUtils.senderMail, ConfigUtils.senderPassword)
  }
}
