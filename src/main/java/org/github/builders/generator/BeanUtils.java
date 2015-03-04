package org.github.builders.generator;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.HashMap;
import java.util.Map;

import static org.github.builders.generator.StringUtils.decapitalize;

/**
 * Created by julian.exenberger on 2015-03-04.
 */
public class BeanUtils {

    @SuppressWarnings("unchecked")
    public static <T> String wrap(TypeKind c) {
        return c.isPrimitive() ? PRIMITIVES_TO_WRAPPERS.get(c).getName() : null;
    }

    public static String getProperty(String setter) {
        if (setter.startsWith("set") || setter.startsWith("get")) {
            return decapitalize(setter.substring(3));
        } else {
            return decapitalize(setter.substring(2));
        }
    }


    public static boolean isSetter(ExecutableElement element, ProcessingEnvironment processingEnv) {

        Types types = processingEnv.getTypeUtils();

        boolean isVoid = types.isSameType(types.getNoType(TypeKind.VOID), element.getReturnType());
        boolean isSetter = element.getSimpleName().toString().startsWith("set");
        boolean hasSingleParameter = element.getParameters().size() == 1;

        processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "\tisVoid -> " + isVoid);
        processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "\tisSetter -> " + isSetter);
        processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "\thasSingleParameter -> " + hasSingleParameter);

        return isVoid & isSetter & hasSingleParameter;

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

    public static boolean isGetter(ExecutableElement element, ProcessingEnvironment processingEnv) {

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

}
