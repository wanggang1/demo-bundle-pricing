package com.bundlepricing.repos

import com.mongodb.casbah.MongoClient
import com.novus.salat.Context
import org.bson.types.ObjectId

import com.bundlepricing.domains.Item

trait ItemRepoComponent {
  def itemRepo: ItemRepo
  
  //ItemRepo traits moved out of component to enable DI to actors with implicit
}

/**
 * generic, for in-memory repo
 */
trait ItemRepo extends RepoMetaData with Repository {
  type Key = String
  type Entity = Item
                           
  val keyField = "name"
  def key(entity: Item): Key = entity.name
}

/**
 * for MongoDB
 * 
 * the implementation (SalatRepository) can be mixin at creation time
 */
abstract class ItemMongoRepo(val dbName: String = "", val collectionName: String = "")
                       (implicit val context: Context, val mongoClient: MongoClient)
                extends SalatRepoMataData with ItemRepo {
  type Id = ObjectId
  
  val idManifest = implicitly[Manifest[Id]]
  val entityManifest = implicitly[Manifest[Entity]]
}