package com.marekjeszka

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.marekjeszka.services.{BooksService, H2DatabaseService}

import scala.concurrent.duration._

object Boot extends App {
  implicit val system = ActorSystem("book-catalog-scala")
  implicit val ec = system.dispatcher
  implicit val log = Logging(system, getClass)
  implicit val materializer = ActorMaterializer()


  val databaseService = new H2DatabaseService()
  val booksService = new BooksService(databaseService)
  val restController = new RestController(booksService)

  implicit val timeout = Timeout(5.seconds)

  Http().bindAndHandle(restController.routes, "localhost", 8080)
}
