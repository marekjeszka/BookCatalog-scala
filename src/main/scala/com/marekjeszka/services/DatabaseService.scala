package com.marekjeszka.services

import com.marekjeszka.model.{Book, BookEntity}
import slick.driver.H2Driver.api._
import slick.jdbc.meta.MTable
import slick.lifted.TableQuery

import scala.concurrent.Future

trait DatabaseService {


  val books = TableQuery[Book]
//  val filenames = TableQuery[Filename]

  def createSchema(db: Database) = {
    db.run((books.schema).create)
//    db.run((books.schema ++ filenames.schema).create)
  }

  def getTables: Future[Seq[MTable]]
  def getBooks: Future[Seq[BookEntity]]
  def getBookById(id: Long): Future[Option[BookEntity]]
  def getBookByTitleLike(title: String): Future[Option[BookEntity]]
  def insertBook(book: BookEntity): Future[Int]
}

class H2DatabaseService extends DatabaseService {
  val db = Database.forConfig("h2mem1")

  createSchema(db)

  override def getTables: Future[Seq[MTable]] = db.run(MTable.getTables)

  override def getBooks: Future[Seq[BookEntity]] = db.run(books.result)

  override def getBookById(id: Long): Future[Option[BookEntity]] =
    db.run(books.filter(_.id === id).result.headOption)

  override def getBookByTitleLike(title: String): Future[Option[BookEntity]] =
    db.run(books.filter(_.title like title).result.headOption)

  override def insertBook(book: BookEntity): Future[Int] = db.run(books += book)
}
