package com.bundlepricing.repos

import com.bundlepricing.domains.Bundle

trait BundleRepoComponent {
  def bundleRepo: BundleRepo

  trait BundleRepo extends RepoMetaData with Repository {
    type Id = String
    type Entity = Bundle
    
    def id(entity: Bundle): Id = entity.key
  }
}
