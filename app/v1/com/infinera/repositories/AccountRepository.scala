package com.infinera.repositories

import javax.inject.Inject

import com.byteslounge.slickrepo.meta.{Entity, Keyed}
import com.byteslounge.slickrepo.repository.Repository
import com.byteslounge.slickrepo.scalaversion.JdbcProfile
import play.api.db.slick.DatabaseConfigProvider
import play.db.NamedDatabase
import slick.ast.BaseTypedType

import scala.concurrent.ExecutionContext

class AccountId private(val underlying: Long) extends AnyVal {
  override def toString: String = underlying.toString
}

object AccountId {
  def apply(raw: String): AccountId = {
    require(raw != null)
    new AccountId(raw.toLong)
  }
}

case class AccountData(override val id: Option[Long],
                                    name: String,
                                    rank: Int,
                                    url: String,
                                    score: Double) extends Entity[AccountData, Long] {
  def withId(id: Long): AccountData = this.copy(id = Some(id))
}

class AccountRepository @Inject()(@NamedDatabase("infinera") dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends Repository[AccountData, Long](dbConfigProvider.get[JdbcProfile].profile) {

  import driver.api._

  val pkType = implicitly[BaseTypedType[Long]]
  val tableQuery = TableQuery[Accounts]
  type TableType = Accounts

  class Accounts(tag: slick.lifted.Tag) extends Table[AccountData](tag, "account_master") with Keyed[Long] {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def rank = column[Int]("rank")

    def url = column[String]("url")

    def score = column[Double]("score")

    def * = (id.?, name, rank, url, score) <> ((AccountData.apply _).tupled, AccountData.unapply)
  }

  def forId(id: Long) = {
    tableQuery.filter(_.id === id).result
  }

  def forName(name: String) = {
    tableQuery.filter(_.name === name).result
  }

  def deleteForIds(ids: Seq[Long]) = {
    dbConfigProvider.get[JdbcProfile].db.run(tableQuery.filter(_.id inSet ids).delete)
  }

}
