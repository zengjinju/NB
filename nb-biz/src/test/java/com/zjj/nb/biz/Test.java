package com.zjj.nb.biz;

import com.zjj.nb.biz.util.DateUtil;
import org.apache.commons.lang.math.NumberUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Created by admin on 2017/12/15.
 */
public class Test {

	public static void main(String[] args){
		try {
			java.awt.Desktop.getDesktop().browse(new URI("https://baidu.com/s?wd=java"));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
}
