package com.marekjeszka.model

case class CategoryEntity(id: Option[Long], name: String) {
  require(!name.isEmpty, "name.empty")
}

//case class CategoryEntityUpdate(name: Option[String], books: Option[Seq[Long]]) {
//  def merge(category: CategoryEntity): CategoryEntity = {
//    CategoryEntity(category.id, name.getOrElse(category.name), books.getOrElse(category.books))
//  }
//}
