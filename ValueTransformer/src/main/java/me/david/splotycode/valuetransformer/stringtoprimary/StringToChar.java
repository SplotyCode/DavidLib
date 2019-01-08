package me.david.splotycode.valuetransformer.stringtoprimary;

import me.david.davidlib.runtime.transformer.TransformException;
import me.david.davidlib.runtime.transformer.ValueTransformer;

public class StringToChar extends ValueTransformer<String, Character> {

    @Override
    public Character transform(String input) throws TransformException {
        char[] chars = input.toCharArray();
        if (chars.length != 1) throw new TransformException("Need exactly one char");
        return chars[0];
    }

}
