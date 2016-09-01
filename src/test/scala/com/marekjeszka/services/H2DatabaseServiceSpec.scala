package com.marekjeszka.services

import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.marekjeszka.model.BookEntity
import de.heikoseeberger.akkahttpcirce.CirceSupport
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{Matchers, WordSpec}
import slick.driver.H2Driver.api._

import scala.concurrent.Future

class H2DatabaseServiceSpec extends WordSpec with Matchers with ScalatestRouteTest with CirceSupport with ScalaFutures {
  implicit override val patienceConfig = PatienceConfig(timeout = Span(5, Seconds))

  trait DBCtx {
    val h2DB: H2DatabaseService = new H2DatabaseService
    h2DB.createSchema(Database.forConfig("h2mem1"))
  }

  "H2 database" should {
    "create schema" in new DBCtx {
      val tables = h2DB.getTables

      tables.futureValue.size shouldBe 1
    }

    "query a book by title" in new DBCtx {
      val newBook: BookEntity = BookEntity(Option(1L), "0076092039389", "Thinking in Java (4th Edition)")
      h2DB.insertBook(newBook).futureValue

      val book: Future[Option[BookEntity]] = h2DB.getBookByTitleLike("%Thinking%")

      book.futureValue.get shouldBe newBook
    }

    "query inserted book by id" in new DBCtx {
      val newBook: BookEntity = BookEntity(Option(1L), "0076092039389", "Thinking in Java (4th Edition)")
      h2DB.insertBook(newBook).futureValue

      val book: Future[Option[BookEntity]] = h2DB.getBookById(1L)

      book.futureValue.get shouldBe newBook
    }
  }
}
