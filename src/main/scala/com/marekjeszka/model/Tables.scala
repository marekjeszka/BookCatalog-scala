package com.marekjeszka.model

import slick.driver.H2Driver.api._
import slick.lifted.{ForeignKeyQuery, ProvenShape}

class Book(tag: Tag)
  extends Table[(Long, String, String)](tag, "BOOK") {

  def id: Rep[Long] = column[Long]("BOOK_ID", O.PrimaryKey)
  def isbn: Rep[String] = column[String]("ISBN")
  def title: Rep[String] = column[String]("TITLE")

  def * : ProvenShape[(Long, String, String)] =
    (id, isbn, title)
}

class Filename(tag: Tag)
  extends Table[(Long, String, Long)](tag, "FILENAME") {

  def id: Rep[Long] = column[Long]("FILENAME_ID", O.PrimaryKey)
  def filename: Rep[String] = column[String]("FILENAME")
  def bookId: Rep[Long] = column[Long]("BOOK_ID")

  def * : ProvenShape[(Long, String, Long)] =
    (id, filename, bookId)

  def book: ForeignKeyQuery[Book, (Long, String, String)] =
    foreignKey("BOOK_FK", bookId, TableQuery[Book])(_.id)
}