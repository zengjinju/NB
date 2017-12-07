package com.zjj.nb.biz.transfer;

import com.zjj.nb.biz.util.DateUtil;
import sun.misc.BASE64Decoder;

import java.io.IOException;
import java.util.Date;

/**
 * Created by admin on 2017/11/23.
 */
public class TestUtils {

	public static String titleXml(BaseDTO baseDO, String content) {
		StringBuilder builder = new StringBuilder();
		Date date = new Date();
		builder.append("<?xml version=\"1.0\" encoding = \"GBK\"?>")
				.append("<CMS>")
				.append("<eb>");
		builder.append("<pub>")
				.append("<TransCode>").append(baseDO.getTransCode()).append("</TransCode>")
				.append("<CIS>").append(baseDO.getCis()).append("</CIS>")
				.append("<BankCode>").append(baseDO.getBankCode()).append("</BankCode>")
				.append("<ID>").append(baseDO.getId()).append("</ID>")
				.append("<TranDate>").append(DateUtil.parseDateToString(date,"yyyyMMdd")).append("</TranDate>")
				.append("<TranTime>").append(DateUtil.parseDateToString(date,"HHmmssSSS")).append("</TranTime>")
				.append("<fSeqno>").append(baseDO.getPackageId()).append("</fSeqno>")
				.append("</pub>");
		builder.append(content);
		builder.append("</eb>").append("</CMS>");
		return builder.toString();
	}

	public static String getBankList(BaseDTO baseDTO,String bankCode,String nextTag,String reserve){
		StringBuilder builder=new StringBuilder();
		builder.append("<in>")
				.append("<BnkCode>").append(bankCode).append("</BnkCode>")
				.append("<NextTag>").append(nextTag).append("</NextTag>")
				.append("<ReqReserved1>").append(reserve).append("</ReqReserved1>")
				.append("<ReqReserved2>").append(reserve).append("</ReqReserved2>")
				.append("</in>");
		return titleXml(baseDTO,builder.toString());
	}

	public static String unEncryptionForBase64(String str) {
		int index = str.indexOf("=");
		if (str == null || str.length() == 0) {
			return null;
		}
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			return new String(decoder.decodeBuffer(str.substring(index + 1)), "GB2312");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
