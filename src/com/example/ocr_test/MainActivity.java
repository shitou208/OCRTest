package com.example.ocr_test;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.googlecode.tesseract.android.TessBaseAPI;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	private String result;
	private TextView res_txt;
	private Bitmap bmp;
	private static final String CLIENT_ID = "gg8SgQH3H9TRjM9tPL79kS4c";
	private static final String TESSBASE_PATH = "/mnt/sdcard/tesseract/";
	private static final String DEFAULT_LANGUAGE = "eng";
	private String tran_result;
	private String eng_result;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		res_txt = (TextView) findViewById(R.id.result_txt);
		Button tran_btn = (Button) findViewById(R.id.translate);
		bmp = BitmapFactory
				.decodeResource(getResources(), R.drawable.ocr_test2);
		th1.start();

		tran_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
	
				th2.start();
			}
		});
	}

	public Handler handle2 = new Handler() {

		public void handleMessage(Message msg) {
			res_txt.setText(result);
		}

	};
	Thread th1 = new Thread() {
		public void run() {
			eng_result = doOcr(bmp);
			result = eng_result;
			handle2.sendEmptyMessage(0);
		}
	};
	Thread th2 = new Thread() {
		public void run() {
			result = translate(eng_result);
			handle2.sendEmptyMessage(0);
		}
	};

	public String doOcr(Bitmap bitmap) {
		TessBaseAPI baseApi = new TessBaseAPI();

		// TESSBASE_PATH = "/mnt/sdcard/tesseract/";
		baseApi.init(TESSBASE_PATH, DEFAULT_LANGUAGE);

		// ����Ӵ��У�tests-twoҪ��BMP����Ϊ������
		bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

		baseApi.setImage(bitmap);

		String text = baseApi.getUTF8Text();

		baseApi.clear();
		baseApi.end();

		return text;
	}

	public String translate(String english) {

		String q = english;
		/*
		 * String s1="&q="+q; String s2="&from=auto&to=auto"; String
		 * url="http://openapi.baidu.com/public/2.0/bmt/translate?client_id=" +
		 * CLIENT_ID+s1+s2; //String url=json_read
		 * "http://openapi.baidu.com/public/2.0/bmt/translate?client_id=gg8SgQH3H9TRjM9tPL79kS4c&q=today is a best day&from=auto&to=auto"
		 * ;
		 * 
		 * url= url.replaceAll(" ", "%20");//Url中无法识别空格，所以会报错，因此，将空格进行替换
		 * //第一步，创建HttpGet对象 HttpGet httpGet = new HttpGet(url);
		 * //第二步，使用execute方法发送HTTP GET请求，并返回HttpResponse对象 HttpResponse
		 * httpResponse; try { httpResponse = new
		 * DefaultHttpClient().execute(httpGet); if
		 * (httpResponse.getStatusLine().getStatusCode() == 200) {
		 * //第三步，使用getEntity方法获得返回结果 tran_result =
		 * EntityUtils.toString(httpResponse.getEntity());
		 * 
		 * } } catch (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */

		// 使用Post的方法，避免出现非法Url
		String url="http://openapi.baidu.com/public/2.0/bmt/translate";
		/* 建立HTTP Post连线 */
		HttpPost httpRequest = new HttpPost(url);
		// Post运作传送变数必须用NameValuePair[]阵列储存
		// 传参数 服务端获取的方法为request.getParameter("name")
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("from", "en"));
		params.add(new BasicNameValuePair("to", "zh"));
		params.add(new BasicNameValuePair("client_id", CLIENT_ID));
		params.add(new BasicNameValuePair("q", q));

		try {

			// 发出HTTP request
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			// 取得HTTP response
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(httpRequest);

			// 若状态码为200 ok
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				// 取出回应字串
				tran_result = EntityUtils.toString(httpResponse
						.getEntity());
				tran_result=json_read(tran_result);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return tran_result;
	}

	public String json_read(String src) {

		
		try {  
		 //   JSONTokener jsonParser = new JSONTokener(src);  
		    // 此时还未读取任何json文本，直接读取就是一个JSONObject对象。  
		    // 如果此时的读取位置在"name" : 了，那么nextValue就是"yuanzhifei89"（String）  
		  // JSONObject person = (JSONObject) jsonParser.nextValue();  
		    // 接下来的就是JSON对象的操作了  
		    
		    JSONArray jsonObjs = new JSONObject(src).getJSONArray("trans_result"); 
            String s = ""; 
            for(int i = 0; i < jsonObjs.length() ; i++){ 
                JSONObject jsonObj = ((JSONObject)jsonObjs.opt(i)) ;
                String text = jsonObj.getString("dst");
                s +=  text + "\n" ; 
            } 
    tran_result=s;
		} catch (JSONException ex) {  
		    // 异常处理代码  
		}  

		return tran_result;
	}

}
