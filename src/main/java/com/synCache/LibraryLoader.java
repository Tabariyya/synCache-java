package com.synCache;

import java.io.*;
import java.nio.file.Files;

public class LibraryLoader {

    public static String getLibraryPath() {
        try {
            String libPath = getLinkedLibraryPath();

            InputStream in = CacheEntry.class.getResourceAsStream(libPath);
            if (in == null) {
                throw new IllegalStateException("Library not found: " + libPath);
            }

            File tempFile = Files.createTempFile("libjavaSynCache", getLibExtension()).toFile();
            tempFile.deleteOnExit();

            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }

            return tempFile.getAbsolutePath();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load native library", e);
        }
    }

    private static String getLinkedLibraryPath() {
        String os = detectOS();
        String arch = detectArch();
        String extension = getLibExtension();

        return String.format("/lib/%s/%s/libjavaSynCache%s", os, arch, extension);
    }

    private static String detectOS() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) return "windows";
        if (osName.contains("mac")) return "macOS";
        if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) return "linux";
        throw new UnsupportedOperationException("Unsupported OS: " + osName);
    }

    private static String detectArch() {
        String arch = System.getProperty("os.arch").toLowerCase();
        if (arch.contains("arm") || arch.contains("aarch64")) return "arm";
        if (arch.contains("64")) return "x64";
        throw new UnsupportedOperationException("Unsupported architecture: " + arch);
    }

    private static String getLibExtension() {
        String os = detectOS();
        return switch (os) {
            case "windows" -> ".dll";
            case "macOS" -> ".dylib";
            case "linux" -> ".so";
            default -> throw new IllegalStateException("Unexpected OS: " + os);
        };
    }
}
