package org.netcracker.learningcenter.reader;


import java.io.File;
import java.io.IOException;

public interface Reader {
    String read(File file) throws IOException;
}
