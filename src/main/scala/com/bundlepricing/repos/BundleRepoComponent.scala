package com.bundlepricing.repos

import com.mongodb.casbah.MongoClient
import com.novus.salat.Context
import org.bson.types.ObjectId

import com.bundlepricing.domains.Bundle

trait BundleRepoComponent {
  def bundleRepo: BundleRepo

  abstract class BundleRepo(val dbName: String = "", val collectionName: String = "")
                           (implicit val context: Context,
                             val mongoClient: MongoClient,
                             val idManifest: Manifest[ObjectId],
                             val entityManifest: Manifest[Bundle])
                 extends RepoMetaData with Repository {
    type Id = ObjectId
    type Key = String
    type Entity = Bundle
    
    val keyField = "key"
    def key(entity: Bundle): Key = entity.key
  }
}
