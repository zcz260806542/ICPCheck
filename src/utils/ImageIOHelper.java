package utils;

import java.awt.image.BufferedImage;  
import java.io.File;  
import java.io.IOException;  
import java.util.Iterator;  
import java.util.Locale;  
  
import javax.imageio.IIOImage;  
import javax.imageio.ImageIO;  
import javax.imageio.ImageReader;  
import javax.imageio.ImageWriteParam;  
import javax.imageio.ImageWriter;  
import javax.imageio.metadata.IIOMetadata;  
import javax.imageio.stream.ImageInputStream;  
import javax.imageio.stream.ImageOutputStream;  
  
import com.sun.media.imageio.plugins.tiff.TIFFImageWriteParam;  
  
public class ImageIOHelper {
    private Locale locale=Locale.CHINESE;
    /**
     * user set locale Construct
     * @param locale
     */
    public ImageIOHelper(Locale locale){
        this.locale=locale;
    }
    
    /**
     * default construct using default locale Locale.CHINESE
     */
    public ImageIOHelper(){
        
    }
    /**
     * create tempFile of Image in order to prevent damaging original file
     * @param imageFile
     * @param imageFormat like png,jps .etc
     * @return TempFile of Image
     * @throws IOException
     */
    public File createImage(File imageFile, String imageFormat) throws IOException {    
        Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName(imageFormat);    
        ImageReader reader = readers.next();    
        ImageInputStream iis = ImageIO.createImageInputStream(imageFile);    
        reader.setInput(iis);    
        IIOMetadata streamMetadata = reader.getStreamMetadata();    
        TIFFImageWriteParam tiffWriteParam = new TIFFImageWriteParam(Locale.CHINESE);    
        tiffWriteParam.setCompressionMode(ImageWriteParam.MODE_DISABLED);    
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("tiff");    
        ImageWriter writer = writers.next();    
        BufferedImage bi = reader.read(0);    
        IIOImage image = new IIOImage(bi,null,reader.getImageMetadata(0));    
        File tempFile = tempImageFile(imageFile);    
        ImageOutputStream ios = ImageIO.createImageOutputStream(tempFile);    
        writer.setOutput(ios);    
        writer.write(streamMetadata, image, tiffWriteParam);    
        ios.close();
        iis.close();
        writer.dispose();    
        reader.dispose();    
        return tempFile;    
    }    
    /**
     * add suffix to tempfile
     * @param imageFile
     * @return
     * @throws IOException 
     */
    private File tempImageFile(File imageFile) throws IOException {    
        String path = imageFile.getPath();    
        StringBuffer strB = new StringBuffer(path);    
        strB.insert(path.lastIndexOf('.'),"_text_recognize_temp");
        String s=strB.toString().replaceFirst("(?<=//.)(//w+)$", "tif");
       // Runtime.getRuntime().exec("attrib "+"\""+s+"\""+" +H");
        return new File(strB.toString()); 
    }    
    
}  