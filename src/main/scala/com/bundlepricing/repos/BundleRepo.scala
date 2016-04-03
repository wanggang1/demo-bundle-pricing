package com.bundlepricing.repos

import com.bundlepricing.domains.Bundle

class BundleRepo extends InMemoryRepository {
  type Id = String
  type Entity = Bundle
  
  def id(entity: Bundle): Id = entity.key
}