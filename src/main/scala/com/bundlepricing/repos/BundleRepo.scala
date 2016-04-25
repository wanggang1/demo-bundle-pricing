package com.bundlepricing.repos

import com.bundlepricing.domains.Bundle

class BundleRepo extends RepoMetaData with InMemoryRepository {
  type Id = String
  type Entity = Bundle
  
  def id(entity: Bundle): Id = entity.key
}