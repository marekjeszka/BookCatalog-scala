package com.marekjeszka.model

import slick.driver.H2Driver.api._
import slick.lifted.ForeignKeyQuery

object Tables {
  class Book(tag: Tag)
    extends Table[BookEntity](tag, "BOOK") {

    def id = column[Option[Long]]("BOOK_ID", O.PrimaryKey, O.AutoInc)
    def isbn = column[String]("ISBN")
    def title = column[String]("TITLE")

    def * = (id, isbn, title) <> ((BookEntity.apply _).tupled, BookEntity.unapply)

    def categories = bookToCategory.filter(_.bookId === id).flatMap(_.categoriesFK)
  }
  lazy val books = TableQuery[Book]

  class File(tag: Tag)
    extends Table[FileEntity](tag, "FILE") {

    def id = column[Option[Long]]("FILENAME_ID", O.PrimaryKey, O.AutoInc)
    def name = column[String]("NAME")
    def bookId = column[Long]("BOOK_ID")

    def * = (id, name, bookId) <> ((FileEntity.apply _).tupled, FileEntity.unapply)

    def book: ForeignKeyQuery[Book, BookEntity] =
      foreignKey("FILE_TO_BOOK_FK", bookId, TableQuery[Book])(_.id.get)
  }
  lazy val files = TableQuery[File]

  class Category(tag: Tag)
    extends Table[CategoryEntity](tag, "CATEGORY") {

    def id = column[Option[Long]]("CATEGORY_ID", O.PrimaryKey, O.AutoInc)
    def name = column[String]("NAME")

    def * = (id, name) <> ((CategoryEntity.apply _).tupled, CategoryEntity.unapply)

    def books = bookToCategory.filter(_.categoryId === id).flatMap(_.bookFK)
  }
  lazy val categories = TableQuery[Category]

  class BookToCategory(tag: Tag)
    extends Table[(Long, Long)](tag, "BOOK_CATEGORY") {

    def bookId = column[Long]("BOOK_ID")
    def categoryId = column[Long]("CATEGORY_ID")

    def * = (bookId, categoryId)

    def bookFK = foreignKey("BOOK_TO_CATEGORY_FK", bookId, books)(book => book.id.get)
    def categoriesFK = foreignKey("CATEGORY_TO_BOOK_FK", categoryId, categories)(category => category.id.get)
  }
  lazy val bookToCategory = TableQuery[BookToCategory]
}