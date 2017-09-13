package utils;


import java.io.BufferedReader;    
import java.io.File;    
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;    
import java.util.ArrayList;    
import java.util.List;
import java.util.Locale;

import org.jdesktop.swingx.util.OS;    

/**
 * TEXT Recognize Utils
 * @author ink.Flower
 *
 */
public class OCRUtil { 
    private final String LANG_OPTION = "-l";
    private final String EOL = System.getProperty("line.separator");    
    private String tessPath = "C:\\Program Files\\Tesseract-OCR";
    private String transname="eng";
    
    /**
     * Construct method of OCR ,set Tesseract-OCR install path
     * @param tessPath Tesseract-OCR install path
     * @param transFileName traningFile name like eng.traineddata
     */
    public OCRUtil(String tessPath,String transFileName){
        this.tessPath=tessPath;
        this.transname=transFileName;
    }
    /**
     * Construct method of OCR,default path is "C://Program Files (x86)//Tesseract-OCR"
     */
    public OCRUtil(){     }
    
    public String getTessPath() {
        return tessPath;
    }
    public void setTessPath(String tessPath) {
        this.tessPath = tessPath;
    }
    public String getTransname() {
        return transname;
    }
    public void setTransname(String transname) {
        this.transname = transname;
    }
    public String getLANG_OPTION() {
        return LANG_OPTION;
    }
    public String getEOL() {
        return EOL;
    }
    
    /**
     * recognize text in image
     * @param imageFile
     * @param imageFormat
     * @return text recognized in image
     * @throws Exception
     */
    public String recognizeText(File imageFile,String imageFormat)throws Exception{    
        File tempImage = new ImageIOHelper().createImage(imageFile,imageFormat);    
        return ocrImages(tempImage, imageFile);   
    }    
    
    /**
     * recognize text in image
     * @param imageFile
     * @param imageFormat
     * @param locale
     * @return text recognized in image
     * @throws Exception
     */
    public String recognizeText(File imageFile,String imageFormat,Locale locale)throws Exception{    
        File tempImage = new ImageIOHelper(locale).createImage(imageFile,imageFormat);
        return ocrImages(tempImage, imageFile);
           
    }
    /**
     * 
     * @param tempImage
     * @param imageFile
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    private String ocrImages(File tempImage,File imageFile) throws IOException, InterruptedException{
        File outputFile = new File(imageFile.getParentFile(),"output");
        //Runtime.getRuntime().exec("attrib "+"\""+outputFile.getAbsolutePath()+"\""+" +H");
        StringBuffer strB = new StringBuffer();    
        List<String> cmd = new ArrayList<String>();    
        if(OS.isWindowsXP()){    
            cmd.add(tessPath+"//tesseract");    
        }else if(OS.isLinux()){    
            cmd.add("tesseract");    
        }else{    
            cmd.add(tessPath+"//tesseract");    
        }    
        cmd.add("");    
        cmd.add(outputFile.getName());    
        cmd.add(LANG_OPTION);    
        cmd.add(transname);    
        ProcessBuilder pb = new ProcessBuilder();    
        pb.directory(imageFile.getParentFile());    
        cmd.set(1, tempImage.getName());    
        pb.command(cmd);    
        pb.redirectErrorStream(true);    
        Process process = pb.start();    
        int w = process.waitFor();    
        tempImage.delete();
        if(w==0){    
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(outputFile.getAbsolutePath()+".txt"),"UTF-8"));    
            String str;    
            while((str = in.readLine())!=null){    
                strB.append(str).append(EOL);    
            }    
            in.close();    
        }else{    
            String msg;    
            switch(w){    
            case 1:    
                msg = "Errors accessing files.There may be spaces in your image's filename.";    
                break;    
            case 29:    
                msg = "Cannot recongnize the image or its selected region.";    
                break;    
            case 31:    
                msg = "Unsupported image format.";    
                break;    
            default:    
                msg = "Errors occurred.";    
            }    
            tempImage.delete();    
            throw new RuntimeException(msg);    
        }    
        new File(outputFile.getAbsolutePath()+".txt").delete();    
        return strB.toString(); 
    }
}    