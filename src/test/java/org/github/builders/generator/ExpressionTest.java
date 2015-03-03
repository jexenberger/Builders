package org.github.builders.generator;

import org.junit.Test;

import static org.github.builders.Pair.p;
import static org.github.builders.generator.Expressions.*;
import static org.junit.Assert.assertEquals;

/**
 * Created by julian3 on 15/03/03.
 */
public class ExpressionTest {

    @Test
    public void testParens() throws Exception {
        assertEquals("(hello)", parens("hello"));
    }

    @Test
    public void testAssignValue() throws Exception {
        assertEquals("x = 123", assignValue("x", 123));
    }

    @Test
    public void testInvoke() throws Exception {
        assertEquals("o.equals(a, b)", invoke("o", "equals", "a", "b"));
    }

    @Test
    public void testTyped() throws Exception {
        assertEquals("Map<String, Integer>", typed("Map", "String", "Integer"));
    }

    @Test
    public void testCreateNew() throws Exception {
        assertEquals("new Character('a')", createNew("Character", charLiteral('a')));
    }

    @Test
    public void testAnyWildcard() throws Exception {
        assertEquals("Map<String, ?>", typed("Map", "String", anyWildcard()));
    }

    @Test
    public void testSuperWildcard() throws Exception {
        assertEquals("Map<String, T super String>", typed("Map", "String", superWildcard("T", "String")));
    }

    @Test
    public void testExtendWildcard() throws Exception {
        assertEquals("Map<String, T extends String>", typed("Map", "String", extendsWildcard("T", "String")));
    }

    @Test
    public void testStringLiteral() throws Exception {
        assertEquals("\"hello\"", stringLiteral("hello"));
    }

    @Test
    public void testCharLiteral() throws Exception {
        assertEquals("'a'", charLiteral('a'));
    }

    @Test
    public void testInferedType() throws Exception {
        assertEquals("ArrayList<>", inferedType("ArrayList"));
    }

    @Test
    public void testLiteralArray() throws Exception {
        assertEquals("{1, 2, 3, 4}", literalArray(1, 2, 3, 4));
    }

    @Test
    public void testDeclare() throws Exception {
        assertEquals("Double x = 123.0", declare("Double", "x", 123.0D));
        assertEquals("Double x", declare("Double", "x"));
    }

    @Test
    public void testDeclareParameters() throws Exception {
        assertEquals("(String x, int y)", declareParameters(p("String", "x"), p("int", "y")));
    }

    @Test
    public void testCast() throws Exception {
        assertEquals(("(String) y"), cast("String", "y"));
    }

    @Test
    public void testBoolExpr() throws Exception {
        assertEquals(("x && y"), boolExpr("&&","x","y"));
    }

    @Test
    public void testAnd() throws Exception {
        assertEquals(("x && y"), and("x","y"));
    }

    @Test
    public void testOr() throws Exception {
        assertEquals(("x || y"), or("x","y"));
    }

    @Test
    public void testNotEq() throws Exception {
        assertEquals(("x != y"), notEq("x","y"));
    }

    @Test
    public void testNot() throws Exception {
        assertEquals(("!(value)"), not("value"));
    }

    @Test
    public void testReturnValue() throws Exception {
        assertEquals(("return value"), returnValue("value"));
    }

    @Test
    public void testReturnVoid() throws Exception {
        assertEquals(("return"), returnVoid());
    }

    @Test
    public void testCreateBreak() throws Exception {
        assertEquals(("break"), createBreak());
    }

    @Test
    public void testCreateContinue() throws Exception {
        assertEquals(("continue"), createContinue());
    }

    @Test
    public void testEq() throws Exception {
        assertEquals(("x == x"), eq("x","x"));
    }

    @Test
    public void testPlus() throws Exception {
        assertEquals(("x + x"), plus("x","x"));
    }

    @Test
    public void testMinus() throws Exception {
        assertEquals(("x - x"), minus("x","x"));
    }

    @Test
    public void testGt() throws Exception {
        assertEquals(("x > x"), gt("x","x"));
    }

    @Test
    public void testGte() throws Exception {
        assertEquals(("x >= x"), gte("x","x"));
    }

    @Test
    public void testLte() throws Exception {
        assertEquals(("x <= x"), lte("x","x"));
    }

    @Test
    public void testLt() throws Exception {
        assertEquals(("x < x"), lt("x","x"));
    }

    @Test
    public void testBitwiseAnd() throws Exception {
        assertEquals(("x & x"), bitwiseAnd("x","x"));
    }

    @Test
    public void testBitwiseOr() throws Exception {
        assertEquals(("x | x"), bitwiseOr("x","x"));
    }

    @Test
    public void testXor() throws Exception {
        assertEquals(("x ^ x"), xor("x","x"));
    }

    @Test
    public void testPostInc() throws Exception {
        assertEquals(("x++"), postInc("x"));
    }

    @Test
    public void testPreInc() throws Exception {
        assertEquals(("++x"), preInc("x"));
    }

    @Test
    public void testPostDec() throws Exception {
        assertEquals(("x--"), postDec("x"));
    }

    @Test
    public void testPreDec() throws Exception {
        assertEquals(("--x"), preDec("x"));
    }


}
