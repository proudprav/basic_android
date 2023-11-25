package com.example.myapplication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class ZipManager {
    private static int BUFFER_SIZE = 6 * 1024;

    public static void zip(String file, String zipFile) throws IOException {

        BufferedInputStream origin = null;
        ZipOutputStream out = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            out = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(Paths.get(zipFile))));
        }
        try {
            byte[] data = new byte[BUFFER_SIZE];

                FileInputStream fi = new FileInputStream(file);
                origin = new BufferedInputStream(fi, BUFFER_SIZE);
                try {
                    ZipEntry entry = new ZipEntry(file.substring(file.lastIndexOf("/") + 1));
                    assert out != null;
                    out.putNextEntry(entry);
                    int count;
                    while ((count = origin.read(data, 0, BUFFER_SIZE)) != -1) {
                        out.write(data, 0, count);
                    }
                } finally {
                    origin.close();
                }

        } finally {
            out.close();
        }
    }
}