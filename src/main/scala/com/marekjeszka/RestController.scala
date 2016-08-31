package com.marekjeszka

import com.marekjeszka.routes.BooksServiceRoutes
import com.marekjeszka.services.BooksService

import scala.concurrent.ExecutionContext

class RestController(val booksService: BooksService)(implicit executionContext: ExecutionContext) {
  val booksRouter = new BooksServiceRoutes(booksService)

  val routes =
    booksRouter.route
}