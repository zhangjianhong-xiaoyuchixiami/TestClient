package org.qydata;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.FileOutputStream;

public final class WaterImg {
	public WaterImg() {

	}

	public static final void pressImage(String pressImg, String targetImg, String newImg, int x, int y) {
		try {
			File e = new File(targetImg);
			BufferedImage src = ImageIO.read(e);
			int wideth = src.getWidth((ImageObserver)null);
			int height = src.getHeight((ImageObserver)null);
			BufferedImage image = new BufferedImage(wideth, height, 1);
			Graphics2D g = image.createGraphics();
			g.drawImage(src, 0, 0, wideth, height, (ImageObserver)null);
			File _filebiao = new File(pressImg);
			BufferedImage src_biao = ImageIO.read(_filebiao);
			int wideth_biao = src_biao.getWidth((ImageObserver)null);
			int height_biao = src_biao.getHeight((ImageObserver)null);
			g.drawImage(src_biao, wideth - wideth_biao - x, height - height_biao - y, wideth_biao, height_biao, (ImageObserver)null);
			g.dispose();
			FileOutputStream out = new FileOutputStream(newImg);
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
			encoder.encode(image);
			out.close();
		} catch (Exception var17) {
			var17.printStackTrace();
		}

	}

	public static void pressText(String pressText, String newImg, String targetImg, String fontName, int fontStyle, Color color, int fontSize, int x, int y) {
		try {
			File e = new File(targetImg);
			BufferedImage src = ImageIO.read(e);
			int wideth = src.getWidth((ImageObserver)null);
			int height = src.getHeight((ImageObserver)null);
			BufferedImage image = new BufferedImage(wideth, height, 1);
			Graphics2D g = image.createGraphics();
			g.drawImage(src, 0, 0, wideth, height, (ImageObserver)null);
			g.setColor(color);
			g.setFont(new Font(fontName, fontStyle, fontSize));
			g.drawString(pressText, wideth - fontSize - x, height - fontSize / 2 - y);
			g.dispose();
			FileOutputStream out = new FileOutputStream(newImg);
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
			encoder.encode(image);
			out.close();
		} catch (Exception var17) {
			System.out.println(var17);
		}

	}

}