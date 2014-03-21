package com.beck.spaceblaster;

import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

public class Panel extends SurfaceView implements SurfaceHolder.Callback {
	// GLOBAL VARIABLES
	private CanvasThread canvasthread;
	// Variable used to hold our bitmap
	int startstart = 0;
	Bitmap bm;
	Bitmap ammopic;
	Bitmap debrispic;
	Bitmap targetpic;
	Bitmap bosspic;
	Vibrator v;
	// Variable used to hold the current rotation value of the bitmap
	private float rotation = 20;
	// A Matrix is used to define size, position, and rotation
	// of a graphic. We can set up a matrix and use it when drawing.
	private Matrix position;
	private Matrix[] position1 = new Matrix[5];
	// We need 5 Matrices so that each shot can move separately.
	private Matrix[] ammomatrix = new Matrix[5];
	public Target[] target = new Target[5];
	// star
	private Matrix[] starposition1 = new Matrix[200];
	public Star[] star = new Star[200];
	//boss
	private Matrix[] bossposition1 = new Matrix[1];
	public Boss[] boss = new Boss[1];

	// x and y hold the width and height of our canvas.
	MediaPlayer mediaplayer;
	MediaPlayer xmediaplayer;
	int x;
	int y;
	int height;
	int width;
	int start = 0;
	float speed = 0;
	float Positionx = 0;
	float Positiony = 0;
	float shipxsubs;
	float shipysubs;
	float shipxsubsb;
	float shipysubsb;
	private Matrix[] debrisposition = new Matrix[20];
	boolean shipdestroyed = false;
	boolean debrisinitialize = false;
	float[] debrispositionx = new float[20];
	float[] debrispositiony = new float[20];
	float debrisspeed;
	float[] debrisrotation = new float[20];

	// We need 5 Ammo Objects.
	Ammo[] ammo = new Ammo[5];

	// END GLOBAL VARIABLES

	public Panel(Context context, AttributeSet attrs) {
		super(context, attrs);
		mediaplayer = MediaPlayer.create(context, R.raw.hit);
		xmediaplayer = MediaPlayer.create(context, R.raw.explosion);
	    v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		WindowManager wm = (WindowManager) context.getSystemService(context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
        width = size.x;
		height = size.y;
		getHolder().addCallback(this);
		canvasthread = new CanvasThread(getHolder(), this);
		setFocusable(true);
		// Here we load our Bitmap variable with the graphic ship.png in the
		// drawable-hdpi folder.
		bm = BitmapFactory.decodeResource(getResources(), R.drawable.ship);
		ammopic = BitmapFactory.decodeResource(getResources(), R.drawable.ammo);
		debrispic = BitmapFactory.decodeResource(getResources(),
				R.drawable.debri);
		targetpic = BitmapFactory.decodeResource(getResources(),
				R.drawable.moon);
		bosspic = BitmapFactory.decodeResource(getResources(),R.drawable.boss);
		// Here we initialize our position Matrix.
		position = new Matrix();

		// We initialize our ammo objects using this simple for loop. There will
		// be 5.
		for (int i = 0; i < bossposition1.length; i++) {
			bossposition1[i] = new Matrix();
		}

		for (int i = 0; i < boss.length; i++) {
			boss[i] = new Boss();
			boss[i].positionx = width/2;
			boss[i].positiony = height + 40;
		}
		
		for (int i = 0; i < starposition1.length; i++) {
			starposition1[i] = new Matrix();
		}
		for (int i = 0; i < star.length; i++) {
			star[i] = new Star();
			Random rand = new Random();
			star[i].positionx = (float) rand.nextInt(width);
			star[i].positiony = (float) rand.nextInt(height);
		}
		

		for (int i = 0; i < position1.length; i++) {
			position1[i] = new Matrix();
		}

		for (int i = 0; i < debrisposition.length; i++) {
			debrisposition[i] = new Matrix();
		}

		for (int i = 0; i < target.length; i++) {
			target[i] = new Target();
			Random rand = new Random();
			target[i].positionx = (float) rand.nextInt(width - 50);
			target[i].positiony = (float) rand.nextInt(height -200);
		}
		// star target

		for (int i = 0; i < ammo.length; i++) {
			ammo[i] = new Ammo();
		}

		// We initialize 5 Matrices. Each Matrix corresponds to an instance of
		// ammo.
		for (int i = 0; i < ammomatrix.length; i++) {
			ammomatrix[i] = new Matrix();
		}

	}

	public Panel(Context context) {
		super(context);
		getHolder().addCallback(this);

		setFocusable(true);

	}

	@Override
	public void onDraw(Canvas canvas) {
		// x and y are **global** variables defined at the top of this class. We
		// need to know how big our screen
		// is. The center on a phone will be different than the center point on
		// a
		// tablet.

		if (GetterSetter.button3pressed == 1) {
			speed = speed + .04f;
		} else if (speed > 0) {
			speed = speed - .01f;
		}

		x = canvas.getWidth();
		y = canvas.getHeight();

		// Within the update method, we determine position and speed of all
		// objects.
		update();

		canvas.drawColor(Color.BLACK);

		// The Matrix position is updated within the update() method.
		for (int i = 0; i < starposition1.length; i++) {
			canvas.drawBitmap(debrispic, starposition1[i], null);
		}

		if (!shipdestroyed) {
			canvas.drawBitmap(bm, position, null);
		} else {
			for (int i = 0; i < debrisposition.length; i++) {
				canvas.drawBitmap(debrispic, debrisposition[i], null);
			}
		}

		// Here we loop through all of the ammo matrices. If the corresponding
		// ammo instance has
		// the isactive integer set to 1, it will draw the bitmap to the screen.
		for (int i = 0; i < ammomatrix.length; i++) {
			if (ammo[i].isactive == 1) {
				canvas.drawBitmap(ammopic, ammomatrix[i], null);
			}
		}
		for (int i = 0; i < position1.length; i++) {
			if (target[i].isshowing) {
				canvas.drawBitmap(targetpic, position1[i], null);
			}
		}
		for (int i = 0; i < bossposition1.length; i++) {
		if(boss[i].isshowing)
		{
		canvas.drawBitmap(bosspic, bossposition1[i], null);
		}
		}
		
	}

	public void update() {

		if (GetterSetter.reset == 1) {
			Positionx = ((x / 2) - bm.getWidth() / 2);
			Positiony = ((y / 2) - bm.getWidth() / 2);
			shipdestroyed = false;
			for (int i = 0; i < target.length; i++) {
				target[i] = new Target();
				Random rand = new Random();
				target[i].positionx = (float) rand.nextInt(x);
				target[i].positiony = (float) rand.nextInt(y);
			}
			for (int i = 0; i < boss.length; i++) {
				boss[i] = new Boss();
				boss[i].positionx = x/2;
				boss[i].positiony = y + 40;
				boss[i].isshowing = true;
			}
			GetterSetter.score = 0;
			GetterSetter.reset = 0;
			Boss.bossincoming = true;
			Ammo.canfire = true;
			speed = 0;
			
	
		}
		
		

		// Every time an ammo object reaches the edge of the screen, --see
		// below-- , ammodestroyed is increased
		// by one. Once 4 shots have reached the edged, all ammunition is reset
		// to default values.
		if (GetterSetter.ammodestroyed >= 3) {
			// Just making sure justtouched isn't 1 here.
			GetterSetter.justtouched = 0;
			// We are ready to fire 4 new shots.
			GetterSetter.shotsfired = 0;
			// We tell the program nothing has reached the edge.
			GetterSetter.ammodestroyed = 0;
			// the resetammo method reinitializes all ammo to default values.
			resetammo();
		}

		// Matrix m is a **local** variable. We will configure m and use it
		// set the value of our global Matrix position.

		Matrix m = new Matrix();
		// Matrix[] target = new Matrix[5];
		Matrix[] localdebris = new Matrix[20];
		if (debrisinitialize) {
			for (int i = 0; i < debrispositionx.length; i++) {
				debrispositionx[i] = Positionx;
				debrispositiony[i] = Positiony;
				debrisspeed = speed;
				Random rand = new Random();
				debrisrotation[i] = (float) rand.nextInt(360);
			}
			debrisinitialize = false;
		}
		// for (int i = 0; i < target.length; i++) {
		// target[i] = new Matrix();
		// }
		for (int i = 0; i < localdebris.length; i++) {
			localdebris[i] = new Matrix();
		}

		// Matrix localammomatrix is a **local** variable. We will configure it
		// and use it
		// set the value of our global Matrix --ammomatrix-- .

		// declare and initialize our local matrix array.
		Matrix[] localammomatrix = new Matrix[5];

		for (int i = 0; i < localammomatrix.length; i++) {
			localammomatrix[i] = new Matrix();
		}

		// This is where we define the center of the screen.
		// x = canvas.getWidth(); --Look in onDraw
		// y = canvas.getHeight(); --Look in on Draw
		// Example: If the canvas width is 10, then x/2 would equal 5, --the
		// horizontal center.
		// We use positionx and positiony to draw the center of the bitmap to
		// the center of the screen.

		if (start == 0) {
			Positionx = ((x / 2) - bm.getWidth() / 2);
			Positiony = ((y / 2) - bm.getHeight() / 2);
			start = 1;
		}

		float speedx = (float) Math.sin(rotation * (Math.PI / 180)) * speed;
		float speedy = (float) Math.cos(rotation * (Math.PI / 180)) * speed;

		// If the user touches the screen, we want to get the current position,
		// rotation, and speed of the ship.
		// We load this information into the current ammo instance and use it to
		// send it flying off in the correct
		// direction.

		if (GetterSetter.justtouched == 1 && GetterSetter.shotsfired < 5) {
			GetterSetter.justtouched = 0;

			// Note: ammo[GetterSetter.shotsfired] is an array position equaling
			// either, 0,1,2,3,4
			// Every time the screen is touched, the shotsfired value goes up by
			// one.
			float ammospeedx = (float) Math.sin(rotation * (Math.PI / 180))
					* (ammo[GetterSetter.shotsfired].movingspeed + speed);
			float ammospeedy = (float) Math.cos(rotation * (Math.PI / 180))
					* (ammo[GetterSetter.shotsfired].movingspeed + speed);

			// Load the current ammo object with all the relevant info.
			ammo[GetterSetter.shotsfired].isactive = 1;
			ammo[GetterSetter.shotsfired].speedx = ammospeedx;
			ammo[GetterSetter.shotsfired].speedy = ammospeedy;
			ammo[GetterSetter.shotsfired].positionx = Positionx;
			ammo[GetterSetter.shotsfired].positiony = Positiony;

		}

		// Set X Boundary for ship
		if ((Positionx + speedx) > (x - 20) || (Positionx + speedx) < 0) {

			speed = 0;

		}
		// Set Y Boundary for ship
		if ((Positiony - speedy) > (y - 20) || (Positiony - speedy) < 0) {

			speed = 0;

		}

		// Move the Ship
		Positionx += speedx;
		Positiony -= speedy;

		// Move each active ammo instance by looping through them, changing the
		// position value, and assigning
		// the new position information to the correct GLOBAL ammomatrix.
		for (int i = 0; i < ammo.length; i++) {
			if (ammo[i].isactive == 1) {
				// Move the ammo picture.
				ammo[i].positionx += ammo[i].speedx;
				ammo[i].positiony -= ammo[i].speedy;
				// Set up our local matrix correctly.
				localammomatrix[i].postTranslate(ammo[i].positionx,
						ammo[i].positiony);
				// Assign this info to our global matrix so the drawing can
				// happen.
				ammomatrix[i].set(localammomatrix[i]);

				// Set the X boundary for the ammo and increment ammodestroyed
				// when reached.
				if ((ammo[i].positionx + ammo[i].speedx) > x
						|| (ammo[i].positionx + ammo[i].speedx) < 0) {

					ammo[i].isactive = 0;
					GetterSetter.ammodestroyed++;

				}

				// Set the Y boundary for the ammo and increment ammodestroyed
				// when reached.
				if ((ammo[i].positiony + ammo[i].speedy) > y
						|| (ammo[i].positiony + ammo[i].speedy) < 0) {

					ammo[i].isactive = 0;
					GetterSetter.ammodestroyed++;

				}

			}
		}

		// Current rotation value

		if (!shipdestroyed) {
			m.postRotate(rotation, bm.getWidth() / 2, bm.getHeight() / 2);
			m.postTranslate(Positionx, Positiony);
		} else {
			for (int z = 0; z < localdebris.length; z++) {
				float debrisspeedx = (float) Math.sin(debrisrotation[z] + 150
						* (Math.PI / 180))
						* debrisspeed - .2f;
				float debrisspeedy = (float) Math.sin(debrisrotation[z]
						* (Math.PI / 180))
						* debrisspeed - .2f;
				debrispositionx[z] += debrisspeedx;
				debrispositiony[z] -= debrisspeedy;

				localdebris[z].postRotate(rotation, debrispic.getWidth() / 2,
						debrispic.getWidth() / 2);
				localdebris[z].postTranslate(debrispositionx[z],
						debrispositiony[z]);

				debrisposition[z].set(localdebris[z]);
			}
		}

		Matrix[] targetmatrix = new Matrix[5];
		for (int i = 0; i < targetmatrix.length; i++) {
			targetmatrix[i] = new Matrix();
		}

		for (int i = 0; i < targetmatrix.length; i++) {

			targetmatrix[i].postTranslate(target[i].positionx,
					target[i].positiony);
			targetmatrix[i].postRotate(target[i].rotation,
					ammopic.getWidth() / 2, ammopic.getHeight() / 2);
			position1[i].set(targetmatrix[i]);
		}
		// star
		Matrix[] starmatrix = new Matrix[200];
		
		for (int i = 0; i < starmatrix.length; i++) {
			starmatrix[i] = new Matrix();
		}
		for (int i = 0; i < starmatrix.length; i++) {
			Random speedrand = new Random();
			Star.speed = (float) speedrand.nextInt(20);
			star[i].positiony = star[i].positiony + Star.speed;
			if(star[i].positiony > y)
			{
				
				Random yrand = new Random();
				star[i].positiony = (float) yrand.nextInt(30);
				Random xrand = new Random();
				star[i].positionx = (float) xrand.nextInt(x);
			}

			starmatrix[i].postTranslate(star[i].positionx, star[i].positiony);
			starmatrix[i].postRotate(star[i].rotation,
					debrispic.getWidth() / 2, debrispic.getHeight() / 2);
			starposition1[i].set(starmatrix[i]);
		}
		//boss
		
	
			Matrix[] bossmatrix = new Matrix[1];
			
			for (int i = 0; i < bossmatrix.length; i++) {
				bossmatrix[i] = new Matrix();
			}
			
			for (int i = 0; i < bossmatrix.length; i++){
				if(GetterSetter.score == 5)
				{
				if (Boss.bossincoming == true)
				{
					boss[i].positiony = boss[i].positiony - 2f;
					if(boss[i].positiony == y -700)
					{
						Boss.bossincoming = false;
					}
				}
				}
				boss[i].isshowing = true;
				bossmatrix[i].postTranslate(boss[i].positionx,boss[i].positiony);
				bossmatrix[i].postRotate(boss[i].rotation,bosspic.getWidth() / 2, bosspic.getHeight() / 2);
				bossposition1[i].set(bossmatrix[i]);
			}
			
			
			
		

		// m.postRotate(rotation, bm.getWidth() / 2, bm.getHeight() / 2);
		// m.postTranslate(Positionx, Positiony);
		// This method sets the value of of our **global** variable (position)
		// to the **local** variable (m)
		position.set(m);

		// update the rotation value.
		// We call update every frame refresh in onDraw.
		if (GetterSetter.button1pressed == 1) {
			rotation += 2;
		}
		if (GetterSetter.button2pressed == 1) {
			rotation -= 2;
		}
		if (rotation == 360 || rotation == -360) {
			rotation = 0;
		}

		// distance check
		float distance = 9000;
		float shipdistance = 9000;
		for (int i = 0; i < target.length; i++) {
			for (int q = 0; q < ammo.length; q++) {
				float xsubs = target[i].positionx - ammo[q].positionx;
				float ysubs = target[i].positiony - ammo[q].positiony;
				float squarevalue = (xsubs * xsubs) + (ysubs * ysubs);
				distance = (float) Math.sqrt(squarevalue);
				if (distance < 30.0) {
					target[i].isshowing = false;
					target[i].positionx = 9000;
					target[i].positiony = 9000;
					mediaplayer.start();
					v.vibrate(1000);
						GetterSetter.score = GetterSetter.score + 1;
					}
				
				}

			// calculate ship/target collision and destroy ship
			shipxsubs = Positionx - target[i].positionx;
			shipysubs = Positiony - target[i].positiony;
			float shipsubs = (shipxsubs * shipxsubs) + (shipysubs * shipysubs);
			shipdistance = (float) Math.sqrt(shipsubs);
			if (shipdistance < 26.0 && shipdestroyed == false) {
				shipdestroyed = true;
				debrisinitialize = true;
				target[i].positionx = 90000;
				target[i].positiony = 90000;
				Ammo.canfire = false;
				xmediaplayer.start();
				v.vibrate(2000);
			}
		}   
		for (int i = 0; i < boss.length; i++) {
		for (int q = 0; q < ammo.length; q++) {
		float xsubsf = boss[i].positionx - ammo[q].positionx;
		float ysubsf = boss[i].positiony - ammo[q].positiony;
		float squarevaluef = (xsubsf * xsubsf) + (ysubsf * ysubsf);
		float fdistance = (float) Math.sqrt(squarevaluef);
		if (fdistance < 30.0) {
			boss[i].isshowing = false;
			boss[i].positionx = 9000;
			boss[i].positiony = 9000;
			mediaplayer.start();
			v.vibrate(1000);
           GetterSetter.score = GetterSetter.score + 1;
           xmediaplayer.start();
           v.vibrate(3000);
           GetterSetter.hp = 1;
			}
		}
		}
		

    for (int i = 0; i < boss.length; i++) {
			shipxsubsb = Positionx - boss[i].positionx;
			shipysubsb = Positiony - boss[i].positiony;
			float shipsubsb = (shipxsubsb * shipxsubsb) + (shipysubsb * shipysubsb);
			float shipdistanceb = (float) Math.sqrt(shipsubsb);
			if (shipdistanceb < 26.0 && shipdestroyed == false) {
				shipdestroyed = true;
				debrisinitialize = true;
				boss[i].positionx = 90000;
			    boss[i].positiony = 90000;
				Ammo.canfire = false;
				xmediaplayer.start();
				v.vibrate(2000);

			}
		}

	}

	public void resetammo() {
		if (Ammo.canfire == true) {
			for (int i = 0; i > ammo.length; i++) {
				ammo[i] = new Ammo();
			}
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	public void surfaceCreated(SurfaceHolder holder) {
		canvasthread = new CanvasThread(getHolder(), this);
		canvasthread.setRunning(true);
		canvasthread.start();

	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		canvasthread.setRunning(false);
		while (retry) {
			try {
				canvasthread.join();
				retry = false;
			} catch (InterruptedException e) {
				// we will try it again and again...
			}
		}

	}

}