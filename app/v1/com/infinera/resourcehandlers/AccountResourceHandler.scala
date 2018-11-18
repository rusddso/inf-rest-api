package com.infinera.resourcehandlers

import javax.inject.Inject

import com.infinera.accounts.PostgresDBImplicits
import com.infinera.repositories.{AccountData, AccountRepository}
import play.api.libs.json._

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

case class AccountFormInput(listDomainId: Int, keyword: String, importance: Int, cookieCount: Int, sourceUrl: String, burstScore: Double, tfIdf: Double)

/**
  * DTO for account_master
  */
case class AccountResource(id: Option[Long],
                           name: String,
                           rank: Int,
                           url: String,
                           score: Double)

object AccountResource {
  /**
    * Mapping to write a AccountResource out as a JSON value.
    */
  implicit val implicitWrites = new Writes[AccountResource] {
    def writes(account: AccountResource): JsValue = {
      Json.obj(
        "id" -> account.id,
        "name" -> account.name,
        "rank" -> account.rank,
        "url" -> account.url,
        "score" -> account.score
      )
    }
  }
}

/**
  * Controls access to the backend data, returning [[AccountResource]]
  */
class AccountResourceHandler @Inject()(accountRepository: AccountRepository,
                                       dbExecutor: PostgresDBImplicits)(implicit ec: ExecutionContext) {

  import dbExecutor.executeOperation

  def findById(id: Long): Future[Seq[AccountResource]] = {
    accountRepository.forId(id).mapTo[Seq[AccountData]].map { s =>
      s.map { md => createAccountResource(md) }
    }
  }

  def findByName(name: String): Future[Seq[AccountResource]] = {
    accountRepository.forName(name).mapTo[Seq[AccountData]].map { s =>
      s.map { md => createAccountResource(md) }
    }
  }

//  def create(name: String, rank: Int, url: String, score: Double): Future[AccountData] = {
//    val data = AccountData(None, name, rank, url, score)
//    accountRepository.save(data)
//    Future.successful(data)
//  }

  def deleteForIds(ids: Seq[Long]): Future[Int] = {
    val countF = accountRepository.deleteForIds(ids)
    val count = Await.result(countF, 5.minutes)
    Future.successful(count)
  }

  def createAccountResource(account: AccountData): AccountResource = {
    AccountResource(account.id, account.name, account.rank, account.url, account.score)
  }

  def createAccountData(account: AccountResource): AccountData = {
    AccountData(account.id, account.name, account.rank, account.url, account.score)
  }

}
