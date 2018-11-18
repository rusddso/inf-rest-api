package com.infinera.actors

import javax.inject.Inject

import akka.actor.{Actor, Props}
import com.infinera.resourcehandlers.AccountResourceHandler
import play.api.Configuration

import scala.concurrent.ExecutionContext

case class ReadAccounts(name: String)

object AccountMetricsActor {

  def props(configuration: Configuration,
            accountResourceHandler: AccountResourceHandler)(implicit ec: ExecutionContext): Props = {
    Props(new AccountMetricsActor(configuration, accountResourceHandler)(ec))
  }
}

class AccountMetricsActor @Inject()(configuration: Configuration,
                                    accountResourceHandler: AccountResourceHandler)(implicit ec: ExecutionContext) extends Actor {

  def receive = {
    case ReadAccounts(name: String) => {
      readAccountsByName(name)
    }
  }

  private def readAccountsByName(name: String) = {
    for {
      accountSeq <- accountResourceHandler.findByName(name)
    } yield {
      println(accountSeq)
    }
  }

}
