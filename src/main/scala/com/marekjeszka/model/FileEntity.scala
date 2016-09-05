package com.marekjeszka.model

case class FileEntity(id: Option[Long] = None, name: String, bookId: Long) {
  require(!name.isEmpty, "name.empty")
}

case class FileEntityUpdate(name: Option[String], bookId: Option[Long]) {
  def merge(file: FileEntity): FileEntity = {
    FileEntity(file.id, name.getOrElse(file.name), bookId.getOrElse(file.bookId))
  }
}
