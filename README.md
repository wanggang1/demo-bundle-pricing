Code Sample
===================

Introduction
------------

This reference implementation is intended for demostration of coding skills in Scala, to aid the technical evaluation in an intervew process. 


Bundle Pricing
--------------
This exercise is a common problem in e­commerce retail systems.  It is implemented as a library.  APIs are provided for adding item to store inventory, adding discount bundles, and computing the lowest price considering all possible discount bundles.

### User Stores

A customer shops in a grocery store, selects items and any quantity of an item, then checks out.  In this store, certain groups of items can be taken together as a "bundle" with a discounted price (comparing to indvidusl item's unit price).  One example bundle is "buy 1 loaf of bread get the 2nd free".  Another bundle could be "buy two loaves of bread and get a jar of peanut butter half price".  The same item can appear in more than one bundle, therefore, a cart of items can be combined in more than one way.  The goal is to produce the lowest cost for a given cart of items.

### Features

* An Item has a unique name and a price.
* A Bundle has a unique key and a list of Item Pricings.
* Ability to add an Item to store
* Ability to query Item by name
* Ability to add discount Bundle to store
* Ability to query all the bundles in store
* Ability to calculate price at checkout
* Ability to handle multiple simultaneous calls without errors

### TODOs

* Item Add route need to use ItemWriterActor, so it also single BundleActor to add an unit price Bundle
* Item Delete route need to use ItemWriterActor, so it also single BundleActor to delete all Bundles containing this item
* Item Update route need to use ItemWriterActor, so it also single BundleActor to update all Bundles containing this item
* add Bundle validation and PartialBundle for Bundle’s create/update routes
* all Bundle routes must use BundleActor because BundleActor maintains a Bundle cache
* BundleActor only delete unit price Bundle through Item deletion signal
* BundeActor should use ItemReaderRouter to validate Item existence before adding/updating the bundle
* add a route to calculate bundled price

### Possible Issues

* Bundles that contain exactly the same items but have different pricing policy (if there is ever a use case for that)








