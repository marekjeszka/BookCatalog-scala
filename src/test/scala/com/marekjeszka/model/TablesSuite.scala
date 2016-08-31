package com.marekjeszka.model


import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import slick.lifted.TableQuery
import slick.driver.H2Driver.api._
import slick.jdbc.meta._

class TablesSuite extends FunSuite with BeforeAndAfter with ScalaFutures {
  implicit override val patienceConfig = PatienceConfig(timeout = Span(5, Seconds))

  val books = TableQuery[Book]
  val filenames = TableQuery[Filename]

  var db: Database = _

  def createSchema() = db.run((books.schema ++ filenames.schema).create).futureValue

  def insertBook() = db.run(books += (101,"0076092039389","Thinking in Java (4th Edition)")).futureValue

  before { db = Database.forConfig("h2mem1") }

  after { db.close() }

  test("Creating the Schema works") {
    createSchema()

    val tables = db.run(MTable.getTables).futureValue

    assert(tables.size == 2)
    assert(tables.count(_.name.name.equalsIgnoreCase("book")) == 1)
    assert(tables.count(_.name.name.equalsIgnoreCase("filename")) == 1)
  }

  test("Inserting a Book works") {
    createSchema()

    val insertCount = insertBook()

    assert(insertCount == 1)
  }

  test("Query Book works") {
    createSchema()
    insertBook()

    val results = db.run(books.result).futureValue
    assert(results.size == 1)
    assert(results.head._1 == 101)
  }
}