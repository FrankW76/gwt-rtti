# Usage #
First of all you need to import the module in your `gwt.xml`
```
<inherits name='com.promis.rtti.Rtti'/>
```

## Specify what packages/classes will have RTTI attached to them ##
Packages that contain classes for which you want RTTI to be generated must have `package-info.java` similar to this:
```
@RttiPackage
package some.package.SomeClass;

import com.promis.rtti.annotations.RttiPackage;
```

All classes that are descendants of `RttiObject` will have RTTI assigned to them by default other classes and interfaces must be marked with `@GenerateRtti` annotation. It has little reason to generate RTTI for interfaces without generating method information (it may not be so useful even after adding this but the support is there ;-)) which can be done by specifying `@GenerateMethods` annotation (for both classes and interfaces).

If you want to create a property that has getter and setter but doesn't modify internal field with proper naming convention (supported naming conventions are `getSomething`, `setSomething` or `isSomething` for booleans, both upper and lower case character are supported after get/set/is) there still has to be one (preferably with `@SuppressWarnings("unused")`) it will inform the RTTI generator to look for getters and setters in the first place and you can also use it to specify annotations for this property (the field itself should be omitted by GWT compiler so it really is just place-hold for annotations).

### Other modifiers ###
  * `@NoCompare`
    * If specified on class entire class won't have comparing support
    * If specified on field the field (property) won't be compared
  * `@NoAssign`
    * If specified on class entire class won't have assigning support
    * If specified on field the field (property) won't be assigned

## Get class information ##
```
RttiClass clazz = Rtti.forName("some.package.SomeClass");
```
or
```
RttiClass clazz = Rtti.getTypeInfo(SomeClass.class);
```
or
```
SomeClass inst = new SomeClass();
RttiClass clazz = Rtti.getTypeInfo(inst);
```

## Access fields/annotations etc. ##
This should be pretty straight forward as this is similar to Java Class/Field/Method approach.

## Compare objects ##
Objects must be of the same class in order for the comparison to return true.
```
Object obj1 = new SomeObject();
Object obj2 = new SomeObject();
RttiClass clazz = Rtti.typeInfo(SomeObject.class);
boolean areEqual = clazz.compare(obj1, obj2);
```
Or in case the object implements `Comparable` (see `Persistent`) this should be as easy as
```
Persistent obj1 = new SomeObject();
Object obj2 = new SomeObject();
boolean areEqual = obj1.compare(obj2);
```

## Assign objects ##
Objects doesn't have to be of the same class in order for the assign to work, they must however share common superclass whose properties will be assigned.
```
Object source = new SomeObject();
Object dest = new SomeObject();
RttiClass clazz = Rtti.typeInfo(SomeObject.class);
boolean success = clazz.assign(dest, source);
```
Or in case the object implements `Assignable` (see `Persistent`) this should be as easy as
```
Object source = new SomeObject();
Persistent dest = new SomeObject();
try
{
  dest.assign(source);
}
catch (UnableToAssignException e)
{
  //...
}
```

## More information ##
Some [javadoc](http://gwt-rtti.googlecode.com/svn/trunk/javadoc/index.html) should be there to help you get started.

See the tests for more examples.