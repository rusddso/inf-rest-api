package com.infinera.controllers

import java.nio.file.{Files, Paths}
import javax.inject.Inject

import com.infinera.accounts.DBImplicits
import play.api.Configuration
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Controller}
import play.db.NamedDatabase
import slick.dbio.DBIO
import slick.jdbc.MySQLProfile.api._
import slick.relational.RelationalProfile

import scala.concurrent.ExecutionContext

class HealthCheckController @Inject()(configuration: Configuration,
                                      dbExecuter: DBImplicits,
                                      @NamedDatabase("infinera") dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends Controller {

  val configDir = configuration.getString("config.dir").getOrElse("none")

  val postgresProfile = dbConfigProvider.get[RelationalProfile]

  def index(): Action[AnyContent] = {
    Action.async { implicit request =>
      val selectOne: DBIO[Seq[(Int)]] = sql"""select 1""".as[(Int)]

      val mysqlStatus = dbExecuter.executeOperation(selectOne).map { seq =>
        ("mysql", if (seq.headOption.getOrElse(0) == 1) "alive" else "dead")
      }

      val postgresStatus = postgresProfile.db.run(selectOne).map { seq =>
        ("postgres", if (seq.headOption.getOrElse(0) == 1) "alive" else "dead")
      }

      val aggFut = for {
        f1Result <- mysqlStatus
        f2Result <- postgresStatus
      } yield List(f1Result,
                   f2Result)

      aggFut.map { resultTuples =>
        val result = resultTuples.toMap
        if (result.getOrElse("mysql", "missing").equals("alive") &&
          result.getOrElse("postgres", "missing").equals("alive")
          ) {
          Ok(Json.toJson(result))
        } else {
          InternalServerError(Json.toJson(result))
        }
      }
    }
  }
}
