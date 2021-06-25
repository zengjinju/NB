package com.zjj.nb.biz.util;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private static final Logger logger = LoggerFactory.getLogger(IPUtils.class);
	public static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");

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
		InetAddress localAddress = null;

		try {
			localAddress = InetAddress.getLocalHost();
			if (isValidAddress(localAddress)) {
				return localAddress.getHostAddress();
			}
		} catch (Throwable var6) {
			logger.error("Failed to retriving ip address, " + var6.getMessage(), var6);
		}

		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			if (interfaces != null) {
				while(interfaces.hasMoreElements()) {
					try {
						NetworkInterface network = (NetworkInterface)interfaces.nextElement();
						Enumeration<InetAddress> addresses = network.getInetAddresses();
						if (addresses != null) {
							while(addresses.hasMoreElements()) {
								try {
									InetAddress address = (InetAddress)addresses.nextElement();
									if (isValidAddress(address)) {
										return address.getHostAddress();
									}
								} catch (Throwable var5) {
									logger.error("Failed to retriving ip address, " + var5.getMessage(), var5);
								}
							}
						}
					} catch (Throwable var7) {
						logger.error("Failed to retriving ip address, " + var7.getMessage(), var7);
					}
				}
			}
		} catch (Throwable var8) {
			logger.error("Failed to retriving ip address, " + var8.getMessage(), var8);
		}

		logger.error("Could not get local host ip address, will use 127.0.0.1 instead.");
		return localAddress.getHostAddress();
	}

	private static boolean isValidAddress(InetAddress address) {
		if (address != null && !address.isLoopbackAddress()) {
			String name = address.getHostAddress();
			return name != null && !"0.0.0.0".equals(name) && !"127.0.0.1".equals(name) && IP_PATTERN.matcher(name).matches();
		} else {
			return false;
		}
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

	public static void main(String[] vars){
		System.out.print(getHost());
	}
}
