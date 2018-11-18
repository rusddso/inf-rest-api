package com.infinera.accounts

import javax.inject.Inject

import com.infinera.repositories.AccountId
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

/**
  * Routes and URLs to the AccountController .
  */
class AccountRouter @Inject()(controller: AccountController) extends SimpleRouter {
  val prefix = "/v1/accounts"

  def link(id: AccountId): String = {
    import com.netaporter.uri.dsl._
    val url = prefix / id.toString
    url.toString()
  }

  override def routes: Routes = {

    case GET(p"/$id/get") =>
      controller.getAccountsById(id.toLong)

    case GET(p"/by_name" ? q_?"name=$name") =>
      controller.getAccountsByName(name.get)

//    case POST(p"/create" ? q_?"name=$name" & q_?"rank=$rank" & q_?"url=$url" & q_?"score=$score") =>
//      controller.createAccount(name.getOrElse("dummy"), rank.getOrElse("0").toInt, url.getOrElse("dummy"), score.getOrElse("0").toDouble)

    case POST(p"/$ids/delete") =>
      val idSeq = ids.split(",").map(_.toLong).distinct
      controller.deleteAccountsForIds(idSeq)

  }

}
