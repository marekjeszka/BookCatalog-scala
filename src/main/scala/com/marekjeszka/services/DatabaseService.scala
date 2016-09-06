package com.marekjeszka.services

import com.marekjeszka.model.{BookEntity, CategoryEntity, FileEntity, Tables}
import slick.driver.H2Driver.api._
import slick.jdbc.meta.MTable

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait DatabaseService {

  def createSchema(): Future[Unit]

  def getTables: Future[Seq[MTable]]
  def getBooks: Future[Seq[BookEntity]]
  def getBookById(id: Long): Future[Option[BookEntity]]
  def getBookByTitleLike(title: String): Future[Option[BookEntity]]
  def insertBook(book: BookEntity): Future[Int]

  def getFiles: Future[Seq[FileEntity]]
  def insertFile(file: FileEntity): Future[Int]

  def getCategories: Future[Seq[CategoryEntity]]
  def insertCategory(category: CategoryEntity): Future[Int]
  def insertBookCategoryMapping(bookId: Long, categoryId: Long): Future[Int]
  def getCategoriesForBook(book: BookEntity): Future[Seq[CategoryEntity]]
}

class H2DatabaseService() extends DatabaseService {
  var db: Database = _

  def createSchema(): Future[Unit] =
    db.run((Tables.books.schema ++ Tables.files.schema ++ Tables.categories.schema ++ Tables.bookToCategory.schema).create)

  override def getTables: Future[Seq[MTable]] = db.run(MTable.getTables)

  override def getBooks: Future[Seq[BookEntity]] = db.run(Tables.books.result)

  override def getBookById(id: Long): Future[Option[BookEntity]] =
    db.run(Tables.books.filter(_.id === id).result.headOption)

  override def getBookByTitleLike(title: String): Future[Option[BookEntity]] =
    db.run(Tables.books.filter(_.title like title).result.headOption)

  override def insertBook(book: BookEntity): Future[Int] = db.run(Tables.books += book)

  override def getFiles: Future[Seq[FileEntity]] = db.run(Tables.files.result)

  override def insertFile(file: FileEntity): Future[Int] = db.run(Tables.files += file)

  override def getCategories: Future[Seq[CategoryEntity]] = db.run(Tables.categories.result)

  override def insertCategory(category: CategoryEntity): Future[Int] = db.run(Tables.categories += category)

  override def insertBookCategoryMapping(bookId: Long, categoryId: Long): Future[Int] =
    db.run(Tables.bookToCategory += (bookId, categoryId))

  override def getCategoriesForBook(book: BookEntity) = {
    val categoriesQuery = Tables.books.filter(_.id === book.id)
      .join(Tables.bookToCategory).on(_.id === _.bookId)
      .join(Tables.categories).on(_._2.categoryId === _.id)
      .result

    val action = for {
      categoriesResult <- categoriesQuery
    } yield {
      categoriesResult.map(_._2)
    }
    db.run(action)
  }
}
