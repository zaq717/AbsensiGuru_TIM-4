/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package absensiguru.dao;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.nio.file.FileSystems;
import java.nio.file.Path;

/**
 *
 * @author rahma
 */
public class QRGenerator {

    public static void generateQRCode(String text, String destPath) throws Exception {
    int width = 300;
    int height = 300;

    BitMatrix matrix = new MultiFormatWriter()
            .encode(text, BarcodeFormat.QR_CODE, width, height);

    Path path = FileSystems.getDefault().getPath(destPath);

    MatrixToImageWriter.writeToPath(matrix, "PNG", path);
}


}
