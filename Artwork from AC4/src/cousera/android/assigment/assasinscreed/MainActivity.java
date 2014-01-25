package cousera.android.assigment.assasinscreed;

import java.util.Random;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.content.res.Configuration;
import android.widget.ImageView;


public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ImageView view = (ImageView)findViewById(R.id.imageView1);
		Random rn = new Random();
		int i = rn.nextInt(5) + 1;
		if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
			String uri = "drawable/landscape_" +  Integer.toString(i);
			view.setImageDrawable(getResources().getDrawable(getResources().getIdentifier(uri, null, getPackageName())));
		}else{
			String uri = "drawable/portrait_" +  Integer.toString(i);
			view.setImageDrawable(getResources().getDrawable(getResources().getIdentifier(uri, null, getPackageName())));
			
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
