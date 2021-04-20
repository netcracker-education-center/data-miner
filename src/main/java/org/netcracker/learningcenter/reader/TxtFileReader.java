package org.netcracker.learningcenter.reader;

import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class TxtFileReader implements Reader {
    @Override
    public String read(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (FileInputStream fis = new FileInputStream(file);
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader br = new BufferedReader(isr)) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }
}
