##Overview
Builders is an [Annotation Processor](http://docs.oracle.com/javase/7/docs/jdk/api/apt/mirror/com/sun/mirror/apt/AnnotationProcessor.html) which generates a [Fluent](http://en.wikipedia.org/wiki/Fluent_interface) [Builder](http://en.wikipedia.org/wiki/Builder_pattern) which can then be used to create and/or populate objects.

To use Builders, simply add the [Builders library](https://github.com/jexenberger/Builders) to your classpath and annotate the class you want to create a builder for using the [org.github.builders.Built](https://github.com/jexenberger/Builders/blob/master/src/main/java/org/github/builders/Built.java) annotation.

Builders will then do the following:
* Generate the Builder class *(your type name)Builder* by default.
* For each public setter method in your class, Builders will generate a fluent builder method.
* For each constructor, Builders will generate a static constructor method in the builder.
* A method which can take an existing instance of your type.
* A *fromMap* method which takes a [java.util.Map](http://docs.oracle.com/javase/7/docs/api/java/util/Map.html), to populate your type instance from a Map.
* A *toMap* method which populates and returns a [java.util.Map](http://docs.oracle.com/javase/7/docs/api/java/util/Map.html) of your type instance.
* If you are using Java 8, Builders will also generate:
  * A builder method for each setter method which accepts a [java.util.function.Supplier](http://docs.oracle.com/javase/8/docs/api/java/util/function/Supplier.html) to return the value.
  * A builder method for each setter method which accepts a [java.util.function.Function](http://docs.oracle.com/javase/8/docs/api/java/util/function/Function.html) giving the instance and returning the value.
  * A static constructor method accepts a [java.util.function.Supplier](http://docs.oracle.com/javase/8/docs/api/java/util/function/Supplier.html) to return an instance of your type.

##Sample
Given the following class:

```java
package org.test;
import org.github.builders.Built;
@Built
public class AClass {

    private String name;
    private int numberOfSiblings;
    private String surname;
    private boolean gender;

    public AClass() {
    }

    public AClass(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfSiblings() {
        return numberOfSiblings;
    }

    public void setNumberOfSiblings(int numberOfSiblings) {
        this.numberOfSiblings = numberOfSiblings;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "AClass{" +
                "name='" + name + '\'' +
                ", numberOfSiblings=" + numberOfSiblings +
                ", surname='" + surname + '\'' +
                ", gender=" + gender +
                '}';
    }
}
```


Builders will generate the following class (Java version < 8):

```java
package org.test;

import org.test.AClass;

public class AClassBuilder {
	private AClass val;

	public AClassBuilder(AClass theVal) {

		if (theVal == null) {
			throw new IllegalArgumentException("theVal parameter was passed a null value");
		}
		this.val = theVal;
	}

	public static AClassBuilder AClass() {
		return new AClassBuilder(new AClass());
	}

	public static AClassBuilder AClass(java.lang.String name) {
		return new AClassBuilder(new AClass(name));
	}

	public AClassBuilder name(java.lang.String theVal) {
		val.setName(theVal);
		return this;
	}

	public AClassBuilder numberOfSiblings(int theVal) {
		val.setNumberOfSiblings(theVal);
		return this;
	}

	public AClassBuilder surname(java.lang.String theVal) {
		val.setSurname(theVal);
		return this;
	}

	public AClassBuilder gender(boolean theVal) {
		val.setGender(theVal);
		return this;
	}

	public AClass get() {
		return this.val;
	}

	public static AClassBuilder withAClass(AClass theVal) {
		return new AClassBuilder(theVal);
	}

	public AClassBuilder fromMap(java.util.Map<String, Object> map) {

		if (map == null) {
			throw new IllegalArgumentException("map parameter was passed a null value");
		}
		val.setName((java.lang.String) map.get("name"));
		val.setNumberOfSiblings((java.lang.Integer) map.get("numberOfSiblings"));
		val.setSurname((java.lang.String) map.get("surname"));
		val.setGender((java.lang.Boolean) map.get("gender"));
		return this;
	}

	public java.util.Map<String, Object> toMap() {
		java.util.Map<String, Object> map = new java.util.LinkedHashMap<String, Object>();
		map.put("name", val.getName());
		map.put("numberOfSiblings", val.getNumberOfSiblings());
		map.put("surname", val.getSurname());
		map.put("gender", val.isGender());
		return map;
	}
}

```

This can then be used as follows:
```java
package org.test;

public class Test {


    public static void main(String[] args) {
        AClass aClass = AClassBuilder.AClass()
                .gender(true)
                .surname("Smith")
                .numberOfSiblings(5)
                .get();

        System.out.println(aClass);
    }
}

```





