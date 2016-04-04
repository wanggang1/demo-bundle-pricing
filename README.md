Code Sample
===================

Introduction
------------

This reference implementation is intended for demostration of coding skills in Scala, to aid the technical evaluation in an intervew process. 


Bundle Pricing
--------------
This exercise is a common problem in e­commerce retail systems.  It is implemented as a library.  APIs are provided for adding item to store inventory, adding discount bundles, and computing the lowest price considering all possible discount bundles.

### User Stores

A customer shops in a grocery store, selects items and any quantity of an item, then checks out.  In this store, certain groups of items can be taken together as a "bundle" with a discounted price (comparing to indvidusl item's unit price).  One example bundle is "buy 1 loaf of bread get the 2nd free".  Another bundle could be "buy two loafs of bread and get a jar of peanut butter half price".  The same item can appear in more than one bundle, therefore, a cart of items can be combined in more than one way.  The goal is to produce the lowest cost for a given cart of items.

### Features

* An Item has a unique name a price.
* A Bundle has a list of Items and a discount pricing for this bundle.
* Ability to add an Item to store
* Ability to query Item by name
* Ability to add discount Bundle to store
* Ability to query all the bundles in store
* Ability to calculate price at checkout
* Ability to handle multiple simultaneous calls without errors

### TODOs

* User Input Validation
* Exception Handling
* Optimization

### Possible Issues

* Handle the bundles that contain exactly the items but different pricing policy (if there is ever a use case for this)








