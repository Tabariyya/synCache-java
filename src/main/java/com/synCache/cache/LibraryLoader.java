package com.synCache.cache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class LibraryLoader {
    protected static String getLibraryPath() {
        try {

            String libPath = getLinkedLibraryPath();

            // Open as stream
            InputStream in = NativeCacheEntry.class.getResourceAsStream(libPath);

            // Create temp file
            File temp = Files.createTempFile("libjavaSynCache", ".so").toFile();
            temp.deleteOnExit();

            // Copy bytes to temp file
            try (FileOutputStream out = new FileOutputStream(temp)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            System.out.println(temp.getAbsolutePath());
            return temp.getAbsolutePath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getLinkedLibraryPath() {
        return "/lib/linux/arm/libjavaSynCache.so";

    }

}
