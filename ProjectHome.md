# GWT RTTI #

This project aims to add runtime type information (reflection) support to GWT client side
code. Basically it adds Java Class/Field/Method equivalents.

## Why this name? ##
I created this library while I was porting some Delphi code to GWT and RTTI is how reflection is called in Delphi, also it is a prefix for all the interfaces that are used to query object information.

## What this library can and cannot do ##
  * It gives you access to class fields and methods (if you choose to generate it)
  * It gives you access to all public fields or any fields that have getters and/or setters (which takes precedence over direct access) so fields are more like properties. Every property can have index (specified by annotation) so thats what makes it a little like Delphi properties.
  * It has annotation support on fields/classes/methods
  * It adds class assign/compare code (something I need and didn't want to move to some extended package, which I might do in the future to clean up the sources a little)
  * It can call any method or parameterless constructor (`newInstance()`) of reflected class (if you choose to do so see [Usage](Usage.md)).
  * No external tools required everything is annotation/GWT generator based
  * It doesn't support anonymous inner classes/interfaces
  * Array support is currently very limited

## TODOs ##
  * Improve readability/structure of the generator (make it less monolithic)
  * Split tests to several classes (also to improve readability)
  * Separate extended features
  * Add support for more types (arrays)
  * Reduce dependencies (on my proprietary libraries)
