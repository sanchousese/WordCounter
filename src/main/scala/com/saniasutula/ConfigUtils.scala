package com.saniasutula

import com.typesafe.config.ConfigFactory

object ConfigUtils {
  val facebookConfig  = ConfigFactory.load().getConfig("facebook")
  val domain          = facebookConfig.getString("domain")
  val appId           = facebookConfig.getString("appId")
  val appSecret       = facebookConfig.getString("appSecret")

  val mailConfig      = ConfigFactory.load().getConfig("mail")
  val senderMail      = mailConfig.getString("senderMail")
  val senderPassword  = mailConfig.getString("senderPassword")
}
