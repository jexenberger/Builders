package org.github.builders;

import org.github.builders.generator.Block;
import org.github.builders.generator.BlockHandler;
import org.github.builders.generator.ClassBlock;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;

import static org.github.builders.Pair.p;
import static org.github.builders.generator.Block.classBlock;
import static org.github.builders.generator.Block.ifBlock;
import static org.github.builders.generator.Expressions.*;
import static org.github.builders.generator.StringUtils.concat;
import static org.github.builders.generator.StringUtils.decapitalize;
import static org.github.builders.generator.StringUtils.join;

/**
 * Created by julian3 on 15/03/03.
 */
@SupportedAnnotationTypes("org.github.builders.Built")
public class BuilderProcessor extends AbstractProcessor {


    public BuilderProcessor() {
        super();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        int sourceVersion = processingEnv.getSourceVersion().ordinal();
        boolean useLambdas = sourceVersion >= 8;
        if (!useLambdas) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "unable to use Lambdas at source level "+processingEnv.getSourceVersion()+"("+sourceVersion+")");
        }
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Built.class);
        for (Element element : elements) {
            if (element.getKind().equals(ElementKind.CLASS)) {


                TypeElement classElement = (TypeElement) element;

                PackageElement packageElement =
                        (PackageElement) classElement.getEnclosingElement();

                JavaFileObject jfo = null;
                try {
                    String typeName = classElement.getSimpleName().toString();
                    String builderName = typeName + "Builder";
                    jfo = processingEnv.getFiler().createSourceFile(
                            builderName);
                    BufferedWriter bw = new BufferedWriter(jfo.openWriter());

                    ClassBlock classBlock = classBlock(packageElement.getQualifiedName().toString(), builderName)
                            .field(Modifier.PRIVATE, classElement.getSimpleName().toString(), "val")
                            .constructor(Block.constructorBlock(builderName, new BlockHandler() {
                                @Override
                                public void handleBlock(Block block) {
                                    block.add(ifBlock(eq("theVal", "null"), new BlockHandler() {
                                        @Override
                                        public void handleBlock(Block block) {
                                            block.add(throwException(createNew("IllegalArgumentException",stringLiteral("theVal parameter was passed a null value"))));
                                        }
                                    }));
                                    block.add(assignValue("this.val", "theVal"));
                                }
                            }).parameters(p(typeName, "theVal")));


                    Map<String, Pair<String,ExecutableElement>> setters = new LinkedHashMap<String, Pair<String,ExecutableElement>>();
                    Map<String, String> getters = new LinkedHashMap<String, String>();
                    List<? extends Element> enclosedElements = classElement.getEnclosedElements();
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "Processing -> " + enclosedElements);
                    for (Element enclosedElement : enclosedElements) {
                        if (enclosedElement.getKind().equals(ElementKind.METHOD) && enclosedElement.getModifiers().contains(Modifier.PUBLIC) && !enclosedElement.getModifiers().contains(Modifier.STATIC)) {
                            ExecutableElement executableElement = (ExecutableElement) enclosedElement;
                            String property = createBuilderMethod(typeName, builderName, classBlock, enclosedElement, useLambdas);
                            if (property != null) {
                                setters.put(property, p(enclosedElement.getSimpleName().toString(), executableElement));
                            }
                            if (isGetter(executableElement)) {
                                String propertyName = getProperty(enclosedElement.getSimpleName().toString());
                                getters.put(propertyName, executableElement.getSimpleName().toString());
                            }
                        } else if (enclosedElement.getKind().equals(ElementKind.CONSTRUCTOR) && enclosedElement.getModifiers().contains(Modifier.PUBLIC)) {
                            createCreatorMethod(typeName, builderName, classBlock, enclosedElement, useLambdas);
                        }


                    }

                    if (useLambdas) {
                        classBlock.method(Block.method(concat("create", typeName), builderName, new BlockHandler() {
                            @Override
                            public void handleBlock(Block block) {
                                block.add(returnValue(createNew(builderName, invoke("supplier","get"))));
                            }
                        }).parameters(p(type("java.util.function.Supplier",typeName), "supplier")).isStatic());
                    }


                    classBlock.method(Block.method("get", typeName, new BlockHandler() {
                        @Override
                        public void handleBlock(Block block) {
                            block.add(returnValue("this.val"));
                        }
                    }));

                    classBlock.method(Block.method(concat("with", typeName), builderName, new BlockHandler() {
                        @Override
                        public void handleBlock(Block block) {
                            block.add(returnValue(createNew(builderName, "theVal")));
                        }
                    }).isStatic().parameters(p(typeName, "theVal")));


                    classBlock.method(Block.method("fromMap", builderName, new BlockHandler() {
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
                                block.add(invoke("val",setter.getLeft(), cast(setter.getRight().getParameters().get(0).asType().toString(), invoke("map","get", stringLiteral(s)))));
                            }
                            block.add(returnValue("this"));
                        }
                    }).parameters(p(type("java.util.Map", "String", "Object"), "map")));

                    classBlock.method(Block.method("toMap", type("java.util.Map", "String", "Object"), new BlockHandler() {
                        @Override
                        public void handleBlock(Block block) {

                            block.add(declare(type("java.util.Map", "String", "Object"), "map", createNew(type("java.util.LinkedHashMap", "String", "Object"))));
                            for (String s : getters.keySet()) {
                                String getter = getters.get(s);
                                block.add(invoke("map","put", stringLiteral(s), invoke("val",getter)));
                            }

                            block.add(returnValue("map"));
                        }
                    }));

                    bw.append(classBlock.render());
                    bw.flush();
                    bw.close();

                    // rest of generated class contents

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }
        return true;
    }

    private void createCreatorMethod(final String typeName, final String builderName, ClassBlock classBlock, Element enclosedElement, boolean useLambdas) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "Creating constructor  for -> " + enclosedElement.getSimpleName());
        String createName = concat("create", typeName);
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

    private String createBuilderMethod(String typeName, String builderName, ClassBlock classBlock, Element enclosedElement, boolean useLambdas) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "Processing -> " + enclosedElement.getSimpleName());
        String setter = enclosedElement.getSimpleName().toString();
        String methodName = getProperty(setter);
        ExecutableElement executableElement = (ExecutableElement) enclosedElement;
        if (isSetter(executableElement)) {
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

    private String getProperty(String setter) {
        if (setter.startsWith("set") || setter.startsWith("get")) {
            return decapitalize(setter.substring(3));
        } else {
            return decapitalize(setter.substring(2));
        }
    }


    public boolean isSetter(ExecutableElement element) {

        Types types = processingEnv.getTypeUtils();

        boolean isVoid = types.isSameType(types.getNoType(TypeKind.VOID), element.getReturnType());
        boolean isSetter = element.getSimpleName().toString().startsWith("set");
        boolean hasSingleParameter = element.getParameters().size() == 1;

        processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "\tisVoid -> " + isVoid);
        processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "\tisSetter -> " + isSetter);
        processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "\thasSingleParameter -> " + hasSingleParameter);

        return isVoid & isSetter & hasSingleParameter;

    }

    public boolean isGetter(ExecutableElement element) {

        Types types = processingEnv.getTypeUtils();

        boolean isNotVoid = !types.isSameType(types.getNoType(TypeKind.VOID), element.getReturnType());
        String name = element.getSimpleName().toString();
        boolean isGetter = name.startsWith("get") || name.startsWith("is");
        boolean hasNoParameters = element.getParameters().size() == 0;

        processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "\tisNotVoid -> " + isNotVoid);
        processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "\tisGetter -> " + isGetter);
        processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "\thasNoParameter -> " + hasNoParameters);

        return isNotVoid & isGetter & hasNoParameters;

    }


    @SuppressWarnings("unchecked")
    private static <T> String wrap(TypeKind c) {
        return c.isPrimitive() ? PRIMITIVES_TO_WRAPPERS.get(c).getName() : null;
    }

    private static final Map<TypeKind, Class<?>> PRIMITIVES_TO_WRAPPERS
            = new HashMap<TypeKind, Class<?>>();

    static {
        PRIMITIVES_TO_WRAPPERS.put(TypeKind.BOOLEAN, Boolean.class);
        PRIMITIVES_TO_WRAPPERS.put(TypeKind.BYTE, Byte.class);
        PRIMITIVES_TO_WRAPPERS.put(TypeKind.CHAR, Character.class);
        PRIMITIVES_TO_WRAPPERS.put(TypeKind.DOUBLE, Double.class);
        PRIMITIVES_TO_WRAPPERS.put(TypeKind.FLOAT, Float.class);
        PRIMITIVES_TO_WRAPPERS.put(TypeKind.INT, Integer.class);
        PRIMITIVES_TO_WRAPPERS.put(TypeKind.LONG, Long.class);
        PRIMITIVES_TO_WRAPPERS.put(TypeKind.SHORT, Short.class);
        PRIMITIVES_TO_WRAPPERS.put(TypeKind.VOID, Void.class);
    }
}
