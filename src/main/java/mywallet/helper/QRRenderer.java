package mywallet.helper;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.EnumMap;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;

public class QRRenderer {
    private String content;
    private BufferedImage bufferedImage;
    private BitMatrix byteMatrix;

    public QRRenderer(String content) {
        setContent(content);
    }

    private void setContent(String content) {
        this.content = content;
        generateQRImage();
    }

    private void generateQRImage() {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        int width = 400;
        int height = 400;
        Map<EncodeHintType, Object> hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 2); /* default = 4 */

        try {
            byteMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            bufferedImage.createGraphics();

            Graphics2D graphics = (Graphics2D) bufferedImage.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, width, height);
            graphics.setColor(Color.BLACK);

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (byteMatrix.get(i, j)) {
                        graphics.fillRect(i, j, 1, 1);
                    }
                }
            }

        } catch (WriterException ex) {

        }
    }

    public void displayIn(ImageView imageView) {
        imageView.setImage(SwingFXUtils.toFXImage(bufferedImage, null));
    }

    public void saveAsFile(File file) throws IOException {
        MatrixToImageWriter.writeToPath(byteMatrix, "PNG", file.toPath());
    }
}