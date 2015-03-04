package org.github.builders;

import org.github.builders.generator.BeanUtils;
import org.github.builders.generator.ClassBlock;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;

import static org.github.builders.BuildHelper.*;
import static org.github.builders.Pair.p;
import static org.github.builders.generator.BeanUtils.isGetter;
import static org.github.builders.generator.StringUtils.concat;
import static org.github.builders.generator.StringUtils.join;

/**
 * Created by julian3 on 15/03/03.
 */
@SupportedAnnotationTypes("org.github.builders.Built")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class BuilderProcessor extends AbstractProcessor {


    public BuilderProcessor() {
        super();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        int sourceVersion = processingEnv.getSourceVersion().ordinal();
        boolean useLambdas = sourceVersion >= 8;
        if (!useLambdas) {
            printMessage("unable to use Lambdas at source level " + processingEnv.getSourceVersion() + "(" + sourceVersion + ")");
        }
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Built.class);
        for (Element element : elements) {
            if (element.getKind().equals(ElementKind.CLASS)) {
                TypeElement classElement = (TypeElement) element;
                Built builtAnnotation = classElement.getAnnotation(Built.class);
                PackageElement packageElement = (PackageElement) classElement.getEnclosingElement();
                JavaFileObject jfo = null;
                try {
                    String typeName = classElement.getSimpleName().toString();
                    String builderName = (!builtAnnotation.name().equals("")) ? builtAnnotation.name() : concat(typeName,"Builder");
                    String typePackageName = packageElement.getQualifiedName().toString();
                    String packageName = (!builtAnnotation.packageName().equals("")) ? builtAnnotation.packageName() : typePackageName;
                    String extendClass = (!builtAnnotation.extendsClass().equals("")) ? builtAnnotation.extendsClass() : null;
                    String[] interfaceClasses = (builtAnnotation.implementsInterfaces().length > 0) ? builtAnnotation.implementsInterfaces() : null;
                    String outputSourceFile = join(".", packageName, builderName);
                    jfo = processingEnv.getFiler().createSourceFile(outputSourceFile);
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "Generating -> " + outputSourceFile);
                    BufferedWriter bw = new BufferedWriter(jfo.openWriter());

                    ClassBlock classBlock = createClass(classElement, packageName, typePackageName, typeName, builderName, extendClass, interfaceClasses);
                    Map<String, Pair<String, ExecutableElement>> setters = new LinkedHashMap<String, Pair<String, ExecutableElement>>();
                    Map<String, String> getters = new LinkedHashMap<String, String>();
                    List<? extends Element> enclosedElements = classElement.getEnclosedElements();
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "Processing -> " + enclosedElements);
                    for (Element enclosedElement : enclosedElements) {
                        if (enclosedElement.getKind().equals(ElementKind.METHOD) && enclosedElement.getModifiers().contains(Modifier.PUBLIC) && !enclosedElement.getModifiers().contains(Modifier.STATIC)) {
                            ExecutableElement executableElement = (ExecutableElement) enclosedElement;
                            String property = createBuilderMethod(typeName, builderName, classBlock, enclosedElement, useLambdas, processingEnv);
                            if (property != null) {
                                setters.put(property, p(enclosedElement.getSimpleName().toString(), executableElement));
                            }
                            if (isGetter(executableElement, processingEnv)) {
                                String propertyName = BeanUtils.getProperty(enclosedElement.getSimpleName().toString());
                                getters.put(propertyName, executableElement.getSimpleName().toString());
                            }
                        } else if (enclosedElement.getKind().equals(ElementKind.CONSTRUCTOR) && enclosedElement.getModifiers().contains(Modifier.PUBLIC)) {
                            createCreatorMethod(typeName, builderName, classBlock, enclosedElement, useLambdas, processingEnv);
                        }
                    }
                    if (useLambdas) {
                        createStaticConstructorWithSupplier(typeName, builderName, classBlock);
                    }
                    createGetMethod(typeName, classBlock);
                    createWithMethod(typeName, builderName, classBlock);
                    createFromMap(builderName, classBlock, setters);
                    createToMap(classBlock, getters);

                    bw.append(classBlock.render());
                    bw.flush();
                    bw.close();

                    // rest of generated class contents

                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }
        return true;
    }

    private void printMessage(String msg) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, msg);
    }


}
