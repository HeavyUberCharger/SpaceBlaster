package com.beck.spaceblaster;

import java.lang.reflect.Field;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends Activity {
	int i;
	float x;
	float y;
	Context context;
	MediaPlayer mediaplayer;
	MediaPlayer mediaplayer1;
	TextView tv;
	TextView tv1;
	Handler mHandler;
	Handler mHandler1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tv = (TextView) findViewById(R.id.textView1);
		Typeface font = Typeface.createFromAsset(getAssets(), "spaceage.ttf");
		tv.setTypeface(font);
		tv1 = (TextView) findViewById(R.id.textView2);
        
        tv1.setText("");
		tv.setText("This program is designed for my Android programming class. \nThe code can "
				+ "be found at github: androidteacher.\n"
				+ "The lessons to go along with this tutorial can be found at:\n "
				+ "http://linuxclassroom.com/bitmap1/ \n"
				+ "Touch the Screen to Fire!");
		context = getApplicationContext();

		mediaplayer = MediaPlayer.create(context, R.raw.main);
		mediaplayer.setLooping(true);
		mediaplayer.start();

		mediaplayer1 = MediaPlayer.create(context, R.raw.blast);

		// Right Button
		final ImageButton button = (ImageButton) findViewById(R.id.button1);
		button.setOnTouchListener(new View.OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					GetterSetter.button1pressed = 1;

					return true;
				}

				if (event.getAction() == MotionEvent.ACTION_UP) {
					GetterSetter.button1pressed = 0;

					return true;
				}
				return false;
			}

		});
		// Left Button
		final ImageButton button2 = (ImageButton) findViewById(R.id.button2);
		button2.setOnTouchListener(new View.OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					GetterSetter.button2pressed = 1;
					return true;
				}

				if (event.getAction() == MotionEvent.ACTION_UP) {
					GetterSetter.button2pressed = 0;

					return true;
				}
				return false;
			}

		});
		// Fired Button
		final ImageButton button3 = (ImageButton) findViewById(R.id.button3);
		button3.setOnTouchListener(new View.OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					GetterSetter.button3pressed = 1;

					return true;
				}

				if (event.getAction() == MotionEvent.ACTION_UP) {
					GetterSetter.button3pressed = 0;

					return true;
				}
				return false;
			}

		});

		/*
		 * HACK: Show the overflow dots on devices that have a menu button
		 */
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception ex) {
			// Ignore
		}

		/*
		 * END OVERFLOW HACK
		 */
		mHandler = new Handler();
		mHandler.post(mUpdate);
		mHandler1 = new Handler();
		mHandler1.post(mUpdate1);
	}

	private Runnable mUpdate = new Runnable() {
		public void run() {
			
			tv.setText("score:" + GetterSetter.score);
			mHandler.postDelayed(this, 1);
		}

	};
	private Runnable mUpdate1 = new Runnable() {
		public void run() {
			if(GetterSetter.score == 1)
			{
			tv1.setText("" + GetterSetter.hp);
			mHandler1.postDelayed(this, 1);
			}
		}

	};

	@Override
	public boolean onTouchEvent(MotionEvent e) {

		x = e.getX();
		y = e.getY();
		// Every Time the screen is touched we toggle the justtouched value and
		// increase shotsfired.
		// We'll be using this data in our Panel class.
		if (Ammo.canfire == true) {
			GetterSetter.shotsfired++;
			GetterSetter.justtouched = 1;
			mediaplayer1.reset();
			mediaplayer1 = MediaPlayer.create(context, R.raw.blast);
			mediaplayer1.start();
		}
		return true;

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.reset:
			GetterSetter.reset = 1;
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		mediaplayer.stop();
	}

}
