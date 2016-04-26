package com.bundlepricing.repos

import com.mongodb.WriteResult
import com.mongodb.casbah.Imports._
import com.novus.salat.dao.SalatDAO
import com.novus.salat.{ Context, grater }

import scala.reflect.runtime.universe.typeOf

trait SalatRepository extends Repository {
  self : RepoMetaData =>
    
  protected def mongoClient: MongoClient
  protected def dbName: String
  protected def collectionName: String
  
  implicit protected def context: Context
  implicit protected def idManifest: Manifest[Id]
  implicit protected def entityManifest: Manifest[Entity]

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