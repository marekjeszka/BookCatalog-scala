package com.marekjeszka.routes

import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.marekjeszka.model.BookEntity
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
    val testBook = Future.successful {
      Option(BookEntity(isbn = "0076092039389", title = "Thinking in Java (4th Edition)"))
    }
  }

  "BooksService routes" should {
    "return all books" in new Context {
      Get("/books") ~> routes ~> check {
        responseAs[Seq[String]].isEmpty should be(true)
      }
    }
  }


  it should {
    "return one book" in new Context  {
      (booksService.getBookById _).expects(101L).returning(testBook)

      Get("/books/101") ~> routes ~> check {
        responseAs[BookEntity] should be(testBook.futureValue.get)
      }
    }
  }

  //  it should {
  //    "return a greeting for GET requests to the root path" in {
  //      Get("/") ~> routes ~> check {
  //        responseAs[String] should include("Say hello")
  //      }
  //    }
  //  }
  //
  //  it should {
  //    "leave GET requests to other paths unhandled" in {
  //      Get("/kermit") ~> routes ~> check {
  //        handled shouldEqual false
  //      }
  //    }
  //  }
}
