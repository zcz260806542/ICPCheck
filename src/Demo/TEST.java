package Demo;

public class TEST {
	public static void main(String[] args) {
		String test="id=\"1\" align=\"center\">										<td align=\"center\" class=\"by1\">1&nbsp;											</td>										<td align=\"center\" class=\"bxy\">同程网络科技股份有限公司&nbsp;</td>										<td align=\"center\" class=\"bxy\">企业&nbsp;</td>										<td align=\"center\" class=\"bxy\">&nbsp;苏ICP备09033604号-29</td>										<td align=\"center\" class=\"bxy\">同程旅游&nbsp;</td>										<td align=\"center\" class=\"bxy\"><div> <a href=\"http://www.ly.c";
		System.out.println(test.substring(test.indexOf("ICP")-1));
		test=test.substring(test.indexOf("ICP")-1);
		test=test.substring(0,test.indexOf("</td>"));
		System.out.println(test);
		
		
	}
}
