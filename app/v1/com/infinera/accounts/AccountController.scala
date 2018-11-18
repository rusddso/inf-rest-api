package com.infinera.accounts

import javax.inject.{Inject, Named}

import akka.actor.ActorRef
import akka.util.Timeout
import com.infinera.resourcehandlers.AccountResourceHandler
import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

case class AccountFormInput(accountId: Int, profileId: Int, createdByUser: Option[Int])

/**
  * Takes HTTP requests and produces JSON.
  */
class AccountController @Inject()(
                                action: AccountAction,
                                accountHandler: AccountResourceHandler,
                                dbExecuter: DBImplicits,
                                @Named("accounts-actor") accountsActor: ActorRef)(implicit ec: ExecutionContext)
  extends Controller {

  implicit val timeout = Timeout(10.minutes)

  private val form: Form[AccountFormInput] = {
    import play.api.data.Forms._

    Form(
      mapping(
        "account_id" -> number,
        "profile_id" -> number,
        "created_by_user" -> optional(number)
      )(AccountFormInput.apply)(AccountFormInput.unapply)
    )
  }

  def getAccountsById(id: Long): Action[AnyContent] = {
    action.async { implicit request =>
      accountHandler.findById(id).map { s => Ok(Json.toJson(s)) }
    }
  }

  def getAccountsByName(name: String): Action[AnyContent] = {
    action.async { implicit request =>
      accountHandler.findByName(name).map { s => Ok(Json.toJson(s)) }
    }
  }

//  def createAccount(name: String, rank: Int, url: String, score: Double): Action[AnyContent] = {
//    action.async { implicit request =>
//      accountHandler.create(name, rank, url, score).map { s => Ok(Json.toJson(s)) }
//    }
//  }

  def deleteAccountsForIds(ids: Seq[Long]): Action[AnyContent] = {
    action.async { implicit request =>
      accountHandler.deleteForIds(ids).map { s => Ok(Json.toJson(s)) }
    }
  }

//  def getAccountByName2(name: String): Action[AnyContent] = {
//    action.async { implicit request =>
//      accountHandler.findByName(name).flatMap {
//        case Some(account) => {
//          accountsActor ! ReadAccounts(name)
//          Future.successful(Ok(Json.toJson(account)))
//        }
//        case None =>
//          Future.successful(NotFound(s"No account for $accountId"))
//      }
//    }
//  }

}
