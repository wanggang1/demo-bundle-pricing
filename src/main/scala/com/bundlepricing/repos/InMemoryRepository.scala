package com.bundlepricing.repos

import scala.concurrent.stm.TMap

/**
 * to be mixin at repo instantiation time
 */
trait InMemoryRepository extends Repository {
  self : RepoMetaData =>
 
  protected val map = TMap.empty[Key, Entity]

  def getByKey(key: Key): Option[Entity] = map.single.get(key)
  def getAll: Map[Key, Entity] = map.single.toMap
  def insert(entity: Entity): Unit = map.single.update(key(entity), entity)
}