package com.bundlepricing.repos

import com.mongodb.casbah.MongoClient
import com.novus.salat.Context
import org.bson.types.ObjectId

import com.bundlepricing.domains.Item

trait ItemRepoComponent {
  def itemRepo: ItemRepo

  abstract class ItemRepo(val dbName: String = "", val collectionName: String = "")
                         (implicit val context: Context,
                             val mongoClient: MongoClient,
                             val idManifest: Manifest[ObjectId],
                             val entityManifest: Manifest[Item])
                  extends RepoMetaData with Repository {
    type Id = ObjectId
    type Key = String
    type Entity = Item
    
    val keyField = "name"
    def key(entity: Item): Key = entity.name
  }
}
