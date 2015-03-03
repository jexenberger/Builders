package org.github.builders;

import org.github.builders.generator.StringUtils;
import org.junit.Test;

import static org.github.builders.generator.StringUtils.concat;
import static org.junit.Assert.assertEquals;

/**
 * Created by julian3 on 15/03/03.
 */
public class StringUtilsTest {


    @Test
    public void testJoin() throws Exception {

        String join = StringUtils.join(" ", "hello", 123);
        assertEquals(join, "hello 123");

        //concat no delimiter
        join = StringUtils.join(null, "hello", 123);
        assertEquals(join, "hello123");

    }


    public void testConcat() throws Exception {
        assertEquals("hello123", concat("hello",123));
    }

    @Test
    public void testWrap() throws Exception {

        String wrap = StringUtils.wrap("(","test", ")");
        assertEquals("(test)" ,wrap);

    }
}
