package org.github.builders.generator;

import javax.lang.model.element.Modifier;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by julian3 on 15/03/03.
 */
public class CodeBuilder {


    int level = 0;

    String className;
    Modifier classModifer;
    Set<String> imports;
    Set<String> staticImports;


    private CodeBuilder(String className, Modifier classModifier) {
        assert className != null;
        assert classModifier != null;

        this.className = className;
        this.classModifer = classModifier;
        this.imports = new LinkedHashSet<>();
        this.staticImports = new LinkedHashSet<>();
    }

    public static CodeBuilder create(String className, Modifier modifier) {
        return new CodeBuilder(className, modifier);
    }


    public CodeBuilder importClass(String className) {
        imports.add(className);
        return this;
    }

    public CodeBuilder importPackage(String packageName) {
        imports.add(packageName+".*");
        return this;
    }


    public CodeBuilder importStaticClass(String className) {
        staticImports.add(className);
        return this;
    }

    public CodeBuilder importStaticPackage(String packageName) {
        staticImports.add(packageName+".*");
        return this;
    }


    public static String expression(String expr) {
        return expr + ";";
    }

    public static String exprInParens(String expr) {
        return "(" + expr + ")";
    }

    public static String assignment(String variable, String value) {
        return variable+ " = "+value;
    }

    public static String cast(String variable, String castType) {
        return "("+castType+") "+variable;
    }




}
