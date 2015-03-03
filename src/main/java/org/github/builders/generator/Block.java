package org.github.builders.generator;

import java.util.ArrayList;
import java.util.Collection;

import static org.github.builders.generator.Expressions.*;
import static org.github.builders.generator.StringUtils.*;

/**
 * Created by julian3 on 15/03/03.
 */
public abstract class Block {

    String declaration;
    String postDeclaration;
    Collection<Object> blocks;
    int offset;
    boolean useTabs = true;
    BlockHandler handler;


    public Block(String declaration, BlockHandler handler) {
        this.declaration = declaration;
        this.handler = handler;
        this.blocks = new ArrayList<Object>();
    }


    public Block(String declaration, String postDeclaration, BlockHandler handler) {
        this(declaration, handler);
        this.postDeclaration = postDeclaration;
    }

    protected void setOffset(int offset) {
        this.offset = offset;
    }


    void setHandler(BlockHandler handler) {
        this.handler = handler;
    }

    void setDeclaration(String declaration) {
        this.declaration = declaration;
    }

    public Block add(Object value) {
        if (blocks == null) {
            blocks = new ArrayList<Object>();
        }
        blocks.add(value);
        return this;
    }

    public Block body(BlockHandler handler) {
        this.handler = handler;
        return this;
    }

    public Block add(Block block) {
        block.setOffset(offset + 1);
        blocks.add(block);
        return this;
    }

    public Block add(String statement) {
        blocks.add(wrap(statement));
        return this;
    }

    static String wrap(String expression) {
        if (expression == null || expression.trim().equals("")) {
            return "";
        }
        return concat(expression, ";\n");
    }

    public String render() {
        StringBuilder builder = new StringBuilder(concat(tabsOrSpace(), join(" ", declaration, "{\n")));
        if (this.handler != null) {
            this.handler.handleBlock(this);
        }
        this.offset++;
        for (Object block : blocks) {
            if (block instanceof Block) {
                renderBlock((Block) block, builder);
            } else {
                renderSection(block, builder);
            }
        }
        String endStatement = (postDeclaration != null) ? wrap(parens(postDeclaration)) : "";
        this.offset--;
        builder.append(tabsOrSpace()).append("}").append(endStatement).append("\n");
        return builder.toString();

    }

    void renderBlock(Block block, StringBuilder builder) {
        builder.append("\n");
        builder.append(tabsOrSpace()).append(block.toString());
    }

    void renderSection(Object block, StringBuilder builder) {
        builder.append(tabsOrSpace()).append(block.toString());
    }

    public String toString() {
        return render();
    }

    private String tabsOrSpace() {
        return (useTabs) ? tabs() : spaces();
    }

    private String tabs() {
        return chars('\t');
    }

    private String spaces() {
        return chars(' ');
    }

    private String chars(char theChar) {
        String str = "";
        for (int i = 0; i < offset; i++) {
            str += theChar;

        }
        return str;
    }

    public static Block ifBlock(String statement, BlockHandler handler) {
        return new Block(join(" ", "if", parens(statement)), handler) {
        };
    }

    public static Block emptyBlock(BlockHandler handler) {
        return new Block(null, handler) {
        };
    }

    public static Block staticBlock(BlockHandler handler) {
        return new Block("static", handler) {
        };
    }

    public static Block whileBlock(String statement, BlockHandler handler) {
        return new Block(join(" ", "while", parens(statement)), handler) {
        };
    }

    public static Block doBlock(String statement, BlockHandler handler) {
        return new Block("do", join(" ", "while", statement), handler) {
        };
    }


    public static Block forEachBlock(String type, String name, String collection, BlockHandler handler) {
        return new Block(join(" ", "for", parens(join(" ", declare(type, name), ":", collection))), handler) {
        };
    }

    public static Block forBlock(String declaration, String condition, String iterationExpression, BlockHandler handler) {
        return new Block(join(" ", "for", parens(join("; ", getBlank(declaration), condition, getBlank(iterationExpression)))), handler) {
        };
    }

    public static Block lambda(BlockHandler handler, String... declaration) {
        return new Block(join(" ", parameterList(declaration), "=>"), handler) {
        };
    }


    public static MethodBlock method(String name, String returnType, BlockHandler handler) {
        return new MethodBlock(name, returnType, handler);
    }


    public static Block tryBlock(BlockHandler handler, final FinallyBlock finallyBlock, final CatchBlock... catchBlocks) {
        assert catchBlocks.length > 0 || finallyBlock != null;

        return new Block("try", handler) {

            @Override
            public String render() {
                String result = super.render().trim();
                for (CatchBlock catchBlock : catchBlocks) {
                    result = join(" ", result, catchBlock.render().trim());
                }
                if (finallyBlock != null) {
                    result = join(" ", result, finallyBlock.render().trim());
                }
                return result;
            }
        };
    }

    public static CatchBlock catchBlock(BlockHandler handler, String... exceptions) {
        return new CatchBlock(handler, exceptions);
    }

    public static FinallyBlock finallyBlock(BlockHandler handler) {
        return new FinallyBlock(handler);
    }

    public static ClassBlock classBlock(String packageName, String name) {
        return new ClassBlock(packageName, name);
    }

    public static ConstructorBlock constructorBlock(String typeName, BlockHandler handler) {
        return new ConstructorBlock(typeName, handler);
    }

    public static class CatchBlock extends Block {

        public CatchBlock(BlockHandler exceptionHandler, String... exceptions) {
            super(join(" ", "catch", parens(join(" ", join(" | ", exceptions), "e"))), exceptionHandler);
        }
    }


    public static class FinallyBlock extends Block {

        public FinallyBlock(BlockHandler exceptionHandler) {
            super("finally", exceptionHandler);
        }
    }


}
