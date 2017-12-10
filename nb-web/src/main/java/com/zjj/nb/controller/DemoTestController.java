package com.zjj.nb.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.zjj.configmanager.manager.HostConfig;
import com.zjj.nb.biz.kdtree.KdNodeFeature;
import com.zjj.nb.biz.kdtree.KdTree;
import com.zjj.nb.biz.manager.ehcache.ICacheProxy;
import com.zjj.nb.biz.manager.ehcache.TestCacheBean;
import com.zjj.nb.biz.manager.hystrix.UserCommand;
import com.zjj.nb.biz.manager.redis.LockCallBack;
import com.zjj.nb.biz.manager.redis.RedisLock;
import com.zjj.nb.biz.manager.threadPool.ThreadPool;
import com.zjj.nb.biz.mq.MQConsumer;
import com.zjj.nb.biz.mq.MqCallBack;
import com.zjj.nb.biz.service.UserService;
import com.zjj.nb.biz.util.IPUtils;
import com.zjj.nb.biz.util.applicationcontext.ApplicationContextHelper;
import com.zjj.nb.biz.util.applicationcontext.ApplicationContextUtil;
import com.zjj.nb.dao.entity.userDAO;
import com.zjj.nb.dao.mapper.userDAOMapper;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by jinju.zeng on 17/2/22.
 */
@Controller
@RequestMapping("demo")
public class DemoTestController {

	@Autowired
	private RedisLock redisLock;
	@Autowired
	private userDAOMapper userdaoMapper;
	@Autowired
	private MQConsumer mqConsumer;
	@Autowired
	private userDAOMapper userDAOMapper;
	@Autowired
	private ICacheProxy cacheProxy;

	private Boolean flag = false;
	private KdTree tree = null;
	private JSONArray array = new JSONArray();

	@PostConstruct
	public void init() {
		Random random = new Random();
		List<KdNodeFeature> list = new ArrayList<>();

		for (int i = 0; i < 3000; i++) {
			KdNodeFeature feature = new KdNodeFeature();
			double x = random.nextInt(100);
			double y = random.nextInt(100);
			double[] d = {x, y};
			feature.setHash_vector(d);
			list.add(feature);
			JSONObject obj = new JSONObject();
			obj.put("gender", "total");
			obj.put("height", x);
			obj.put("weight", y);
			array.add(obj);
		}
		Long start=System.currentTimeMillis();
		tree = KdTree.build(list);
		System.out.println("构建树花费时间："+(System.currentTimeMillis()-start));
		flag = true;
	}

	@RequestMapping("test")
	public void test() {
		//DemoTestController userService= (DemoTestController) ApplicationContextHelper.getInstance().getBean(this.getClass());
		Map<String, UserService> map = ApplicationContextHelper.getInstance().getBeanOfType(UserService.class);
		System.out.println(map);
		System.out.println(ApplicationContextUtil.getBean("userServiceImpl"));
	}

	@RequestMapping("lock")
	public void lockTest() {
		Object obj = redisLock.lock("zjj", new LockCallBack() {
			@Override
			public Object runTask() {
				return testTime();
			}
		});
	}

	public Integer testTime() {
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return 1;
	}

	@RequestMapping("insert")
	public void insert() {
		userDAO userdao = new userDAO();
		userdao.setUserName("zjj");
		userdao.setUserPassword("123456");
		userdao.setGmtCreate(DateTime.now().toDate());
		userdaoMapper.insertSelective(userdao);
	}

	@RequestMapping("get")
	@ResponseBody
	public Object get() {
		userDAO userdao = userdaoMapper.selectByNameAndPwd("abc", "123456");
		System.out.println(userdao);
		return userdao;
	}

	@RequestMapping(value = "post", method = RequestMethod.POST)
	@ResponseBody
	public Object post() {
		userDAO userdao = userdaoMapper.selectByNameAndPwd("abc", "123456");
		return userdao;
	}

	@RequestMapping("index")
	public String index(@RequestParam(value = "x",required = false)Double x, @RequestParam(value = "y",required = false) Double y,@RequestParam(value = "dis",required = false)Double dis, Model model) {
		model.addAttribute("x",x==null?0:x);
		model.addAttribute("y",y==null?0:y);
		model.addAttribute("dis",dis==null?0:dis);
		return "index";
	}

	@RequestMapping("data")
	@ResponseBody
	public JSONArray getData(@RequestParam("x")double x, @RequestParam("y") double y,@RequestParam("dis") double dis) {
		JSONArray array1 = new JSONArray();
		array1.addAll(array);
		KdNodeFeature feature = new KdNodeFeature();
		double[] d = {x, y};
		JSONObject obj = new JSONObject();
		obj.put("gender", "target");
		obj.put("height", d[0]);
		obj.put("weight", d[1]);
		array1.add(obj);
		feature.setHash_vector(d);
		if(dis!=0) {
			List<KdNodeFeature> featureList = tree.aroundFind(feature, dis);
			for (KdNodeFeature feature1 : featureList) {
				JSONObject obj1 = new JSONObject();
				obj1.put("gender", "result");
				obj1.put("height", feature1.getHash_vector()[0]);
				obj1.put("weight", feature1.getHash_vector()[1]);
				array1.add(obj1);
			}
		}else{
			KdNodeFeature feature1=tree.find(feature);
			JSONObject obj1 = new JSONObject();
			obj1.put("gender", "result");
			obj1.put("height", feature1.getHash_vector()[0]);
			obj1.put("weight", feature1.getHash_vector()[1]);
			array1.add(obj1);
		}
		return array1;
	}

	@RequestMapping("ip")
	public void testIp(HttpServletRequest request) {
		String ip = IPUtils.getRemoteAddress(request);
		System.out.println(ip);
	}

	@RequestMapping("mq")
	public void testMq() throws MQClientException {
		mqConsumer.register(new MqCallBack() {
			@Override
			public String getTags() {
				return "TagA";
			}

			@Override
			public void doCallBack() {
				userDAO userdao = userdaoMapper.selectByNameAndPwd("abc", "123456");
				System.out.println(JSON.toJSONString(userdao));
			}
		});
	}

	@RequestMapping("mq1")
	public void testMq1() throws MQClientException {
		mqConsumer.register(new MqCallBack() {
			@Override
			public String getTags() {
				return "TagB";
			}

			@Override
			public void doCallBack() {
				userDAO userdao = userdaoMapper.selectByNameAndPwd("abc", "123456");
				System.out.println(JSON.toJSONString(userdao));
			}
		});
	}

	@RequestMapping("ehcache")
	public void ehcache() {
		System.out.println(cacheProxy.getValue(TestCacheBean.CACHE_KET));
	}

	@RequestMapping("hystrix")
	public void hystrixTest() {
		userDAO userdao = new UserCommand("zjj", "123456").execute();
	}

	@RequestMapping("config")
	@ResponseBody
	public String getConfig() {
		while (true) {
			String value = HostConfig.get("redis.port", "");
			if (value != null && !"".equals(value)) {
				return value;
			}
			System.out.println(System.currentTimeMillis());
		}
	}

}
