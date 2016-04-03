package com.bundlepricing.repos

import com.bundlepricing.domains.Item

class ItemRepo extends InMemoryRepository {
  type Id = String
  type Entity = Item
  
  def id(entity: Item): Id = entity.name
}