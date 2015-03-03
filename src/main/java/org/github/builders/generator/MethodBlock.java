package org.github.builders.generator;

import org.github.builders.Pair;

import static org.github.builders.generator.Expressions.declareParameters;
import static org.github.builders.generator.Expressions.typeList;
import static org.github.builders.generator.StringUtils.concat;
import static org.github.builders.generator.StringUtils.join;

/**
 * Created by julian3 on 15/03/03.
 */
public class MethodBlock extends MemberBlock<MethodBlock> {

    String[] typeArgs = {};
    String name;
    boolean isStatic = false;
    boolean isSynchronized = false;
    boolean isAbstract = false;
    boolean isFinal = false;
    boolean isNative = false;
    String returnType;

    public MethodBlock(String name, String returnType, BlockHandler exceptionHandler) {
        super(name, exceptionHandler);
        assert name != null && returnType != null;
        this.name = name;
        this.returnType = returnType;
    }


    @Override
    protected String createDeclaration(String parameterList, String exceptionList) {
        String typeArgs = arrayNullOrEmpty(this.typeArgs) ? typeList(join(", ", this.typeArgs)) : "";
        String isStatic = (this.isStatic) ? "static" : "";
        String isFinal = (this.isFinal) ? "final" : "";
        String isAbstract = (this.isAbstract) ? "abstract" : "";
        String isNative = (this.isNative) ? "native" : "";
        String isSynchronized = (this.isSynchronized) ? "synchronized" : "";
        return join(" ",this.modifier, isStatic, isFinal, isAbstract, isNative, isSynchronized, typeArgs, this.returnType, concat(this.name, parameterList),exceptionList);
    }


    public MethodBlock typeArgs(String... typeArgs) {
        this.typeArgs = typeArgs;
        return this;
    }


    public MethodBlock isStatic(){
        this.isStatic = true;
        return this;
    }

    public MethodBlock isSynchronized(){
        this.isSynchronized = true;
        return this;
    }

    public MethodBlock isFinal(){
        this.isFinal = true;
        return this;
    }

    public MethodBlock isNative(){
        this.isNative = true;
        return this;
    }

    public MethodBlock isAbstract(){
        this.isAbstract = true;
        return this;
    }


}

