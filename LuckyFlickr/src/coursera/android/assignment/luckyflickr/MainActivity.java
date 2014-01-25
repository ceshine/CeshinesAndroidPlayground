package coursera.android.assignment.luckyflickr;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class MainActivity extends Activity {
	
	private List<String> PhotoUrlList;
	WebView myWebView;
	Random rn = new Random();
	
	private class DownloadWebPageTask extends AsyncTask<String, Void, String> {
        
		@Override
        protected String doInBackground(String... urls) {
            String response = "";
            
            String url = urls[0];
            DefaultHttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            try {
                HttpResponse execute = client.execute(httpGet);
                InputStream content = execute.getEntity().getContent();

                BufferedReader buffer = new BufferedReader(
                        new InputStreamReader(content));
                String s = "";
                while ((s = buffer.readLine()) != null) {
                    response += s;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
        	PhotoUrlList = parse(result);
        	displayPhoto();
        }
    }
	
	private List<String> parse(String xml){
		String pattern = "<photo id=\"(\\d+)\" owner=\"\\S+\" secret=\"(\\w+)\" server=\"(\\d+)\" farm=\"(\\d+)\"";
		Matcher m =  Pattern.compile(pattern).matcher(xml);
		
		List<String> result = new ArrayList<String>();
		String url_mold = "http://farm%s.staticflickr.com/%s/%s_%s.jpg";
		while(m.find()){
			result.add(
				String.format(url_mold, m.group(4), m.group(3), m.group(1), m.group(2) ));
		}
		return result;
	}
	
	private void getPhotoList(String text){
		String url = String.format(
						"http://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=77bed304eee94df4fe96605c5f75a139&sort=interestingness-desc&text=%1$s"
						,text );
		DownloadWebPageTask task = new DownloadWebPageTask();
		task.execute(new String[] {url});	
	}
	
	private void displayPhoto(){
		
		if(PhotoUrlList == null || PhotoUrlList.size() == 0)
			return;
		String url = PhotoUrlList.get(rn.nextInt(PhotoUrlList.size()));
		
		//int height= this.getResources().getDisplayMetrics().heightPixels ;
		int height = (int)(myWebView.getMeasuredHeight() * 0.95);
		int width = (int)(myWebView.getMeasuredWidth() * 0.95);
		Log.i("LF", String.format("%d %d", height, PhotoUrlList.size()));		
		String data="<html><head><title>Example</title><meta name=\"viewport\"\"content=\"width="+width+", initial-scale=1 \" /></head>";
		data=data+"<body><center><img height=\""+height+"\" src=\""+url+"\" /></center></body></html>";
		
		myWebView.loadData(data, "text/html", null);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		myWebView = (WebView) findViewById(R.id.webView1);
		myWebView.getSettings().setBuiltInZoomControls(true);
		getPhotoList("beijing");
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void lucky(View v){
		displayPhoto();
	}
}
