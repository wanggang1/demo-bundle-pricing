package com.bundlepricing.repos

import com.mongodb.casbah.MongoClient
import com.novus.salat.Context
import org.bson.types.ObjectId

import com.bundlepricing.domains.Bundle

trait BundleRepoComponent {
  
  def bundleRepo: BundleRepo

  trait BundleRepo extends RepoMetaData with Repository {
    type Key = String
    type Entity = Bundle

    val keyField = "key"
    def key(entity: Bundle): Key = entity.key
  }
  
  abstract class BundleMongoRepo(val dbName: String = "", val collectionName: String = "")
                                (implicit val context: Context, val mongoClient: MongoClient)
                 extends SalatRepoMataData with BundleRepo {
    type Id = ObjectId
    
    val idManifest = implicitly[Manifest[Id]]
    val entityManifest = implicitly[Manifest[Entity]]
  }
  
}
