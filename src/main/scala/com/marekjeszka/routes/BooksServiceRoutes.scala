package com.marekjeszka.routes

import akka.http.scaladsl.server.Directives._
import com.marekjeszka.services.BooksService
import de.heikoseeberger.akkahttpcirce.CirceSupport
import io.circe.generic.auto._
import io.circe.syntax._

import scala.concurrent.{ExecutionContext, Future}

class BooksServiceRoutes(val booksService: BooksService)(implicit executionContext: ExecutionContext)
                        extends CirceSupport {
  import booksService._

  val seq: Future[Seq[String]] = Future {
    IndexedSeq.newBuilder.result()
  }

  val route = pathPrefix("books") {
    pathEndOrSingleSlash {
      get {
        complete(seq.map(_.asJson))
      }
    } ~
    pathPrefix(IntNumber) { id =>
      pathEndOrSingleSlash {
        get {
          complete(getBookById(id).map(_.asJson))
        }
      }
    }
  }
}
