package com.u1tramarinet.imageclustering;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {
    private FileUtils() {
    }

    public static void save(File file, String contentUrl) {
        try (ReadableByteChannel readableByteChannel = Channels.newChannel(new URL(contentUrl).openStream())) {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save(File file, Image contentImage) {
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(contentImage, null), getExtensionName(file.getAbsolutePath()), file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String createTempFileUrl(String originalFileUrl) {
        int lastPeriodIndex = originalFileUrl.lastIndexOf(".");
        String tempFileUrl = originalFileUrl.substring(0, lastPeriodIndex) + ".temp" + originalFileUrl.substring(lastPeriodIndex);

        Path originalFilePath;
        Path tempFilePath;
        try {
            originalFilePath = Paths.get(new URI(originalFileUrl));
            tempFilePath = Paths.get(new URI(tempFileUrl));
            if (tempFilePath.toFile().exists()) {
                Files.delete(tempFilePath);
            }
            Files.copy(originalFilePath, tempFilePath);
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
            return null;
        }
        return tempFileUrl;
    }

    public static void deleteFile(String fileUrl) {
        if (fileUrl == null) return;
        try {
            Files.delete(Path.of(new URI(fileUrl)));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static String getExtensionName(String fileName) {
        if (fileName == null) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}
