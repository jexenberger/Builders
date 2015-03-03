package org.github.builders.generator;

import org.junit.Test;

import javax.lang.model.element.Modifier;

import static org.github.builders.Pair.p;
import static org.github.builders.generator.Block.*;
import static org.github.builders.generator.Expressions.*;

/**
 * Created by julian3 on 15/03/03.
 */
public class BlockTest {


    @Test
    public void testIfBlock() throws Exception {

        Block ifBlock = Block.ifBlock(gt("hello", "world"), genericHandler());
        System.out.println(ifBlock.toString());

    }

    @Test
    public void testWhileBlock() throws Exception {

        Block block = Block.whileBlock(gt("hello", "world"), genericHandler());
        System.out.println(block.toString());

    }

    @Test
    public void testDoBlock() throws Exception {

        Block block = Block.doBlock(gt("hello", "world"), genericHandler());
        System.out.println(block.toString());

    }

    @Test
    public void testEmptyBlock() throws Exception {

        Block block = Block.emptyBlock(genericHandler());
        System.out.println(block.toString());

    }

    @Test
    public void testStaticBlock() throws Exception {

        Block block = Block.staticBlock(genericHandler());
        System.out.println(block.toString());

    }

    @Test
    public void testForEachBlock() throws Exception {

        Block block = Block.forEachBlock("String", "val", "vals", genericHandler());
        System.out.println(block.toString());

    }

    @Test
    public void testForBlock() throws Exception {

        Block block = Block.forBlock("int x=0", "x > 10", "x++", genericHandler());
        System.out.println(block.toString());

    }

    @Test
    public void testLambda() throws Exception {

        Block block = Block.lambda(genericHandler(), "x", "y");
        System.out.println(block.toString());

    }

    @Test
    public void testTryBlock() throws Exception {

        Block.CatchBlock catchBlock = catchBlock(genericHandler(), "NullPointerException", "IllegalStateException");
        Block.FinallyBlock finallyBlock = finallyBlock(genericHandler());
        Block block = Block.tryBlock(genericHandler(), finallyBlock, catchBlock, catchBlock(genericHandler(), "Exception"));
        System.out.println(block.toString());

        System.out.println("----");
        block = Block.tryBlock(genericHandler(), null, catchBlock(genericHandler(), "Exception"));
        System.out.println(block.toString());

        System.out.println("----");
        block = Block.tryBlock(genericHandler(), finallyBlock(genericHandler()));
        System.out.println(block.toString());

    }

    @Test
    public void testMethod() throws Exception {

        String result = method("doStuff", "String", genericHandler())
                .isSynchronized()
                .isStatic()
                .isNative()
                .isAbstract()
                .isFinal()
                .typeArgs("T", "K")
                .parameters(p("T", "a"), p("K", "b"))
                .exceptions("IOException", "FileNotFoundException").render();

        System.out.println(result);

        result = method("doStuff", "String", genericHandler())
                .render();
        System.out.println(result);

        result = method("doStuff", "String", genericHandler())
                .modifier(null)
                .render();
        System.out.println(result);
    }


    @Test
    public void testRenderClass() throws Exception {

        String render = classBlock("mypackage.test", "TestClass")
                .importClass("java.io.InputStream")
                .importClass("java.io.OutputStream")
                .importPackage("java.util")
                .importPackage("java.io")
                .staticClassImport("java.util.Collections")
                .staticImport("java.util.Collection.asList")
                .implementsInterface("java.io.Serializable")
                .extendsClass("Object")
                .field("String", "x")
                .field("String", "x1", stringLiteral("hello world"))
                .field(Modifier.PRIVATE, "int", "y")
                .field(Modifier.PUBLIC, "double", "a", 0.0)
                .method(method("doStuff", "String", genericHandler()))
                .method(method("otherThing", "void", genericHandler()))
                .isAbstract()
                .render();

        System.out.println(render);

    }

    private BlockHandler genericHandler() {
        return new BlockHandler() {
            @Override
            public void handleBlock(Block block) {
                block.add(declare("String", "aString", stringLiteral("hello world")));
            }
        };
    }
}
