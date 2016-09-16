package com.marekjeszka.routes

import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.marekjeszka.model.{BookEntity, BookEntityUpdate}
import com.marekjeszka.services.BooksService
import de.heikoseeberger.akkahttpcirce.CirceSupport
import io.circe.generic.auto._
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.Future

class BooksRouterSpec extends WordSpec
  with Matchers
  with ScalatestRouteTest
  with MockFactory
  with CirceSupport
  with ScalaFutures {

  trait Context {
    class NoArgsBookService extends BooksService(null)
    val booksService = mock[NoArgsBookService]
    val routes = new BooksServiceRoutes(booksService).route
    val aBook = BookEntity(Some(1), "0076092039389", "Thinking in Java (4th Edition)")
    val anotherBook = BookEntity(Some(2), "8601400000663", "Effective Java (2nd Edition)")
    val futureBook = Future.successful(Option(aBook))
  }

  "BooksService routes" should {
    "return all books" in new Context {
      (booksService.getBooks _).expects().returning(Future(IndexedSeq.newBuilder.+=(aBook, anotherBook).result()))

      Get("/books") ~> routes ~> check {
        val books: Seq[BookEntity] = responseAs[Seq[BookEntity]]
        books should contain only (aBook, anotherBook)
      }
    }

    "return one book" in new Context  {
      (booksService.getBookById _).expects(101L).returning(futureBook)

      Get("/books/101") ~> routes ~> check {
        responseAs[BookEntity] should be(futureBook.futureValue.get)
      }
    }

    "insert one book" in new Context {
      (booksService.insertBook _).expects(aBook).returning(Future.successful(1))

      Put("/books", aBook) ~> routes ~> check {
        responseAs[Int] should be(1)
      }
    }

    "update one book" in new Context {
      private val newIsbn: String = "1234"
      private val updatedBook: BookEntity = BookEntity(aBook.id, newIsbn, aBook.title)

      (booksService.updateBook _)
        .expects(1L, BookEntityUpdate(Some(newIsbn), Some(aBook.title)))
        .returning(Future.successful(Some(updatedBook)))

      Post("/books/1", updatedBook) ~> routes ~> check {
        responseAs[BookEntity] should be(updatedBook)
      }
    }
  }
}
