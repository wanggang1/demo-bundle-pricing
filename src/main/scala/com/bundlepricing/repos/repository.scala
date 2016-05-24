package com.bundlepricing.repos

trait RepoMetaData {
  type Entity <: AnyRef // Domain object with an unique identifier
  type Key <: AnyRef
  
  def key(entity: Entity): Key // extract key from domain object
  def keyField: String
}

trait Repository {
  self : RepoMetaData =>

  def getByKey(key: Key): Option[Entity]
  def getAll: Map[Key, Entity]
  def insert(entity: Entity): Unit
}

