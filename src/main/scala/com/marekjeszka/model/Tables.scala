package com.marekjeszka.model

import slick.driver.H2Driver.api._

class Book(tag: Tag)
  extends Table[BookEntity](tag, "BOOK") {

  def id = column[Option[Long]]("BOOK_ID", O.PrimaryKey, O.AutoInc)
  def isbn = column[String]("ISBN")
  def title = column[String]("TITLE")

  def * = (id, isbn, title) <> ((BookEntity.apply _).tupled, BookEntity.unapply)
}

//class Filename(tag: Tag)
//  extends Table[(Long, String, Long)](tag, "FILENAME") {
//
//  def id: Rep[Long] = column[Long]("FILENAME_ID", O.PrimaryKey)
//  def filename: Rep[String] = column[String]("FILENAME")
//  def bookId: Rep[Long] = column[Long]("BOOK_ID")
//
//  def * : ProvenShape[(Long, String, Long)] =
//    (id, filename, bookId)
//
//  def book: ForeignKeyQuery[Book, BookEntity] =
//    foreignKey("BOOK_FK", bookId, TableQuery[Book])(_.id)
//}