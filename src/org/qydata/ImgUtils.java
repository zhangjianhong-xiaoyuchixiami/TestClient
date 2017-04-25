package org.qydata;

import org.apache.commons.codec.binary.Base64;

import java.io.*;

public class ImgUtils {

	public static void baseStrToImageFile(String picturePath ,String certPicture,String pictureFileName){
		byte[] base64EncryptDataBytes = Base64.decodeBase64(certPicture);
		 InputStream in = new ByteArrayInputStream(base64EncryptDataBytes);
         File file=new File(picturePath,pictureFileName);//可以是任何图片格式.jpg,.png等
         FileOutputStream fos;
		try {
			fos = new FileOutputStream(file);
			byte[] b = new byte[1024];
	         int nRead = 0;
	         while ((nRead = in.read(b)) != -1) {
	             fos.write(b, 0, nRead);
	         }
	         fos.flush();
	         fos.close();
	         in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
