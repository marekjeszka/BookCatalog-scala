package com.marekjeszka

import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.marekjeszka.model.BookEntity
import com.marekjeszka.routes.BooksServiceRoutes
import com.marekjeszka.services.BooksService
import de.heikoseeberger.akkahttpcirce.CirceSupport
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}
import io.circe.generic.auto._

class BooksRouterSpec extends WordSpec
  with Matchers
  with ScalatestRouteTest
  with CirceSupport
  with ScalaFutures {

  implicit val ec = system.dispatcher

  val routes = new BooksServiceRoutes(new BooksService()).route

  "BooksService routes" should {
    "return all books" in {
      Get("/books") ~> routes ~> check {
        responseAs[Seq[String]].isEmpty should be(true)
      }
    }
  }

  val testBook = BookEntity(isbn = "0785342336788", title = "Java Puzzlers: Traps, Pitfalls, and Corner Cases")

  it should {
    "return one book" in {
      Get("/books/101") ~> routes ~> check {
        responseAs[BookEntity] should be(testBook)
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
