package com.zjj.nb.biz.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.*;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jinju.zeng on 2017/6/12.
 */
@Slf4j
public class IPUtils {

	private static final String REGULAR_IPV4 = "^((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]|[0-9])\\.){3}(25[0-5]|2[0-4][0-9]|1[09][0-9]|[1-9][0-9]|[0-9])$";

	public static String getRemoteAddress(HttpServletRequest request) {
		if (request == null) {
			log.info("当前Request对象为空");
			return null;
		}

		/**
		 * 用来识别通过HTTP代理或负载均衡方式连接到Web服务器的客户端最原始的IP地址的HTTP请求头字段
		 */
		String ip = request.getHeader("x-forwarded-for");

		/**
		 *Proxy-Client-IP 字段和 WL-Proxy-Client-IP 字段只在 Apache（Weblogic Plug-In Enable）+WebLogic 搭配下出现，
		 * 其中“WL” 就是 WebLogic 的缩写,这两关只是起到兼容的目的
		 */
		if (isNull(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (isNull(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (isNull(ip)) {
			/**
			 * REMOTE_ADDR 是客户端跟服务器“握手”时的IP，但如果使用了“匿名代理”，REMOTE_ADDR 将显示代理服务器的ip，或者最后一个代理服务器的ip
			 */
			ip = request.getRemoteAddr();
		}
		if (ip.contains(",")) {
			ip = ip.split(",")[0];
		}
		log.info("当前请求的客户端的IP=" + ip);
		return isIpv4(ip) ? ip : "127.0.0.1";
	}

	private static Boolean isNull(String ip) {
		return StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip);
	}

	private static Boolean isIpv4(String ip) {
		Pattern pattern = Pattern.compile(REGULAR_IPV4);
		Matcher matcher = pattern.matcher(ip);
		return matcher.matches();
	}

	/**
	 * 获取外网IP
	 *
	 * @return
	 */
	public static String internetIp() {
		try {
			Enumeration<NetworkInterface> netWorks = NetworkInterface.getNetworkInterfaces();
			InetAddress inetAddress;
			Enumeration<InetAddress> inetAddresses = null;
			while (netWorks.hasMoreElements()) {
				inetAddresses = netWorks.nextElement().getInetAddresses();
				while (inetAddresses.hasMoreElements()) {
					inetAddress = inetAddresses.nextElement();
					if (inetAddress != null
							&& inetAddress instanceof Inet4Address
							&& !inetAddress.isSiteLocalAddress()
							&& !inetAddress.isLoopbackAddress()
							&& inetAddress.getHostAddress().indexOf(":") == -1) {
						return inetAddress.getHostAddress();
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取内网IP
	 *
	 * @return
	 */
	public static String intranetIp() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
		}
		return null;
	}

	public static String getHost() {
		String internetIp = internetIp();
		return internetIp == null ? intranetIp() : internetIp;
	}
}
