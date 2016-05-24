package com.bundlepricing.repos

import com.mongodb.WriteResult
import com.mongodb.casbah.Imports._
import com.novus.salat.dao.SalatDAO
import com.novus.salat.{ Context, grater }

import scala.reflect.runtime.universe.typeOf

/**
 * Mongo implementation of Repository with Salat library
 */
trait SalatRepository extends Repository {
  self : SalatRepoMataData =>

  lazy protected val salatDao = new SalatDAO[Entity, Id](mongoClient(dbName)(collectionName)) {}

  def get(id: Id): Option[Entity] = {
    //TODO: Salat decided to disable "ById" querying on compound ids to "protect" users who don't read the docs
    //TODO: When they fix it refactor out the boilerplate from this trait
    //TODO: See https://github.com/salat/salat/issues/110 and https://github.com/salat/salat/issues/86
    //    salatDao.findOneById(id)
    
    salatDao.findOne(idToDBObject(id))
  }
  
  def getByKey(key: Key): Option[Entity] =
    salatDao.findOne(DBObject(keyField -> key))

  def getAll: Map[Key, Entity] =
    salatDao.find(MongoDBObject.empty).toList.map(e => key(e) -> e)(scala.collection.breakOut)

  def insert(entity: Entity): Unit =
    salatDao.insert(entity)

  private type CaseClass = AnyRef with Product

  def idToDBObject(id: Id): DBObject =
    if (typeOf[Id] <:< typeOf[CaseClass]) DBObject("_id" -> grater[Id].asDBObject(id)) else DBObject("_id" -> id)

}

trait SalatRepoMataData extends RepoMetaData {
  type Id <: AnyRef //Entity's unique identifier
  
  def dbName: String
  def collectionName: String

  implicit def mongoClient: MongoClient
  implicit def context: Context
  implicit def idManifest: Manifest[Id]
  implicit def entityManifest: Manifest[Entity]
}
