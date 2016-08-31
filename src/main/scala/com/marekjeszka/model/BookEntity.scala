package com.marekjeszka.model

case class BookEntity(id: Option[Long] = None, isbn: String, title: String) {
  require(!isbn.isEmpty, "isbn.empty")
  require(!title.isEmpty, "title.empty")
}

case class BookEntityUpdate(isbn: Option[String] = None, title: Option[String] = None) {
  def merge(book: BookEntity): BookEntity = {
    BookEntity(book.id, isbn.getOrElse(book.isbn), title.getOrElse(book.title))
  }
}
