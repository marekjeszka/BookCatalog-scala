package com.marekjeszka.services

import com.marekjeszka.model.{BookEntity, BookEntityUpdate}

import scala.concurrent.{ExecutionContext, Future}

class BooksService(databaseService: DatabaseService)(implicit executionContext: ExecutionContext) {

  def getBooks: Future[Seq[BookEntity]] = databaseService.getBooks

  def getBookById(id: Long): Future[Option[BookEntity]] = databaseService.getBookById(id)

  def updateBook(id: Long, bookUpdate: BookEntityUpdate): Future[Option[BookEntity]] = getBookById(id).flatMap {
    case Some(book) =>
      val updatedBook = bookUpdate.merge(book)
      databaseService.updateBook(id, updatedBook)
    case None => Future.successful(None)
  }

  def insertBook(newBook: BookEntity): Future[Int] = databaseService.insertBook(newBook)
}
