package org.netcracker.learningcenter.reader;

import org.netcracker.learningcenter.exceptions.ReaderNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReaderFactory {
    private TxtFileReader txtFileReader;
    private PdfFileReader pdfFileReader;
    private DocxFileReader docxFileReader;

    @Autowired
    public ReaderFactory(TxtFileReader txtFileReader, PdfFileReader pdfFileReader, DocxFileReader docxFileReader) {
        this.txtFileReader = txtFileReader;
        this.pdfFileReader = pdfFileReader;
        this.docxFileReader = docxFileReader;
    }

    public Reader getReader(String filename) throws ReaderNotFoundException {
        if (filename.endsWith(".txt")) {
            return txtFileReader;
        } else if (filename.endsWith(".pdf")) {
            return pdfFileReader;
        } else if (filename.endsWith(".docx")) {
            return docxFileReader;
        } else {
            throw new ReaderNotFoundException("No reader found for file " + filename + ".Unsupported extension.");
        }
    }
}
