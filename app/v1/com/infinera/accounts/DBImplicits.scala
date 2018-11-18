package com.infinera.accounts

import javax.inject.Inject

import com.byteslounge.slickrepo.scalaversion.JdbcProfile
import play.api.db.slick.DatabaseConfigProvider
import play.db.NamedDatabase
import slick.dbio.DBIO

import scala.concurrent.Future
import scala.language.implicitConversions

class DBImplicits @Inject()(dbConfigProvider: DatabaseConfigProvider) {
  implicit def executeOperation[T](databaseOperation: DBIO[T]): Future[T] = {
    dbConfigProvider.get[JdbcProfile].db.run(databaseOperation)
  }
}

class PostgresDBImplicits @Inject()(@NamedDatabase("infinera") dbConfigProvider: DatabaseConfigProvider) {
  implicit def executeOperation[T](databaseOperation: DBIO[T]): Future[T] = {
    dbConfigProvider.get[JdbcProfile].db.run(databaseOperation)
  }
}