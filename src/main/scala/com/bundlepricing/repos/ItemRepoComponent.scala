package com.bundlepricing.repos

import com.bundlepricing.domains.Item

trait ItemRepoComponent {
  def itemRepo: ItemRepo

  trait ItemRepo extends RepoMetaData with Repository {
    type Id = String
    type Entity = Item
    
    def id(entity: Item): Id = entity.name
  }
}
