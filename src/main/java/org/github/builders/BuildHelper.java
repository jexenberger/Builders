package org.github.builders;

import org.github.builders.generator.Block;
import org.github.builders.generator.BlockHandler;
import org.github.builders.generator.ClassBlock;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;

import static org.github.builders.Pair.p;
import static org.github.builders.generator.BeanUtils.getProperty;
import static org.github.builders.generator.BeanUtils.isSetter;
import static org.github.builders.generator.BeanUtils.wrap;
import static org.github.builders.generator.Block.classBlock;
import static org.github.builders.generator.Block.ifBlock;
import static org.github.builders.generator.Expressions.*;
import static org.github.builders.generator.Expressions.assignValue;
import static org.github.builders.generator.StringUtils.concat;
import static org.github.builders.generator.StringUtils.decapitalize;
import static org.github.builders.generator.StringUtils.join;

/**
 * Created by julian3 on 15/03/03.
 */
public class BuildHelper {
     static ClassBlock createClass(TypeElement classElement, String packageName, String typePackageName, String typeName, String builderName, String extendsClass, String[] implementsInterfaces) {
        return classBlock(packageName, builderName)
                .extendsClass(extendsClass)
                .implementsInterface(implementsInterfaces)
                .importClass(join(".",typePackageName,typeName))
                .field(Modifier.PRIVATE, classElement.getSimpleName().toString(), "val")
                .constructor(Block.constructorBlock(builderName, new BlockHandler() {
                    @Override
                    public void handleBlock(Block block) {
                        block.add(ifBlock(eq("theVal", "null"), new BlockHandler() {
                            @Override
                            public void handleBlock(Block block) {
                                block.add(throwException(createNew("IllegalArgumentException", stringLiteral("theVal parameter was passed a null value"))));
                            }
                        }));
                        block.add(assignValue("this.val", "theVal"));
                    }
                }).parameters(p(typeName, "theVal")));
    }

    static void createCreatorMethod(final String typeName, final String builderName, ClassBlock classBlock, Element enclosedElement, boolean useLambdas, ProcessingEnvironment processingEnv) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "Creating constructor  for -> " + enclosedElement.getSimpleName());
        String createName = typeName;
        ExecutableElement executableElement = (ExecutableElement) enclosedElement;
        List<? extends VariableElement> parameters = executableElement.getParameters();
        Collection<Pair<String, String>> parmsList = new ArrayList<Pair<String, String>>();
        Collection<String> parms = new ArrayList<String>();

        for (VariableElement parameter : parameters) {
            parmsList.add(p(parameter.asType().toString(), parameter.getSimpleName().toString()));
            parms.add(parameter.getSimpleName().toString());
        }
        classBlock.method(Block.method(createName, builderName, new BlockHandler() {
            @Override
            public void handleBlock(Block block) {
                block.add(returnValue(createNew(builderName, createNew(typeName, parms.toArray(new String[]{})))));
            }
        }).parameters(parmsList.toArray(new Pair[]{})).isStatic());

    }

    static ClassBlock createStaticConstructorWithSupplier(String typeName, final String builderName, ClassBlock classBlock) {
        return classBlock.method(Block.method(typeName, builderName, new BlockHandler() {
            @Override
            public void handleBlock(Block block) {
                block.add(returnValue(createNew(builderName, invoke("supplier", "get"))));
            }
        }).parameters(p(type("java.util.function.Supplier",typeName), "supplier")).isStatic());
    }

    static ClassBlock createFromMap(String builderName, ClassBlock classBlock, final Map<String, Pair<String, ExecutableElement>> setters) {
        return classBlock.method(Block.method("fromMap", builderName, new BlockHandler() {
            @Override
            public void handleBlock(Block block) {
                block.add(ifBlock(eq("map", "null"), new BlockHandler() {
                    @Override
                    public void handleBlock(Block block) {
                        block.add(throwException(createNew("IllegalArgumentException", stringLiteral("map parameter was passed a null value"))));
                    }
                }));
                for (String s : setters.keySet()) {
                    Pair<String, ExecutableElement> setter = setters.get(s);
                    TypeMirror typeMirror = setter.getRight().getParameters().get(0).asType();
                    String castType = (typeMirror.getKind().isPrimitive()) ? wrap(typeMirror.getKind()) : typeMirror.toString();
                    block.add(invoke("val", setter.getLeft(), cast(castType, invoke("map", "get", stringLiteral(s)))));
                }
                block.add(returnValue("this"));
            }
        }).parameters(p(type("java.util.Map", "String", "Object"), "map")));
    }

    static void createToMap(ClassBlock classBlock, final Map<String, String> getters) {
        classBlock.method(Block.method("toMap", type("java.util.Map", "String", "Object"), new BlockHandler() {
            @Override
            public void handleBlock(Block block) {

                block.add(declare(type("java.util.Map", "String", "Object"), "map", createNew(type("java.util.LinkedHashMap", "String", "Object"))));
                for (String s : getters.keySet()) {
                    String getter = getters.get(s);
                    block.add(invoke("map", "put", stringLiteral(s), invoke("val", getter)));
                }

                block.add(returnValue("map"));
            }
        }));
    }

    static void createGetMethod(String typeName, ClassBlock classBlock) {
        classBlock.method(Block.method("get", typeName, new BlockHandler() {
            @Override
            public void handleBlock(Block block) {
                block.add(returnValue("this.val"));
            }
        }));
    }
    static String createBuilderMethod(String typeName, String builderName, ClassBlock classBlock, Element enclosedElement, boolean useLambdas, ProcessingEnvironment processingEnv) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "Processing -> " + enclosedElement.getSimpleName());
        String setter = enclosedElement.getSimpleName().toString();
        String methodName = getProperty(setter);
        ExecutableElement executableElement = (ExecutableElement) enclosedElement;
        if (isSetter(executableElement, processingEnv)) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "Creating build method for -> " + enclosedElement.getSimpleName());
            VariableElement typeParameterElement = executableElement.getParameters().get(0);
            classBlock.method(Block.method(methodName, builderName, new BlockHandler() {
                @Override
                public void handleBlock(Block block) {
                    block.add(invoke("val", setter, "theVal"))
                            .add(returnValue("this"));

                }
            }).parameters(p(typeParameterElement.asType().toString(), "theVal")));
            String type = (typeParameterElement.asType().getKind().isPrimitive()) ? wrap(typeParameterElement.asType().getKind()) : typeParameterElement.asType().toString();
            if (useLambdas) {
                classBlock.method(Block.method(methodName, builderName, new BlockHandler() {
                    @Override
                    public void handleBlock(Block block) {
                        block.add(invoke("val", setter, invoke("supplier","get")))
                                .add(returnValue("this"));

                    }
                }).parameters(p(type("java.util.function.Supplier",type), "supplier")));
                classBlock.method(Block.method(methodName, builderName, new BlockHandler() {
                    @Override
                    public void handleBlock(Block block) {
                        block.add(invoke("val", setter, invoke("function","apply","val")))
                                .add(returnValue("this"));

                    }
                }).parameters(p(type("java.util.function.Function",typeName, type), "function")));
            }
            return methodName;
        }
        return null;
    }


    static void createWithMethod(String typeName, final String builderName, ClassBlock classBlock) {
        classBlock.method(Block.method(concat("with", typeName), builderName, new BlockHandler() {
            @Override
            public void handleBlock(Block block) {
                block.add(returnValue(createNew(builderName, "theVal")));
            }
        }).isStatic().parameters(p(typeName, "theVal")));
    }

}
