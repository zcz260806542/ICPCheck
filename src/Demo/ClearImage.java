package Demo;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.imageio.ImageIO;

import utils.CSVUtils;
import utils.MailBean;
import utils.OCRUtil;
import utils.SendMail;

public class ClearImage {

	static String DPath = "./";
	static String binaryBufferedImageName = "temp1.jpg";
	static String tempbufferedImageImageName = "temp2.jpg";
	static Float flagPx=2f;
    static String cookie= "__jsluid=8327d5cef5a9ca391a69beb8bc774aea; Hm_lvt_d7682ab43891c68a00de46e9ce5b76aa=1504581942,1504762898; Hm_lpvt_d7682ab43891c68a00de46e9ce5b76aa=1504763043; ";
    
    
    
    private static void getCookie() {
    	URLConnection connection;
		try {
			connection = new URL("http://www.miitbeian.gov.cn/icp/publish/query/icpMemoInfo_showPage.action").openConnection();
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) ");
	    	List<String> cookies = connection.getHeaderFields().get("Set-Cookie");
	    	String tmpString="__jsluid=8327d5cef5a9ca391a69beb8bc774aea; Hm_lvt_d7682ab43891c68a00de46e9ce5b76aa=1504581942,1504762898; Hm_lpvt_d7682ab43891c68a00de46e9ce5b76aa=1504763043; ";
	    	cookie=tmpString+cookies.get(1).substring(0,cookies.get(1).indexOf(';'));
		} catch (MalformedURLException e2) {
			//e2.printStackTrace();
		} catch (IOException e2) {
			//e2.printStackTrace();
		}
		
	}
    
    public static void main(String[] args) {
    	List<String> unrecognizedList=new ArrayList<String>();
    	List<String> errorList=new ArrayList<String>();
    	List<String> dataList=CSVUtils.importCsv(new File("123.csv"));
    	getCookie();
		//去重
		List<String> newList = new ArrayList(new TreeSet(dataList)); 
		for (String address : newList) {
			System.out.println(address);
			//System.out.println(address);
			for (int i = 0; i < 21; i++) {
				try {
					downloadPicture("http://www.miitbeian.gov.cn/getVerifyCode?"+i,i);
					String filepath = "temp.jpg";
					final String destDir = "";
					File file = new File(filepath);
					cleanImage(file, destDir);
					
			
					File file1 = new File("temp2.jpg");
					BufferedImage tempbufferedImage = ImageIO.read(file1);
					tempbufferedImage=clearNoise(tempbufferedImage,50,4,4); 
					ImageIO.write(tempbufferedImage, "jpg", new File(".", "test.jpg"));
				
				
				
					String valCode = new OCRUtil().recognizeText(new File(DPath + "test.jpg"), "jpg");
					valCode= valCode.replaceAll("[^\\w]|_","");
					//System.out.println(valCode);
					if(valCode.length()!=6)
					{
						Thread.sleep(3000);
						continue;
					}
					Thread.sleep(1500);
					String resString = yanzheng("http://www.miitbeian.gov.cn/common/validate/validCode.action?validateValue="+valCode);
					if(resString.equals("{\"result\":true}")){
						String result=sendPost("http://www.miitbeian.gov.cn/icp/publish/query/icpMemoInfo_searchExecute.action", MessageFormat.format("siteName=&siteDomain={0}&condition=2&siteUrl=&mainLicense=&siteIp=&unitName=&mainUnitNature=-1&certType=-1&mainUnitCertNo=&verifyCode={1}", address,valCode));
						//String r=getICP("http://www.miitbeian.gov.cn/icp/publish/query/icpMemoInfo_searchExecute.action?siteDomain=webpower-in1c.com&verifyCode="+valCode);
						//System.out.println(result);
						if(result.indexOf("id=\"1\"")<0){
							errorList.add(address);
						}else{
						}
						break;
					}else if(resString.equals("{}")){
						getCookie();
					}
					
					Thread.sleep(1500);
				} catch (IOException e) {
					//e.printStackTrace();
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e1) {
						//e1.printStackTrace();
					}
				} catch (Exception e) {
					//e.printStackTrace();
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e1) {
						//e1.printStackTrace();
					}
				} 
				
				if(i==20){
					unrecognizedList.add(address);
					break;
				}
			}
		}
		
		//发送邮件
		SendMail(unrecognizedList, errorList);
		
		
	}
    
    
    public static void SendMail(List<String> unrecognizedList,List<String> errorList){
        MailBean mb = new MailBean();
        mb.setHost("smtp.exmail.qq.com"); 
        mb.setUsername("email@webpowerchina.com"); 
        mb.setPassword("password");
        mb.setFrom("email@webpowerchina.com");
        mb.setTo("260806542@qq.com"); 
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        mb.setSubject("ICP filing inquiry "+df.format(new Date()));
        String content="以下域名未备案：\n";
        for (String string : errorList) {
        	//System.out.println(errorList);
        	content+=string+"\n";
		}
        
        content+="\n\n";
        content+="以下域名查询超时，请手动查询\n";
        for (String string : unrecognizedList) {
        	content+=string+"\n";
		}
        
        mb.setContent(content);

        SendMail sm = new SendMail();
        if(sm.sendMail(mb))
        {
            //System.out.println("发送成功!");
        }
        else{

            // System.out.println("发送失败!");
        }
    }
    
    
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            URLConnection conn = realUrl.openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) ");
            conn.addRequestProperty("Cookie", cookie);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());
            out.print(param);
            out.flush();
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }   
	
    
    private static String getICP(String urlStr) throws IOException {  
        URL url = null;  
        BufferedReader in = null;
        String result ="";
		url = new URL(urlStr);  
		URLConnection con = url.openConnection();  

		con.setRequestProperty("accept", "*/*");
        con.setRequestProperty("connection", "Keep-Alive");
		con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) ");
		con.addRequestProperty("Cookie", cookie);
		
		in = new BufferedReader(new InputStreamReader(
				con.getInputStream()));
        String line;
        while ((line = in.readLine()) != null) {
            result += line;
        }
		return result;
	}  
	
	private static String yanzheng(String urlStr) throws IOException {  
        URL url = null;  
        BufferedReader in = null;
        String result ="";
		url = new URL(urlStr);  
		URLConnection con = url.openConnection();  
		con.setRequestProperty("accept", "*/*");
        con.setRequestProperty("connection", "Keep-Alive");
		con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) ");
		con.addRequestProperty("Cookie", cookie);
		in = new BufferedReader(new InputStreamReader(
				con.getInputStream()));
        String line;
        while ((line = in.readLine()) != null) {
            result += line;
        }
		return result;
	}  
	
	
    private static void downloadPicture(String urlStr,int index) throws IOException {  
        URL url = null;  
		url = new URL(urlStr);  
		URLConnection con = url.openConnection();  
		con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) "+index);
		con.addRequestProperty("Cookie", cookie);
		DataInputStream dataInputStream = new DataInputStream(con.getInputStream());
		String imageName =  "temp.jpg";  
		FileOutputStream fileOutputStream = new FileOutputStream(new File(imageName));  
		byte[] buffer = new byte[1024];  
		int length;  
		while ((length = dataInputStream.read(buffer)) > 0) {  
			fileOutputStream.write(buffer, 0, length);  
		}  
		  
		dataInputStream.close();  
		fileOutputStream.close(); 
	}  
	
	public static BufferedImage clearNoise(BufferedImage bufferedImage,int G,int N,int Z) {
		for (int i = 0; i < Z; i++) {
			for (int j = 1; j < bufferedImage.getWidth()-1; j++) {
				for (int j2 = 1; j2 < bufferedImage.getHeight()-1; j2++) {
					Integer color=getPixel(bufferedImage,j,j2,G,N);
					if(color!=null){
						bufferedImage.setRGB(j, j2, color);
					}
				}
			}
		}
		return bufferedImage;
	}
	
	 
	
	public static Integer getPixel(BufferedImage bufferedImage,int x,int y,int G,int N){
		int L=bufferedImage.getRGB(x, y);
		
//		int alpha = (L >> 24) & 0xff;
//		int red = (L >> 16) & 0xff;
//		int green = (L >> 8) & 0xff;
		L = (L ) & 0xff;
		
		boolean flag =false;
		if(L>G){
			flag=true;
		}else{
			flag=false;
		}
		
		int nearDots=0;
	    if (flag == ((bufferedImage.getRGB(x - 1, y - 1) & 0xff) > G))
	        nearDots += 1;
	    if (flag == ((bufferedImage.getRGB(x - 1, y) & 0xff) > G))
	        nearDots += 1;
	    if (flag == ((bufferedImage.getRGB(x - 1, y + 1) & 0xff) > G))
	        nearDots += 1;
	    if (flag == ((bufferedImage.getRGB(x, y - 1) & 0xff) > G))
	        nearDots += 1;
	    if (flag == ((bufferedImage.getRGB(x, y + 1) & 0xff) > G))
	        nearDots += 1;
	    if (flag == ((bufferedImage.getRGB(x + 1, y - 1) & 0xff) > G))
	        nearDots += 1;
	    if (flag == ((bufferedImage.getRGB(x + 1, y) & 0xff) > G))
	        nearDots += 1;
	    if (flag == ((bufferedImage.getRGB(x + 1, y + 1) & 0xff) > G))
	        nearDots += 1;

	    if (nearDots < N)
	        return -1;
	    else
	        return null;
	}
	
	
	

	public static BufferedImage reline(BufferedImage curImg) {
		
		if (curImg != null) {
			int width = curImg.getWidth();
			int height = curImg.getHeight();
			int px = 3;
			Map<Integer, Integer> map = new HashMap<Integer, Integer>();
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					int argb = curImg.getRGB(x, y);
					int r = (int) (((argb >> 16) & 0xFF) * 1.1 + 30);
					int g = (int) (((argb >> 8) & 0xFF) * 1.1 + 30);
					int b = (int) (((argb >> 0) & 0xFF) * 1.1 + 30);
					int sum = r + g + b;
					if (!map.containsKey(sum)) {
						map.put(sum, 1);
					} else {
						int num = map.get(sum);
						map.remove(sum);
						map.put(sum, num + 1);
					}
				}
			}
			List<Integer> list = new ArrayList<Integer>();
			for (Integer in : map.keySet()) {
				Integer n = map.get(in);
				list.add(n);
			}
			Collections.sort(list);
			int num1 = 0;
			int num2 = 0;
			int num3 = 0;
			int num4 = 0;
			if (list.size() > 4) {
				num1 = list.get(list.size() - 5);
				num2 = list.get(list.size() - 4);
				num3 = list.get(list.size() - 3);
				num4 = list.get(list.size() - 2);
			}
			List<Integer> keylist = new ArrayList<Integer>();
			for (Integer key : map.keySet()) {
				if (map.get(key) == num1 || map.get(key) == num2 || map.get(key) == num3 || map.get(key) == num4) {
					keylist.add(key);
				}
			}
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					int argb = curImg.getRGB(x, y);
					int r = (int) (((argb >> 16) & 0xFF) * 1.1 + 30);
					int g = (int) (((argb >> 8) & 0xFF) * 1.1 + 30);
					int b = (int) (((argb >> 0) & 0xFF) * 1.1 + 30);
					int sum = r + g + b;
					int sum1 = 0;
					int sum2 = 0;
					int sum3 = 0;
					int sum4 = 0;
					int sum5 = 0;
					int sum6 = 0;
					boolean flag = true;
					for (int i = 1; i <= px && y + i < height && y - i > 0 && x - i > 0 && x + i < width; i++) {
						int upargb = curImg.getRGB(x, y - i);
						int endargb = curImg.getRGB(x, y + i);
						int rightupargb = curImg.getRGB(x + i, y + i);
						int leftupargb = curImg.getRGB(x - i, y + i);
						int leftdownargb = curImg.getRGB(x - i, y - i);
						int rightdownargb = curImg.getRGB(x + i, y - i);
						int r1 = (int) (((upargb >> 16) & 0xFF) * 1.1 + 30);
						int g1 = (int) (((upargb >> 8) & 0xFF) * 1.1 + 30);
						int b1 = (int) (((upargb >> 0) & 0xFF) * 1.1 + 30);
						sum1 = r1 + g1 + b1;
						int r2 = (int) (((endargb >> 16) & 0xFF) * 1.1 + 30);
						int g2 = (int) (((endargb >> 8) & 0xFF) * 1.1 + 30);
						int b2 = (int) (((endargb >> 0) & 0xFF) * 1.1 + 30);
						sum2 = r2 + g2 + b2;
						int r3 = (int) (((rightupargb >> 16) & 0xFF) * 1.1 + 30);
						int g3 = (int) (((rightupargb >> 8) & 0xFF) * 1.1 + 30);
						int b3 = (int) (((rightupargb >> 0) & 0xFF) * 1.1 + 30);
						sum3 = r3 + g3 + b3;
						int r4 = (int) (((leftupargb >> 16) & 0xFF) * 1.1 + 30);
						int g4 = (int) (((leftupargb >> 8) & 0xFF) * 1.1 + 30);
						int b4 = (int) (((leftupargb >> 0) & 0xFF) * 1.1 + 30);
						sum4 = r4 + g4 + b4;
						int r5 = (int) (((leftdownargb >> 16) & 0xFF) * 1.1 + 30);
						int g5 = (int) (((leftdownargb >> 8) & 0xFF) * 1.1 + 30);
						int b5 = (int) (((leftdownargb >> 0) & 0xFF) * 1.1 + 30);
						sum5 = r5 + g5 + b5;
						int r6 = (int) (((rightdownargb >> 16) & 0xFF) * 1.1 + 30);
						int g6 = (int) (((rightdownargb >> 8) & 0xFF) * 1.1 + 30);
						int b6 = (int) (((rightdownargb >> 0) & 0xFF) * 1.1 + 30);
						sum6 = r6 + g6 + b6;

						if (keylist.contains(sum1) || keylist.contains(sum2) || keylist.contains(sum3)
								|| keylist.contains(sum4) || keylist.contains(sum5) || keylist.contains(sum6)) {
							flag = false;
						}
					}
					if (!(keylist.contains(sum)) && flag) {
						curImg.setRGB(x, y, Color.white.getRGB());
					}
				}
			}

		}
		return curImg;
	}

	private static BufferedImage removeLine(BufferedImage img, Float px) {
		if (img != null) {
			int width = img.getWidth();
			int height = img.getHeight();

			for (int x = 0; x < width; x++) {
				List<Integer> list = new ArrayList<Integer>();
				for (int y = 0; y < height; y++) {
					int count = 0;
					while (y < height - 1 && isBlack(img.getRGB(x, y))) {
						count++;
						y++;
					}
					if ((float)count <= px && count > 0) {
						for (int i = 0; i <= count; i++) {
							list.add(y - i);
						}
					}
				}
				if (list.size() != 0) {
					for (int i = 0; i < list.size(); i++) {
						img.setRGB(x, list.get(i), Color.white.getRGB());
					}
				}
			}
		}
		return img;
	}

	public static void cleanImage(File sfile, String destDir) throws IOException {
		File destF = new File(destDir);
		if (!destF.exists()) {
			destF.mkdirs();
		}

		BufferedImage bufferedImage = ImageIO.read(sfile);

		int h = bufferedImage.getHeight();
		int w = bufferedImage.getWidth();

		int[][] gray = new int[w][h];
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				int argb = bufferedImage.getRGB(x, y);
				int r = (int) (((argb >> 16) & 0xFF) * 1.1 + 30);
				int g = (int) (((argb >> 8) & 0xFF) * 1.1 + 30);
				int b = (int) (((argb >> 0) & 0xFF) * 1.1 + 30);
				if (r >= 255) {
					r = 255;
				}
				if (g >= 255) {
					g = 255;
				}
				if (b >= 255) {
					b = 255;
				}
				gray[x][y] = (int) Math.pow(
						(Math.pow(r, 2.2) * 0.2973 + Math.pow(g, 2.2) * 0.6274 + Math.pow(b, 2.2) * 0.0753), 1 / 2.2);
			}
		}

		int threshold = ostu(gray, w, h);
		BufferedImage binaryBufferedImage = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_BINARY);
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				if (gray[x][y] > threshold) {
					gray[x][y] |= 0x00FFFF;
				} else {
					gray[x][y] &= 0xFF0000;
				}
				binaryBufferedImage.setRGB(x, y, gray[x][y]);
			}
		}

		// for (int y = 0; y < h; y++)
		// {
		// for (int x = 0; x < w; x++)
		// {
		// if (isBlack(binaryBufferedImage.getRGB(x, y)))
		// {
		// System.out.print("*");
		// } else
		// {
		// System.out.print(" ");
		// }
		// }
		// System.out.println();
		// }

		ImageIO.write(binaryBufferedImage, "jpg", new File(DPath, binaryBufferedImageName));

		String filepath = DPath + binaryBufferedImageName;
		File file = new File(filepath);
		BufferedImage tempbufferedImage = ImageIO.read(file);
		
		
		tempbufferedImage = reline(tempbufferedImage);
		tempbufferedImage = removeLine(tempbufferedImage, flagPx);
		ImageIO.write(tempbufferedImage, "jpg", new File(DPath, tempbufferedImageImageName));

	}

	public static boolean isBlack(int colorInt) {
		Color color = new Color(colorInt);
		if (color.getRed() + color.getGreen() + color.getBlue() <= 300) {
			return true;
		}
		return false;
	}

	public static boolean isWhite(int colorInt) {
		Color color = new Color(colorInt);
		if (color.getRed() + color.getGreen() + color.getBlue() > 300) {
			return true;
		}
		return false;
	}

	public static int isBlackOrWhite(int colorInt) {
		if (getColorBright(colorInt) < 30 || getColorBright(colorInt) > 730) {
			return 1;
		}
		return 0;
	}

	public static int getColorBright(int colorInt) {
		Color color = new Color(colorInt);
		return color.getRed() + color.getGreen() + color.getBlue();
	}

	public static int ostu(int[][] gray, int w, int h) {
		int[] histData = new int[w * h];
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				int red = 0xFF & gray[x][y];
				histData[red]++;
			}
		}

		int total = w * h;

		float sum = 0;
		for (int t = 0; t < 256; t++)
			sum += t * histData[t];

		float sumB = 0;
		int wB = 0;
		int wF = 0;

		float varMax = 0;
		int threshold = 0;

		for (int t = 0; t < 256; t++) {
			wB += histData[t]; // Weight Background
			if (wB == 0)
				continue;

			wF = total - wB; // Weight Foreground
			if (wF == 0)
				break;

			sumB += (float) (t * histData[t]);

			float mB = sumB / wB; // Mean Background
			float mF = (sum - sumB) / wF; // Mean Foreground

			// Calculate Between Class Variance
			float varBetween = (float) wB * (float) wF * (mB - mF) * (mB - mF);
			// Check if new maximum found
			if (varBetween > varMax) {
				varMax = varBetween;
				threshold = t;
			}
		}

		return threshold;
	}

}
