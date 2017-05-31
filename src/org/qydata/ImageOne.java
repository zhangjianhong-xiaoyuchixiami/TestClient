package org.qydata;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by jonhn on 2017/5/27.
 */
public class ImageOne {

    public static void setAlpha(String os) {
        try {
            ImageIcon e = new ImageIcon(os);
            Image image = e.getImage();
            Image smallImage = image.getScaledInstance(178, 220, 2);
            ImageIcon smallIcon = new ImageIcon(smallImage);
            BufferedImage bufferedImage = new BufferedImage(178, 220, 4);
            Graphics2D g2D = (Graphics2D)bufferedImage.getGraphics();
            g2D.drawImage(smallIcon.getImage(), 0, 0, smallIcon.getImageObserver());
            byte alpha = 100;

            for(int j1 = bufferedImage.getMinY(); j1 < bufferedImage.getHeight(); ++j1) {
                for(int j2 = bufferedImage.getMinX(); j2 < bufferedImage.getWidth(); ++j2) {
                    int pixel = bufferedImage.getRGB(j2, j1);
                    int[] rgb = new int[]{(pixel & 16711680) >> 16, (pixel & '\uff00') >> 8, pixel & 255};
                    pixel = alpha + 1 << 24 | pixel & 16777215;
                    bufferedImage.setRGB(j2, j1, pixel);
                }
            }

            g2D.drawImage(bufferedImage, 0, 0, e.getImageObserver());
            ImageIO.write(bufferedImage, "jpg", new File(os));
        } catch (Exception var12) {
            var12.printStackTrace();
        }

    }

}
