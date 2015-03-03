package org.github.builders.generator;

import org.github.builders.Pair;

import javax.lang.model.element.Modifier;

import static org.github.builders.generator.StringUtils.concat;
import static org.github.builders.generator.StringUtils.join;
import static org.github.builders.generator.StringUtils.wrap;

/**
 * Created by julian3 on 15/03/03.
 */
public class Expressions {





    public static String parens(String expr) {
        return wrap("(", expr, ")");
    }

    public static String assignValue(String variable, Object value) {
        return assignment(variable,"=", value);
    }


    public static String invoke(String classOrInstance, String method, String... parameters) {
        return concat(classOrInstance, '.', StringUtils.concat(method, parameterList(parameters)));
    }


    public static String typed(String typeName, String... typeArguments) {
      return concat(typeName,typeList(typeArguments));
    }

    public static String createNew(String classOrInstance, String... parameters) {
        String first = "new "+classOrInstance;
        first += parameterList(parameters);
        return first;
    }

    public static String parameterList(String... parameters) {
        return createList("(",")", parameters);
    }

    public static String anyWildcard() {
         return "?";
    }

    public static String superWildcard(String extended, String type) {
         return extended+" super "+type;
    }

    public static String extendsWildcard(String extended, String type) {
         return extended+" extends "+type;
    }
    
    public static String stringLiteral(Object value) {
        return wrap("\"",value.toString(),"\"");
    }

    public static String charLiteral(char value) {
        return wrap("'",Character.valueOf(value).toString(),"'");
    }
    

    public static String type(String type, String ... types) {
        return concat(type, typeList(types));
    }

    public static String typeList(String... parameters) {
        return createList("<",">", parameters);
    }

    public static String inferedType(String type) {
        return concat(type,"<>");
    }

    public static String literalArray(Object ... parameters) {
        return createList("{","}", parameters);
    }

    public static String createList(String wrapperLeft, String wrapperRight, Object ... parameters) {
        return wrap(wrapperLeft, join(", ", parameters), wrapperRight);
    }
    
    public static String declare(String type, String name, Object defaultValue) {
        return join(" ", type,name,((defaultValue != null) ? concat("= ",defaultValue) : "")).trim();
    }

    public static String declare(Modifier modifier, String type, String name, Object defaultValue) {
        return join(" ", modifier.toString(), type,name,((defaultValue != null) ? concat("= ",defaultValue) : "")).trim();
    }

    public static String declare(String type, String name) {
        return declare(type, name, null);
    }

    public static String declare(Modifier modifier, String type, String name) {
        return declare(modifier, type, name, null);
    }

    public static String declareParameters(Pair<String, String>... parameters) {
        String first = "";
        for (Pair parameter : parameters) {
            first += parameter.getLeft()+" "+parameter.getRight() + ", ";
        }
        if (first.endsWith(", ")) {
            first = first.substring(0, first.lastIndexOf(", "));
        }
        return "("+first+")";
    }

    public static String assignment( String variable, String assigmentOperator, Object value) {
        return join(" ", variable, assigmentOperator, value.toString());
    }

    public static String cast( String castType, String variable) {
        return join(" ",wrap("(",castType,")"),variable);
    }

    public static String boolExpr(String operator, String left, String right) {
        return left + " " + operator + " " + right;
    }

    public static String throwException(String name) {
        return join(" ","throw", name);
    }

    public static String and(String left, String right) {
        return boolExpr("&&", left, right);
    }

    public static String notEq(String left, String right) {
        return boolExpr("!=", left, right);
    }

    public static String not(String expr) {
        return  "!" + parens(expr);
    }

    public static String returnValue(String value) {
        return  join(" ", "return" ,value);
    }

    public static String returnVoid() {
        return  "return";
    }

    public static String createBreak() {
        return "break";
    }

    public static String createContinue() {
        return "continue";
    }

    public static String eq(String left, String right) {
        return boolExpr("==", left, right);
    }

    public static String or(String left, String right) {
        return boolExpr("||", left, right);
    }

    public static String plus(String left, String right) {
        return boolExpr("+", left, right);
    }

    public static String minus(String left, String right) {
        return boolExpr("-", left, right);
    }

    public static String gt(String left, String right) {
        return boolExpr(">", left, right);
    }

    public static String lt(String left, String right) {
        return boolExpr("<", left, right);
    }

    public static String gte(String left, String right) {
        return boolExpr(">=", left, right);
    }

    public static String lte(String left, String right) {
        return boolExpr("<=", left, right);
    }

    public static String bitwiseAnd(String left, String right) {
        return boolExpr("&", left, right);
    }

    public static String bitwiseOr(String left, String right) {
        return boolExpr("|", left, right);
    }

    public static String xor(String left, String right) {
        return boolExpr("^", left, right);
    }


    public static String postInc(String var) {
        return concat(var,"++");
    }

    public static String preInc(String var) {
        return concat("++",var);
    }

    public static String postDec(String var) {
        return concat(var,"--");
    }

    public static String preDec(String var) {
        return concat("--",var);
    }



}
