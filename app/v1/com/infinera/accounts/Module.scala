package com.infinera.accounts

import com.infinera.repositories._
import com.infinera.actors._
import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import play.api.libs.concurrent.AkkaGuiceSupport
import play.api.{Configuration, Environment}

/**
  * Sets up custom components for Play.
  *
  * https://www.playframework.com/documentation/2.5.x/ScalaDependencyInjection
  */
class Module(environment: Environment, configuration: Configuration)
  extends AbstractModule
    with ScalaModule
    with AkkaGuiceSupport {

  override def configure() = {
    bind(classOf[DBImplicits]).asEagerSingleton()

    // v1 repositories
    bind(classOf[AccountRepository]).asEagerSingleton()

    // v1 actors
    bindActor[com.infinera.actors.AccountMetricsActor]("accounts-actor")
  }
}
