package org.github.builders.generator;

import javax.lang.model.element.Modifier;
import java.util.Set;
import java.util.TreeSet;

import static org.github.builders.generator.Expressions.declare;
import static org.github.builders.generator.StringUtils.concat;
import static org.github.builders.generator.StringUtils.getBlank;
import static org.github.builders.generator.StringUtils.join;

public class ClassBlock extends Block {

    Modifier modifier = Modifier.PUBLIC;
    Set<String> classImports;
    Set<String> packageImpors;
    Set<String> staticClassImports;
    Set<String> staticImports;
    Set<String> fields;
    Set<MethodBlock> methods;
    boolean isStatic = false;
    private boolean isAbstract = false;
    String packageName = "";
    String extendsClass;
    String[] interfaces;



    public ClassBlock(String packageName, String name) {
        super(name, null);
        this.packageName = packageName;
        classImports = new TreeSet<>();
        packageImpors = new TreeSet<>();
        staticClassImports = new TreeSet<>();
        staticImports = new TreeSet<>();
        fields = new TreeSet<>();
        methods = new TreeSet<>();
    }

    public ClassBlock importClass(String clazz) {
        classImports.add(clazz);
        return this;
    }

    public ClassBlock importPackage(String clazz) {
        packageImpors.add(clazz);
        return this;
    }

    public ClassBlock staticClassImport(String clazz) {
        staticClassImports.add(clazz);
        return this;
    }

    public ClassBlock staticImport(String clazz) {
        staticImports.add(clazz);
        return this;
    }

    public ClassBlock isStatic() {
        this.isStatic = true;
        return this;
    }

    public ClassBlock modifier(Modifier modifier) {
        this.modifier = modifier;
        return this;
    }

    public ClassBlock isAbstract() {
        this.isAbstract = true;
        return this;
    }

    public ClassBlock field(Modifier modifier, String type, String name, Object defaultValue) {
        add(declare(modifier, type, name, defaultValue));
        return this;
    }

    public ClassBlock field(Modifier modifier, String type, String name) {
        add(declare(modifier, type, name));
        return this;
    }

    public ClassBlock field( String type, String name) {
        add(declare(type, name));
        return this;
    }

    public ClassBlock field( String type, String name, Object defaultValue) {
        add(declare(type, name, defaultValue));
        return this;
    }

    public ClassBlock method(MethodBlock block) {
        add(block);
        return this;
    }

    public ClassBlock constructor(ConstructorBlock block) {
        add(block);
        return this;
    }


    public ClassBlock extendsClass(String name) {
        this.extendsClass = name;
        return this;
    }

    public ClassBlock implementsInterface(String ... name) {
        this.interfaces = name;
        return this;
    }


    @Override
    public String render() {
        StringBuilder builder = new StringBuilder(wrap(join(" ", "package", this.packageName)));
        builder.append(newLine());
        for (String packageImpor : packageImpors) {
            builder.append(wrap(join(" ", "import", join(".", packageImpor, "*"))));
        }
        builder.append(newLine());
        for (String classImport : classImports) {
            builder.append(wrap(join(" ", "import", classImport)));
        }
        builder.append(newLine());
        for (String staticPackageImport : staticImports) {
            builder.append(wrap(join(" ", "import", "static", staticPackageImport)));
        }
        builder.append(newLine());
        for (String staticPackageImport : staticClassImports) {
            builder.append(wrap(join(" ", "import", "static", join(".", staticPackageImport, "*"))));
        }
        builder.append(newLine());
        builder.append(newLine());
        String implementsClause = (this.interfaces != null && this.interfaces.length > 0) ? join(" ", "implements",join(", ", this.interfaces)) : "";
        String extendsClause = (this.extendsClass != null && !this.extendsClass.trim().equals("")) ? join(" ","extends", this.extendsClass) : "";
        String typeExtensions = join(" ",extendsClause, implementsClause);
        String modifiers = join(" ",getBlank(this.modifier.toString()), (this.isStatic) ? "static" : "",(this.isAbstract) ? "abstract" : "" );
        String declaration = join(" ",modifiers ,"class", this.declaration, typeExtensions);
        setDeclaration(declaration);
        return concat(builder.toString(), super.render());
    }

    private String newLine() {
        return "\n";
    }
}

