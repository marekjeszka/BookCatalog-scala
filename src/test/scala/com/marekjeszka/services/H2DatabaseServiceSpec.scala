package com.marekjeszka.services

import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.marekjeszka.model.{BookEntity, CategoryEntity, FileEntity}
import de.heikoseeberger.akkahttpcirce.CirceSupport
import org.h2.jdbc.JdbcSQLException
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{BeforeAndAfter, Matchers, WordSpec}
import slick.driver.H2Driver.api._

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{Await, Future}
import scala.util.Random

class H2DatabaseServiceSpec extends WordSpec with Matchers with ScalatestRouteTest with CirceSupport with ScalaFutures with BeforeAndAfter {
  implicit override val patienceConfig = PatienceConfig(timeout = Span(5, Seconds))

  val h2DB: H2DatabaseService = new H2DatabaseService

  before {
    h2DB.db = Database.forConfig("h2mem1")
    h2DB.createSchema().futureValue
  }

  after {
    h2DB.db.shutdown.futureValue
  }

  "H2 database" should {
    "create schema" in {
      val tables = h2DB.getTables

      tables.futureValue.size shouldBe 4
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
      }.map(h2DB.insertBook).foreach(r => r.futureValue)

      h2DB.getBooks.futureValue.size shouldBe 5
    }
  }

  it should {
    "query all files" in {
      h2DB.insertBook(BookEntity(Some(1L), Random.nextString(10), Random.nextString(10))).futureValue
      val count = 2
      (1 to count).map { _ =>
        FileEntity(Some(Random.nextLong()), Random.nextString(10), 1L)
      }.map(f => h2DB.insertFile(f))

      h2DB.getFiles.futureValue.size shouldBe 2
    }
  }

  it should {
    "throw exception when inserting file without a reference book" in {
      val thrown = intercept[JdbcSQLException] {
        Await.result(h2DB.insertFile(FileEntity(Some(1L), Random.nextString(10), 102L)), FiniteDuration(1, "s"))
      }
      thrown.getMessage should startWith("Referential integrity constraint violation")
    }
  }

  it should {
    "query all categories" in {
      h2DB.insertCategory(CategoryEntity(Some(1L), Random.nextString(10))).futureValue

      h2DB.getCategories.futureValue.size shouldBe 1
    }
  }

  it should {
    "query mappings between books and categories" in {
      val cat1: CategoryEntity = CategoryEntity(Some(1L), Random.nextString(10))
      val cat2: CategoryEntity = CategoryEntity(Some(2L), Random.nextString(10))
      h2DB.insertCategory(cat1).futureValue
      h2DB.insertCategory(cat2).futureValue
      val book: BookEntity = BookEntity(Some(1L), Random.nextString(10), Random.nextString(10))
      h2DB.insertBook(book).futureValue
      h2DB.insertBookCategoryMapping(1L, 1L).futureValue
      h2DB.insertBookCategoryMapping(1L, 2L).futureValue

      val categories = h2DB.getCategoriesForBook(book).futureValue
      categories should contain only(cat1, cat2)
    }
  }
}
