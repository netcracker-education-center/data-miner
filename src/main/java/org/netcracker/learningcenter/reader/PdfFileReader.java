package org.netcracker.learningcenter.reader;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class PdfFileReader implements Reader {
    @Override
    public String read(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (PDDocument document = PDDocument.load(file)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String[] text = stripper.getText(document).split("\\r\\n");
            for (String line : text) {
                sb.append(line);
            }
        }
        return sb.toString();
    }
}
