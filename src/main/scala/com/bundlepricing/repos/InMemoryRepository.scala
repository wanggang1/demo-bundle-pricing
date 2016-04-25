package com.bundlepricing.repos

import scala.concurrent.stm.TMap

trait InMemoryRepository extends Repository {
  self : RepoMetaData =>
    
  protected val map = TMap.empty[Id, Entity]

  def get(id: Id): Option[Entity] = map.single.get(id)
  def getAll: Map[Id, Entity] = map.single.toMap
  def insert(entity: Entity): Unit = map.single.update(id(entity), entity)
}