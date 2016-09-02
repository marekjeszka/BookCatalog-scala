package com.marekjeszka.services

import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.marekjeszka.model.BookEntity
import de.heikoseeberger.akkahttpcirce.CirceSupport
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{BeforeAndAfter, Matchers, WordSpec}
import slick.driver.H2Driver.api._

import scala.concurrent.Future
import scala.util.Random

class H2DatabaseServiceSpec extends WordSpec with Matchers with ScalatestRouteTest with CirceSupport with ScalaFutures with BeforeAndAfter {
  implicit override val patienceConfig = PatienceConfig(timeout = Span(5, Seconds))

  val h2DB: H2DatabaseService = new H2DatabaseService
  var db: Database = _

  before {
    db = Database.forConfig("h2mem1")
    h2DB.createSchema(db)
  }

  after {
    db.shutdown.futureValue
  }

  "H2 database" should {
    "create schema" in {
      val tables = h2DB.getTables

      tables.futureValue.size shouldBe 1
    }
  }

  it should {
    "query a book by title" in {
      val newBook: BookEntity = BookEntity(Some(1L), "0076092039389", "Thinking in Java (4th Edition)")
      h2DB.insertBook(newBook).futureValue

      val book: Future[Option[BookEntity]] = h2DB.getBookByTitleLike("%Thinking%")

      book.futureValue.get shouldBe newBook
    }
  }

  it should {
    "query inserted book by id" in {
      val newBook: BookEntity = BookEntity(Some(1L), "0076092039389", "Thinking in Java (4th Edition)")
      h2DB.insertBook(newBook).futureValue

      val book: Future[Option[BookEntity]] = h2DB.getBookById(1L)

      book.futureValue.get shouldBe newBook
    }
  }

  it should {
    "query all books" in {
      val count = 5
      (1 to count).map { _ =>
        BookEntity(Some(Random.nextLong()), Random.nextString(10), Random.nextString(10))
      }.map(h2DB.insertBook)

      h2DB.getBooks.futureValue.size shouldBe 7 // should be 5
    }
  }
}
