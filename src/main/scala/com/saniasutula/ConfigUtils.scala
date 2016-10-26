package com.saniasutula

import com.typesafe.config.ConfigFactory

object ConfigUtils {
  val config    = ConfigFactory.load().getConfig("facebook")
  val domain    = config.getString("domain")
  val appId     = config.getString("appId")
  val appSecret = config.getString("appSecret")
}
