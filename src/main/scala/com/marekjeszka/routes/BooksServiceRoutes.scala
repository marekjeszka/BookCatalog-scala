package com.marekjeszka.routes

import akka.http.scaladsl.server.Directives._
import com.marekjeszka.model.{BookEntity, BookEntityUpdate}
import com.marekjeszka.services.BooksService
import de.heikoseeberger.akkahttpcirce.CirceSupport
import io.circe.generic.auto._
import io.circe.syntax._

import scala.concurrent.ExecutionContext

class BooksServiceRoutes(val booksService: BooksService)(implicit executionContext: ExecutionContext)
                        extends CirceSupport {
  import booksService._

  val route = pathPrefix("books") {
    pathEndOrSingleSlash {
      put {
        entity(as[BookEntity]) { newBook =>
          complete(insertBook(newBook).map(_.asJson))
        }
      } ~
      get {
        complete(getBooks.map(_.asJson))
      }
    } ~
    pathPrefix(IntNumber) { id =>
      pathEndOrSingleSlash {
        get {
          complete(getBookById(id).map(_.asJson))
        } ~
        post {
          entity(as[BookEntityUpdate]) { bookUpdate =>
            complete(updateBook(id, bookUpdate).map(_.asJson))
          }
        }
      }
    }
  }
}
