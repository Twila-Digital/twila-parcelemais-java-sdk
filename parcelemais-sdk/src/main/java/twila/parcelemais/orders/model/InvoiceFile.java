package twila.parcelemais.orders.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

public final class InvoiceFile {

    private final String fileName;
    private final String base64Content;

    private InvoiceFile(String fileName, String base64Content) {
        this.fileName = fileName;
        this.base64Content = base64Content;
    }

    public String getFileName() {
        return fileName;
    }

    public String getBase64Content() {
        return base64Content;
    }

    public static InvoiceFile fromBytes(byte[] content, String fileName) {
        return new InvoiceFile(fileName, Base64.getEncoder().encodeToString(content));
    }

    public static InvoiceFile fromStream(InputStream content, String fileName) {
        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            byte[] chunk = new byte[8192];
            int read;
            while ((read = content.read(chunk)) != -1)
                buffer.write(chunk, 0, read);

            return fromBytes(buffer.toByteArray(), fileName);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public static InvoiceFile fromFile(Path path) {
        try {
            return fromBytes(Files.readAllBytes(path), path.getFileName().toString());
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }
}
