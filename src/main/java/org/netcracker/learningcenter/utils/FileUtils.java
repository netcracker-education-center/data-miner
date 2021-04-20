package org.netcracker.learningcenter.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;


public class FileUtils {

    public static List<File> listFilesForFolder(String path) throws IOException {
        return Files.walk(Paths.get(path))
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .collect(Collectors.toList());
    }

    public static File createFile(String path, String filename) throws IOException {
        if (filename.endsWith(".txt")) {
            File file = new File(path, filename);
            file.createNewFile();
        } else if (filename.endsWith(".pdf")) {
            try (PDDocument document = new PDDocument()) {
                document.save(path + "/" + filename);
            }
        } else if (filename.endsWith(".docx")) {
            try (XWPFDocument document = new XWPFDocument();
                 FileOutputStream fos = new FileOutputStream(new File(path, filename))) {
                document.write(fos);
            }
        }
        return new File(path, filename);
    }
}
