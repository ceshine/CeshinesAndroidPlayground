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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class MainActivity extends Activity {
	
	private static final String KEYWORD_FIELD = "keyword";
	private List<String> PhotoUrlList;
	private ImageView imView;
	private Random rn = new Random();
	private int idx;
	private String keyword;
	private SharedPreferences pref;
	
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
        	lucky(null);
        }
    }
	
	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		@Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap bmImg;
            String url = urls[0];
            DefaultHttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            try {
                HttpResponse execute = client.execute(httpGet);
                InputStream content = execute.getEntity().getContent();

                bmImg = BitmapFactory.decodeStream(content);
                return bmImg;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
		
		@Override
		protected void onPostExecute(Bitmap result){
			displayPhoto(result);
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
	
	private void displayPhoto(Bitmap photo){
		Log.i("lucky", "Displaying the photo...");
		imView.setImageBitmap(photo);
		getWindow().setTitle(String.format("%s - %s - %s", getResources().getString(R.string.app_name) , keyword,  idx));
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		pref = getPreferences(MODE_PRIVATE);
		keyword = pref.getString(KEYWORD_FIELD, "Fun");
		Log.i("LF", keyword);
		imView = (ImageView) findViewById(R.id.imageView1);
		getPhotoList(keyword);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.action_setKeyword:
	        	// get keyword_dialog.xml view
				LayoutInflater li = LayoutInflater.from(this);
				View promptsView = li.inflate(R.layout.keyword_dialog, null);
 
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
 
				// set keyword_dialog.xml to alertdialog builder
				alertDialogBuilder.setView(promptsView);
 
				final EditText userInput = (EditText) promptsView
						.findViewById(R.id.editTextDialogUserInput);
 
				// set dialog message
				alertDialogBuilder
					.setPositiveButton(R.string.ok,
					  new DialogInterface.OnClickListener() {
					    public void onClick(DialogInterface dialog,int id) {				 
							keyword = userInput.getText().toString();
							//update preferences
							pref.edit().putString(KEYWORD_FIELD, keyword).commit();
							getPhotoList(keyword);
					    }
					  })
					.setNegativeButton(R.string.cancel,
					  new DialogInterface.OnClickListener() {
					    public void onClick(DialogInterface dialog,int id) {
					    	dialog.cancel();
					    }
					  });
 
				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();
 
				// show it
				alertDialog.show();
				return super.onOptionsItemSelected(item);
 	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	public void lucky(View v){
		if(PhotoUrlList == null || PhotoUrlList.size() == 0)
			return;
		idx = rn.nextInt(PhotoUrlList.size());
		String url = PhotoUrlList.get(idx);
		
		DownloadImageTask task = new DownloadImageTask();
		task.execute(new String[] {url});
	}
}
