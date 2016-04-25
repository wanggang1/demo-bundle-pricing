package com.bundlepricing.repos

trait RepoMetaData {
  type Id <: AnyRef //Entity's unique identifier
  type Entity <: AnyRef // Domain object with an unique identifier

  def id(entity: Entity): Id // extract Id from domain object
}

trait Repository {
  self : RepoMetaData =>
    
  def get(id: Id): Option[Entity]
  def getAll: Map[Id, Entity]
  def insert(entity: Entity): Unit
}
