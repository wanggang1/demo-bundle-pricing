package com.bundlepricing.repos

import scala.concurrent.stm.TMap

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

trait InMemoryRepository extends Repository {
  self : RepoMetaData =>
 
  protected val map = TMap.empty[Key, Entity]

  def getByKey(key: Key): Option[Entity] = map.single.get(key)
  def getAll: Map[Key, Entity] = map.single.toMap
  def insert(entity: Entity): Unit = map.single.update(key(entity), entity)
}
