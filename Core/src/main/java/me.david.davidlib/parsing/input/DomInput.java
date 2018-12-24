package me.david.davidlib.parsing.input;

import java.io.InputStream;

public interface DomInput {

    byte[] getBytes();
    String getString();
    InputStream getStream();

    DomSourceType getType();

}
