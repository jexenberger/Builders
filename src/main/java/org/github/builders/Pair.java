package org.github.builders;

import static org.github.builders.generator.StringUtils.join;

/**
 * Created by julian3 on 15/03/03.
 */
public class Pair<T, K> {

    T left;
    K right;

    public Pair(T left, K right) {
        this.left = left;
        this.right = right;
    }

    public  static <T,K> Pair p(T left, K right) {
        return new Pair(left, right);
    }

    public T getLeft() {
        return left;
    }

    public K getRight() {
        return right;
    }

    public String spaceSeparated() {
        return join(" ",left,right);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
