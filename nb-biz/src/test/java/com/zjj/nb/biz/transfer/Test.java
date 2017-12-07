package com.zjj.nb.biz.transfer;

import com.zjj.nb.biz.util.DateUtil;
import com.zjj.nb.biz.util.http.HttpUtil;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by admin on 2017/11/23.
 */
public class Test {
	private static Connection connection=null;
	private static PreparedStatement psState=null;
	private static final String sql="INSERT INTO nb_bank_type (paySys_bnkCode, bank_name, bank_alias_code, parent_bank_name,area_code) VALUES(?, ?, ?, ?,?);";
	private static final String url="jdbc:mysql://localhost:3306/nb?characterEncoding=utf-8&amp;zeroDateTimeBehavior=convertToNull&allowMultiQueries=true";
	private static final String driver="com.mysql.jdbc.Driver";
	private static final String username="root";
	private static final String password="123456";
	private static String[] codes={"302","308","402"};
	private static String[] codes1={"201","202","203","301","303","304","305","306","307","309","310"};
	private static String[] codes2={"102","103","104","105"};
	private static String[] code3={"313","314","402"};
	private static ExecutorService executorService= Executors.newFixedThreadPool(5);
	private static AtomicInteger auto=new AtomicInteger(1000000);
	static{
		try {
			Class.forName(driver);
			connection= DriverManager.getConnection(url,username,password);
			psState=connection.prepareStatement(sql);
			connection.setAutoCommit(false);
		}catch (Exception e){

		}
	}

	private static String nextTag="";
	public static void main(String[] args) throws SQLException {
		BaseDTO baseDTO=new BaseDTO();
		baseDTO.setBankCode("102");
		baseDTO.setCis("120290001066991");
		baseDTO.setId("201701.y.1202");
		String packageId= DateUtil.parseDateToString(new Date(),"yyyyMMddHHmmsss");
		baseDTO.setPackageId(packageId);
		baseDTO.setTranDate(new Date());
		baseDTO.setTranTime(new Date());
		baseDTO.setTransCode("DIBPSBC");
		List<RdDTO> totalList=new ArrayList<>();
		CountDownLatch countDownLatch=new CountDownLatch(code3.length);
		for(int i=0;i<code3.length;i++){
			executorService.execute(new TaskRunnable(totalList,baseDTO,countDownLatch,code3[i]));
		}

		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
//		System.out.println(totalList);
		insertInto(totalList);
	}

	public static void doTask(List<RdDTO> list,BaseDTO baseDTO,ThreadLocal<String> local,int count,Boolean islast,String bankCode){
		do{
			String packageId="";
			String xml="";
			synchronized (Test.auto) {
				packageId=DateUtil.parseDateToString(new Date(),"yyyyMMdd");
				packageId = packageId + String.valueOf(Test.auto.getAndIncrement());
				baseDTO.setPackageId(packageId);
				xml=TestUtils.getBankList(baseDTO,bankCode,local.get(),"");
			}
//			System.out.println("请求参数："+xml);
			Map<String,String> map=new HashMap<>();
			map.put("Version", "0.0.0.1");
			map.put("BankCode", baseDTO.getBankCode());
			map.put("GroupCIS", baseDTO.getCis());
			map.put("ID", baseDTO.getId());
			map.put("Cert", "");
			map.put("TransCode", baseDTO.getTransCode());
			map.put("PackageID", packageId);
			map.put("reqData", xml);
			String url = "http://172.16.20.140:448"+ "?userID=201701.y.1202"+ "&PackageID=" + packageId + "&SendTime=" + DateUtil.parseDateToString(new Date(),"yyyyMMddHHmmssSSS");
			System.out.println(url);
			String result=HttpUtil.post(url,map,null,"GB2312");
			result=TestUtils.unEncryptionForBase64(result);
			System.out.println(result);
			List<RdDTO> list1=parsexml2RdDTO(result,local);
			list.addAll(list1);
			if(!islast&&list.size()>=count){
				break;
			}

		}while (!"".equals(local.get()));
	}

	public static List<RdDTO> parsexml2RdDTO(String xml,ThreadLocal<String> local) {
		RdDTO rdDTO = null;
		List<RdDTO> list=new ArrayList<>();
		try {
			Document doc = DocumentHelper.parseText(xml);
			//获取根节点
			Element root = doc.getRootElement();
			listNodes(root,list,local);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return list;
	}

	private static String listNodes(Element root,List<RdDTO> list,ThreadLocal<String> local) {
		Iterator iterator = root.elementIterator();
		while (iterator.hasNext()) {
			Element element = (Element) iterator.next();
			if("RetCode".equals(element.getName())){
				String value=element.getText();
				if(!"0".equals(value)){
					local.set("");
				}
			}
			if("NextTag".equals(element.getName())){
				String nextTag=element.getText();
				local.set(nextTag);
			}
			if ("rd".equals(element.getName())) {
				RdDTO rdDo = new RdDTO();
				Iterator childIterator = element.elementIterator("PaySysBnkCode");
				Element childElement = (Element) childIterator.next();
				String bankCode=childElement.getText();
				rdDo.setPaySysBnkCode(bankCode);
				if(bankCode!=null){
					rdDo.setAreaCode(bankCode.substring(3,7));
				}
				childIterator = element.elementIterator("BnkName");
				childElement = (Element) childIterator.next();
				rdDo.setBnkName(childElement.getText());
				childIterator = element.elementIterator("RepReserved4");
				childElement = (Element) childIterator.next();
				String[] values=childElement.getText().split("\\|");
				if(values!=null&&values.length>=2) {
					rdDo.setCode(values[0]);
					rdDo.setParentName(values[1]);
				}
				list.add(rdDo);
			}
			listNodes(element,list,local);
		}
		return null;
	}

	public static void insertInto(List<RdDTO> list) throws SQLException {
		Long start=System.currentTimeMillis();
		for(RdDTO rdDTO : list){
			psState.setString(1,rdDTO.getPaySysBnkCode());
			psState.setString(2,rdDTO.getBnkName());
			psState.setString(3,rdDTO.getCode());
			psState.setString(4,rdDTO.getParentName());
			psState.setString(5,rdDTO.getAreaCode());
			psState.addBatch();
		}
		psState.executeBatch();
		connection.commit();
		System.out.println((System.currentTimeMillis()-start)/1000);
	}

	private static class TaskRunnable implements Runnable{
		//默认5个线程分段执行
		private CountDownLatch countDownLatch=new CountDownLatch(5);
		private CountDownLatch countDown;
		private List<RdDTO> list;
		private BaseDTO baseDTO;
		private String bankCode;
		public TaskRunnable(List<RdDTO> list,BaseDTO baseDTO,CountDownLatch countDownLatch,String bankCode){
			this.list=list;
			this.baseDTO=baseDTO;
			this.countDown=countDownLatch;
			this.bankCode=bankCode;
		}
		@Override
		public void run() {
			Thread thread1=new Thread(new Runnable() {
				@Override
				public void run() {
					String nextTag="";
					List<RdDTO> totalList=new ArrayList<>();
					ThreadLocal<String> local=new ThreadLocal<String>(){
						@Override
						protected String initialValue() {
							return "";
						}
					};
					int times=0;
					while(times<=3) {
						try {
							Test.doTask(totalList, baseDTO, local, 3000, false, bankCode);
							synchronized (list) {
								list.addAll(totalList);
								countDownLatch.countDown();
								System.out.println("当前线程" + Thread.currentThread().getName() + ":" + local.get());
								break;
							}
						} catch (Exception e) {
							times++;
							if (times >= 3) {
								countDownLatch.countDown();
								break;
							}
						}
					}
				}
			});

			Thread thread2=new Thread(new Runnable() {
				@Override
				public void run() {
					String nextTag="";
					List<RdDTO> totalList=new ArrayList<>();
					ThreadLocal<String> local=new ThreadLocal<String>(){
						@Override
						protected String initialValue() {
							return "3000";
						}
					};
					int times=0;
					while(times<=3) {
						try {
							Test.doTask(totalList, baseDTO, local, 3000, false, bankCode);
							synchronized (list) {
								list.addAll(totalList);
								countDownLatch.countDown();
								System.out.println("当前线程" + Thread.currentThread().getName() + ":" + local.get());
								break;
							}
						} catch (Exception e) {
							times++;
							if (times >= 3) {
								countDownLatch.countDown();
								break;
							}
						}
					}
				}
			});

			Thread thread3=new Thread(new Runnable() {
				@Override
				public void run() {
					List<RdDTO> totalList=new ArrayList<>();
					ThreadLocal<String> local=new ThreadLocal<String>(){
						@Override
						protected String initialValue() {
							return "6000";
						}
					};
					int times=0;
					while(times<=3) {
						try {
							Test.doTask(totalList, baseDTO, local, 3000, false, bankCode);
							synchronized (list) {
								list.addAll(totalList);
								countDownLatch.countDown();
								System.out.println("当前线程" + Thread.currentThread().getName() + ":" + local.get());
								break;
							}
						} catch (Exception e) {
							times++;
							if (times >= 3) {
								countDownLatch.countDown();
								break;
							}
						}
					}
				}
			});

			Thread thread4=new Thread(new Runnable() {
				@Override
				public void run() {
					List<RdDTO> totalList=new ArrayList<>();
					ThreadLocal<String> local=new ThreadLocal<String>(){
						@Override
						protected String initialValue() {
							return "9000";
						}
					};
					int times=0;
					while(times<=3) {
						try {
							Test.doTask(totalList, baseDTO, local, 3000, false, bankCode);
							synchronized (list) {
								list.addAll(totalList);
								countDownLatch.countDown();
								System.out.println("当前线程" + Thread.currentThread().getName() + ":" + local.get());
								break;
							}
						} catch (Exception e) {
							times++;
							if (times >= 3) {
								countDownLatch.countDown();
								break;
							}
						}
					}
				}
			});

			Thread thread5=new Thread(new Runnable() {
				@Override
				public void run() {
					List<RdDTO> totalList=new ArrayList<>();
					ThreadLocal<String> local=new ThreadLocal<String>(){
						@Override
						protected String initialValue() {
							return "12000";
						}
					};
					int times=0;
					while(times<=3) {
						try {
							Test.doTask(totalList, baseDTO, local, 3000, true, bankCode);
							synchronized (list) {
								list.addAll(totalList);
								countDownLatch.countDown();
								System.out.println("当前线程" + Thread.currentThread().getName() + ":" + local.get());
								break;
							}
						} catch (Exception e) {
							times++;
							if (times >= 3) {
								countDownLatch.countDown();
								break;
							}
						}
					}
				}
			});

			thread1.start();
			thread2.start();
			thread3.start();
			thread4.start();
			thread5.start();
			try {
				countDownLatch.await();
				countDown.countDown();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

}
