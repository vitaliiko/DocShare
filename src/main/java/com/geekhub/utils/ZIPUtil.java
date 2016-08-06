package com.geekhub.utils;

import com.geekhub.entities.UserDocument;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZIPUtil {

    private static byte[] buffer = new byte[1024];

    public static byte[] createZIP(List<UserDocument> documents) {
        try (ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
             ZipOutputStream zipOut = new ZipOutputStream(byteArrayOut)) {

            zipOut.setLevel(ZipOutputStream.STORED);
            for (UserDocument doc : documents) {
                zipOut.putNextEntry(new ZipEntry(doc.getName()));
                File file = new File(UserFileUtil.ROOT_LOCATION + doc.getHashName() + UserFileUtil.SYSTEM_EXTENSION);
                write(new FileInputStream(file), zipOut);
                zipOut.closeEntry();
            }

            byteArrayOut.flush();
            zipOut.flush();
            byteArrayOut.close();
            zipOut.close();

            return byteArrayOut.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void write(InputStream in, OutputStream out) throws IOException {
        int len;
        while ((len = in.read(buffer)) >= 0) {
            out.write(buffer, 0, len);
        }
        in.close();
    }
}
