package coursera.android.assignment.a2_1;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;

public class Jabberwocky extends Activity {
	
	MediaPlayer chef;
	WebView myWebView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jabberwocky);
		myWebView = (WebView) findViewById(R.id.webView1);
		myWebView.getSettings().setBuiltInZoomControls(true);
		//myWebView.getSettings().setJavaScriptEnabled(true);
		//myWebView.getSettings().setDomStorageEnabled(true);	

		// Open asset/index.html
		myWebView.loadUrl("file:///android_asset/jabberwocky.html");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.jabberwocky, menu);
		return true;
	}

	@Override
	protected void onResume() {
		chef = MediaPlayer.create(this, R.raw.southpark);
		chef.setLooping(true);
		chef.start();
		super.onResume();
	}

	@Override
	protected void onPause() {
		chef.stop();
		chef.release();
		super.onPause();
	}
	
	public void openPic(View v) {
		myWebView.loadUrl("file:///android_asset/jabberwocky.jpg");
	}
	
	public void openWiki(View v){
		String url = "http://en.wikipedia.org/wiki/Jabberwocky";
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		startActivity(i);
	}
	
}
