package org.github.builders.generator;

import org.github.builders.Pair;

import javax.lang.model.element.Modifier;

import static org.github.builders.generator.Expressions.declareParameters;
import static org.github.builders.generator.Expressions.typeList;
import static org.github.builders.generator.StringUtils.concat;
import static org.github.builders.generator.StringUtils.join;

/**
 * Created by julian3 on 15/03/03.
 */
public abstract class MemberBlock<T extends MemberBlock> extends Block {
    Modifier modifier = Modifier.PUBLIC;
    String[] exceptions = {};
    Pair<String, String>[] parameters = new Pair[]{};

    public MemberBlock(String declaration, BlockHandler handler) {
        super(declaration, handler);
    }

    public MemberBlock(String declaration, String postDeclaration, BlockHandler handler) {
        super(declaration, postDeclaration, handler);
    }

    public T modifier(Modifier modifier) {
        this.modifier = modifier;
        return (T) this;
    }

    public T exceptions(String ... exceptions){
        this.exceptions = exceptions;
        return (T) this;
    }

    boolean arrayNullOrEmpty(Object[] array) {
        return array != null && array.length > 0;
    }

    public Modifier getModifier() {
        return modifier;
    }

    @Override
    public String render() {
        String parameterList = arrayNullOrEmpty(this.parameters) ? declareParameters(this.parameters) : declareParameters();
        String exceptionList = (arrayNullOrEmpty(this.exceptions)) ? join(" ", "throws", join(", ", this.exceptions)) : "";
        setDeclaration(createDeclaration(parameterList, exceptionList));
        return super.render();
    }

    protected abstract String createDeclaration(String parameterList, String exceptionList);

    public T parameters(Pair<String, String>... parameters) {
        this.parameters = parameters;
        return (T) this;
    }
}
