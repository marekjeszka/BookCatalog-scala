package com.marekjeszka.services

import com.marekjeszka.model.{BookEntity, BookEntityUpdate}
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.{ExecutionContext, Future}

class BooksServiceSpec extends WordSpec
  with Matchers
  with MockFactory
  with ScalaFutures {

  val databaseService = mock[DatabaseService]
  val booksService = new BooksService(databaseService)(ExecutionContext.global)
  val aBook = BookEntity(Some(1), "0076092039389", "Thinking in Java (4th Edition)")
  val futureBook = Future.successful(Some(aBook))

  "books service" should {
    "update book" in {
      val newIsbn: String = "1234"
      val newTitle: String = "Writing in Java"
      val updatedBook = BookEntity(aBook.id, newIsbn, newTitle)
      (databaseService.getBookById _).expects(1L).returning(futureBook)
      (databaseService.updateBook _).expects(*, *)
        .returning(Future.successful(Some(updatedBook)))

      booksService.updateBook(updatedBook.id.get, BookEntityUpdate(Some(newIsbn), Some(newTitle)))
        .futureValue.get.shouldBe(updatedBook)
    }
  }

  "books service" should {
    "not update book" in {
      val newIsbn: String = "1234"
      val newTitle: String = "Writing in Java"
      val none: Option[BookEntity] = None: Option[BookEntity]
      (databaseService.getBookById _).expects(1L).returning(Future.successful(none))

      booksService.updateBook(1L, BookEntityUpdate()).futureValue
        .shouldBe(none)
    }
  }
}
