package org.github.builders.generator;

import static org.github.builders.generator.StringUtils.concat;
import static org.github.builders.generator.StringUtils.join;

/**
 * Created by julian3 on 15/03/03.
 */
public class ConstructorBlock extends MemberBlock<ConstructorBlock> {
    public ConstructorBlock(String declaration, BlockHandler handler) {
        super(declaration, handler);
    }

    @Override
    protected String createDeclaration(String parameterList, String exceptionList) {
        return join(" ", getModifier(),concat(this.declaration,parameterList),exceptionList);
    }


}
