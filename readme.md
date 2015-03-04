Builders is an [Annotation Processor](http://docs.oracle.com/javase/7/docs/jdk/api/apt/mirror/com/sun/mirror/apt/AnnotationProcessor.html) which generates a [Fluent](http://en.wikipedia.org/wiki/Fluent_interface) [Builder](http://en.wikipedia.org/wiki/Builder_pattern) which can then be used to create and/or populate objects.

To use Builders, simply add the [Builders library](https://github.com/jexenberger/Builders) to your classpath and annotate the class you want to create a builder for using the [org.github.builders.Built](https://github.com/jexenberger/Builders/blob/master/src/main/java/org/github/builders/Built.java) annotation.

Builders will then do the following:
1. Generate the Builder class *(your type name)Builder* by default.
2. For each public setter method in your class, Builders will generate a fluent builder method.
3. For each constructor, Builders will generate a static constructor method in the builder.
4. A *fromMap* method which takes a [java.util.Map](http://docs.oracle.com/javase/7/docs/api/java/util/Map.html), to populate your type instance from a Map.
5. A *toMap* method which populates and returns a [java.util.Map](http://docs.oracle.com/javase/7/docs/api/java/util/Map.html) of your type instance.
6. If you are using Java 8, Builders will also generate:
--1. A builder method for each setter method which accepts a [java.util.function.Supplier](http://docs.oracle.com/javase/8/docs/api/java/util/function/Supplier.html) to return the value.
--2. A builder method for each setter method which accepts a [java.util.function.Function](http://docs.oracle.com/javase/8/docs/api/java/util/function/Function.html) giving the instance and returning the value.
--3. A static accepts a [java.util.function.Supplier](http://docs.oracle.com/javase/8/docs/api/java/util/function/Supplier.html) to return an instance of your type.
