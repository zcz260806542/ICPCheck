package Demo;

public class TEST {
	public static void main(String[] args) {
		String test="id=\"1\" align=\"center\">										<td align=\"center\" class=\"by1\">1&nbsp;											</td>										<td align=\"center\" class=\"bxy\">ͬ������Ƽ��ɷ����޹�˾&nbsp;</td>										<td align=\"center\" class=\"bxy\">��ҵ&nbsp;</td>										<td align=\"center\" class=\"bxy\">&nbsp;��ICP��09033604��-29</td>										<td align=\"center\" class=\"bxy\">ͬ������&nbsp;</td>										<td align=\"center\" class=\"bxy\"><div> <a href=\"http://www.ly.c";
		System.out.println(test.substring(test.indexOf("ICP")-1));
		test=test.substring(test.indexOf("ICP")-1);
		test=test.substring(0,test.indexOf("</td>"));
		System.out.println(test);
		
		
	}
}
