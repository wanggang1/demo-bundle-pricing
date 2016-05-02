package com.bundlepricing.actors

import akka.actor._

import com.bundlepricing.repos.BundleRepoComponent

class BundleActor extends Actor with ActorLogging  {
  self: BundleRepoComponent =>
    
  def receive = {
    case _ =>
  }
  
}