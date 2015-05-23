package com.mylogin.tutwlan_auto_login;

import java.util.Timer;
import java.util.TimerTask;


import android.os.Bundle;
import android.os.SystemClock;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	static SharedPreferences SP;
	boolean timeout = false;
	static WebView tut;
	static String tut_url="https://netar2.imc.tut.ac.jp/upload/custom/tutwlan_cap/tutwlan-login.html";
	Timer timer;
	int 読み込み時間=0;
	boolean 読み込み中=false;
	@SuppressLint("SetJavaScriptEnabled") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//	WebViewの設定
		tut = (WebView)findViewById(R.id.webView1);
		tut.getSettings().setJavaScriptEnabled(true);
		tut.setWebViewClient(new WebViewClient()
		{
			@Override
		    public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				読み込み中=true;
		    }

	        @Override
	        public void onPageFinished(WebView view, String url) {
	            super.onPageFinished(view, url);
	            読み込み中=false;
	            timeout=false;
	            long eventTime = SystemClock.uptimeMillis();
	            dispatchKeyEvent(new KeyEvent( eventTime, eventTime, KeyEvent.ACTION_MULTIPLE , KeyEvent.KEYCODE_TAB, 1, 0 ));

	        }

	        @Override
	        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
	        	Log.e("onRecieve","error");
				if(tut_url.equals("https://netar2.imc.tut.ac.jp/upload/custom/tutwlan_cap/tutwlan-login.html"))
					tut_url="https://netar1.imc.tut.ac.jp/upload/custom/tutwlan_cap/tutwlan-login.html";
				else tut_url="https://www.google.co.jp/";
				
				((EditText)findViewById(R.id.EditText_URL)).setText(tut_url);
				tut.stopLoading();
				//sleep(10);
				tut.loadUrl(tut_url);
	        }
		});

		//tut.loadUrl(((EditText)findViewById(R.id.EditText_URL)).getText().toString());
	    
		//SharedPreferenceの準備
	    SP=getSharedPreferences("dictionary_sp",Context.MODE_PRIVATE);
	    
  
	}

	 @Override
	  public void onResume() {
		 super.onResume();
		 辞書を読み込み();
		 tut.loadUrl(((EditText) findViewById(R.id.EditText_URL)).getText().toString());
	    	startTimer(this);//タイマ割り込み処理
	 }
	@Override
	public void onPause(){
    	super.onPause();
    	辞書をコミット();
    	finish();
	}
	public void OnClick_実行(View view)
	{
		実行コマンド();
	}

	public void OnClick_更新(View view)
	{
		更新コマンド();
	}
	public void OnClick_tut1(View view)
	{
		tut_url="https://netar1.imc.tut.ac.jp/upload/custom/tutwlan_cap/tutwlan-login.html";
		((EditText)findViewById(R.id.EditText_URL)).setText(tut_url);
		更新コマンド();
	}
	public void OnClick_tut2(View view)
	{
		tut_url="https://netar2.imc.tut.ac.jp/upload/custom/tutwlan_cap/tutwlan-login.html";
		((EditText)findViewById(R.id.EditText_URL)).setText(tut_url);
		更新コマンド();
	}

	void 更新コマンド()
	{
		Log.d("button","更新");
		tut.loadUrl(((EditText)findViewById(R.id.EditText_URL)).getText().toString());
	}
	void 実行コマンド()
	{
		String ID=((EditText)findViewById(R.id.EditText_ID)).getText().toString();
		String Pass=((EditText)findViewById(R.id.EditText_Pass)).getText().toString();
		Log.d("button","実行");
		
		Keycode.Stringからキーイベント発行(this,ID);
		long eventTime = SystemClock.uptimeMillis();
		dispatchKeyEvent(new KeyEvent( eventTime, eventTime, KeyEvent.ACTION_DOWN , KeyEvent.KEYCODE_TAB, 1, 0 ));
		Keycode.Stringからキーイベント発行(this,Pass);
		eventTime = SystemClock.uptimeMillis();
		dispatchKeyEvent(new KeyEvent( eventTime, eventTime, KeyEvent.ACTION_DOWN , KeyEvent.KEYCODE_TAB, 1, 0 ));
		eventTime = SystemClock.uptimeMillis();
		dispatchKeyEvent(new KeyEvent( eventTime, eventTime, KeyEvent.ACTION_DOWN , KeyEvent.KEYCODE_ENTER, 1, 0 ));
	}
	@SuppressLint("NewApi") void 辞書をコミット(){
		//String URL=((EditText)findViewById(R.id.EditText_URL)).getText().toString();
		String ID=((EditText)findViewById(R.id.EditText_ID)).getText().toString();
		String Pass=((EditText)findViewById(R.id.EditText_Pass)).getText().toString();
		
		SharedPreferences.Editor editor=SP.edit();
		//editor.putString("URL",URL);
		editor.putString("ID",ID);
		editor.putString("Pass",Pass);
		
		editor.apply();
	}
	
	void 辞書を読み込み(){
		//String URL=SP.getString("URL","");
		String ID=SP.getString("ID","");//存在しない時は空文字が帰ってくる
		String Pass=SP.getString("Pass","");
		//if(URL.equals(""))URL=tut_url;
		//Log.d("辞書を読み込み",URL);
		//((EditText)findViewById(R.id.EditText_URL)).setText(URL);
		((EditText)findViewById(R.id.EditText_ID)).setText(ID);
		((EditText)findViewById(R.id.EditText_Pass)).setText(Pass);
	}
    public void startTimer(final MainActivity mv){
    	
    	if(timer!=null)timer.cancel();
    	timer=new Timer();
    	final android.os.Handler handler=new android.os.Handler();
    	timer.schedule(new TimerTask(){
    		int timecount=0;//10msおきに数える

			@Override
			public void run() {
				// TODO Auto-generated method stub
				handler.post(new Runnable(){

					public void run() {
						// TODO Auto-generated method stub
						if(timecount==40000)timecount=0;
						else timecount++;
						if(読み込み中)読み込み時間++;
						else 読み込み時間=0;
						
						if(読み込み時間>250){
							読み込み時間=0;
							
							if(tut_url.equals("https://netar2.imc.tut.ac.jp/upload/custom/tutwlan_cap/tutwlan-login.html"))
									tut_url="https://netar1.imc.tut.ac.jp/upload/custom/tutwlan_cap/tutwlan-login.html";
							else tut_url="https://www.google.co.jp/";
							
							((EditText)findViewById(R.id.EditText_URL)).setText(tut_url);
							tut.stopLoading();
							//sleep(10);
							tut.loadUrl(tut_url);
						}

							
						
					}
				});
			}
    	},0,10);//10ms
    }
    public synchronized  void sleep(long msec)
    {	
    	try
    	{
    		wait(msec);
    	}catch(InterruptedException e){}
    }
}
