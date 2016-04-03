package com.bundlepricing.repos

import scala.concurrent.stm.TMap

trait Repository {
  type Id <: AnyRef //Entity's unique identifier
  type Entity <: AnyRef // Domain object with an unique identifier

  def id(entity: Entity): Id // extract Id from domain object
  def get(id: Id): Option[Entity]
  def getAll: Map[Id, Entity]
  def insert(entity: Entity): Unit
}

trait InMemoryRepository extends Repository {
  protected val map = TMap.empty[Id, Entity]

  def get(id: Id): Option[Entity] = map.single.get(id)
  def getAll: Map[Id, Entity] = map.single.toMap
  def insert(entity: Entity): Unit = map.single.update(id(entity), entity)
}
