package com.marekjeszka.model

import slick.driver.H2Driver.api._
import slick.lifted.ForeignKeyQuery

class Book(tag: Tag)
  extends Table[BookEntity](tag, "BOOK") {

  def id = column[Option[Long]]("BOOK_ID", O.PrimaryKey, O.AutoInc)
  def isbn = column[String]("ISBN")
  def title = column[String]("TITLE")

  def * = (id, isbn, title) <> ((BookEntity.apply _).tupled, BookEntity.unapply)
}

class File(tag: Tag)
  extends Table[FileEntity](tag, "FILE") {

  def id = column[Option[Long]]("FILENAME_ID", O.PrimaryKey, O.AutoInc)
  def name = column[String]("NAME")
  def bookId = column[Long]("BOOK_ID")

  def * = (id, name, bookId) <> ((FileEntity.apply _).tupled, FileEntity.unapply)

  def book: ForeignKeyQuery[Book, BookEntity] =
    foreignKey("BOOK_FK", bookId, TableQuery[Book])(_.id.get)
}