package com.xueyu.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xueyu.constant.WxLoginContant;
import com.xueyu.dao.LoginMapper;
import com.xueyu.pojo.dto.Login;
import com.xueyu.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author durance
 */
@Slf4j
@Service
public class LoginServiceImpl extends ServiceImpl<LoginMapper, Login> implements LoginService {

	@Value("${file.qrcode}")
	private String filepath;

	/**
	 * IO流保存图片
	 *
	 * @param instreams 需要保存的流
	 * @param imagePath 保存的图片路径
	 * @param fileName  文件名
	 * @return 保存状态
	 */
	private static boolean uploadImages(InputStream instreams, String imagePath, String fileName) {
		File f = new File(imagePath);
		f.setWritable(true, false);
		boolean flag = false;
		try {
			// 1K的数据缓冲
			byte[] bs = new byte[1024];
			// 读取到的数据长度
			int len;
			// 输出的文件流
			File file = new File(imagePath, fileName);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
				try {
					// 创建新文件
					file.createNewFile();
				} catch (IOException e) {
					log.error("创建新文件时出现了错误");
					e.printStackTrace();
				}
			}
			OutputStream os = new FileOutputStream(imagePath + File.separator + fileName);
			// 开始读取
			while ((len = instreams.read(bs)) != -1) {
				os.write(bs, 0, len);
			}
			// 完毕，关闭所有链接
			os.close();
			instreams.close();
			flag = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return flag;
	}

	@Override
	public InputStream getQrcdoeInputStream() {
		// 1、创建文件名及scene值
		String scene = UUID.randomUUID().toString().substring(0, 8);
		// 2、获取token
		String accessToken = getAccessToken();
		log.info("accessToken = {}", accessToken);
		// 3、请求图片流
		return getwxacode(accessToken, scene);
	}

	/**
	 * 1. 带参数有限个数小程序码接口
	 * 2. @param url
	 * 3. @param access_token
	 * 4. @param path
	 * 5. @param width
	 * 6. @return
	 */
	private static InputStream getwxacode(String accessToken, String scene) {
		String url = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=" + accessToken;
		JSONObject jsonParam = new JSONObject();
		// 封装请求对象
		// scene值
		jsonParam.put("scene", scene);
		// 跳往的小程序页面，一般为认证界面
		jsonParam.put("page", WxLoginContant.authpage);
		// 图片宽度，默认为 430
		jsonParam.put("width", "430");
		// 检测页面是否存在，默认为 true
		jsonParam.put("check_path", true);
		// 返回请求结果
		return doWxPost(url, jsonParam);
	}

	@Override
	public String saveQrcodeFileAndData() {
		// 保存数据
		String fileName = saveQrcodeFile();
		// 截取无后缀的文件名，即scene值
		String scene = fileName.substring(0, 8);
		// 2、数据库增加该项数据
		Login login = new Login();
		login.setScene(scene);
		save(login);
		return scene;
	}

	@Override
	public String saveQrcodeFile() {
		// 1、创建文件名及scene值
		String scene = UUID.randomUUID().toString().substring(0, 8);
		// 2、获取token
		String accessToken = getAccessToken();
		log.info("accessToken = {}", accessToken);
		// 3、请求图片流
		InputStream inputStream = getwxacode(accessToken, scene);
		// 4、保存图标文件
		return saveToImgByInputStream(inputStream, scene);
	}

	/**
	 * 获取access_token
	 *
	 * @return access_token
	 */
	private String getAccessToken() {
		RestTemplate restTemplate = new RestTemplate();
		Map<String, String> params = new HashMap<>();
		params.put("APPID", WxLoginContant.appId);
		params.put("APPSECRET", WxLoginContant.secret);
		ResponseEntity<String> responseEntity = restTemplate.getForEntity(
				"https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={APPID}&secret={APPSECRET}",
				String.class, params);
		String body = responseEntity.getBody();
		JSONObject object = JSON.parseObject(body);
		return object.getString("access_token");
	}

	/**
	 * 通过流保存图片
	 *
	 * @param instreams 二进制流
	 * @param fileName  图片的名称
	 */
	private String saveToImgByInputStream(InputStream instreams, String fileName) {
		fileName = fileName + ".jpg";
		if (instreams != null) {
			boolean b = uploadImages(instreams, filepath, fileName);
		}
		return fileName;
	}

	/**
	 * 发送post请求
	 *
	 * @param url       请求地址
	 * @param jsonParam 请求参数
	 * @return 响应流
	 */
	private static InputStream doWxPost(String url, JSONObject jsonParam) {
		InputStream instreams = null;
		// 创建HttpPost对象
		HttpPost httpRequst = new HttpPost(url);
		try {
			StringEntity se = new StringEntity(jsonParam.toString(),"utf-8");
			se.setContentType("application/json");
			se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,"UTF-8"));
			httpRequst.setEntity(se);
			HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequst);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				HttpEntity httpEntity = httpResponse.getEntity();
				if (httpEntity != null) {
					instreams = httpEntity.getContent();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return instreams;
	}

}
