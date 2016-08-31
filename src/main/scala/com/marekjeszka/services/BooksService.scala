package com.marekjeszka.services

import com.marekjeszka.model.BookEntity

import scala.concurrent.{ExecutionContext, Future}

class BooksService(implicit executionContext: ExecutionContext) {
  def getBooks: Future[Seq[BookEntity]] = Future {
    IndexedSeq.newBuilder.+=(BookEntity(None, "", "")).result()
  }

  def getBookById(id: Long): Future[BookEntity] = Future {
    BookEntity(isbn = "0076092039389", title = "Thinking in Java (4th Edition)")
  }
}
