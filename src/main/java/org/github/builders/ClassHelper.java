package org.github.builders;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Created by julian3 on 15/03/03.
 */
public class ClassHelper {


    public static void declareMethod(BufferedWriter writer, String modifier, String returnType, String name, Pair... args) throws IOException{
        writer.newLine();
        writer.append('\t').append(modifier).append(' ').append(returnType).append(' ').append(name).append('(');
        for (Pair arg : args) {
            writer.append(arg.getLeft().toString());
            writer.append(' ');
            writer.append("){");
        }

    }

    public static void endBlock(BufferedWriter writer, int offset) throws IOException {
        for (int i = 0; i < offset ; i++) {
            writer.write('\t');

        }
        writer.write('}');
    }



}
