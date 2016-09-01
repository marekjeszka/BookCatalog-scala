package com.marekjeszka.services

import com.marekjeszka.model.BookEntity

import scala.concurrent.{ExecutionContext, Future}

class BooksService(databaseService: DatabaseService)(implicit executionContext: ExecutionContext) {

  def getBooks: Future[Seq[BookEntity]] = databaseService.getBooks

  def getBookById(id: Long): Future[Option[BookEntity]] = databaseService.getBookById(id)
}
