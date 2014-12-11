package com.example.kelvinwu.rotationapp;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    public static volatile MediaPlayer mp;

    private SensorManager mSensorManager;
    private OrientationEventListener mSensorEventListener;
    private GameView circleView;

    private int lastSoundPlayed = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RelativeLayout layout = (RelativeLayout)findViewById(R.id.myLayout);

        circleView = new GameView(getApplicationContext());
        layout.addView(circleView);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorEventListener = new OrientationEventListener(this.getApplicationContext()) {
            @Override
            public void onOrientationChanged(int orientation) {
                updateUI(orientation);
                if (orientation >= 0 && orientation < 90 && lastSoundPlayed != 0) {
                    if (playAudio(getApplicationContext().getResources().openRawResourceFd(R.raw.sound_0))) {
                        lastSoundPlayed = 0;
                    }
                    return;
                }
                else if (orientation >= 90 && orientation < 180 && lastSoundPlayed != 1) {

                    if (playAudio(getApplicationContext().getResources().openRawResourceFd(R.raw.sound_1)));
                    {
                        lastSoundPlayed = 1;
                    }
                    return;
                }
                else if (orientation >= 180 && orientation < 270 && lastSoundPlayed != 2) {
                    if (playAudio(getApplicationContext().getResources().openRawResourceFd(R.raw.sound_2))){
                        lastSoundPlayed = 2;
                    }
                    return;
                }
                else if (orientation >= 270 && orientation < 360 && lastSoundPlayed != 3) {
                    if (playAudio(getApplicationContext().getResources().openRawResourceFd(R.raw.sound_3))) {
                        lastSoundPlayed = 3;
                    }
                    return;
                }
                else if (orientation == -1 && lastSoundPlayed != 4) {
                    if (playAudio(getApplicationContext().getResources().openRawResourceFd(R.raw.sound_4))) {
                        lastSoundPlayed = 4;
                    }
                    return;
                }
            }
        };

    }

    private void updateUI(final int orientation) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView t = (TextView) findViewById(R.id.test);
                t.setText("test " + orientation);
                circleView.doSomething(orientation);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorEventListener.enable();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSensorEventListener.disable();
    }

    private static MediaPlayer getMediaPlayer() {
        if (mp == null) {
            synchronized (MediaPlayer.class) {
                if (mp == null) {
                    mp = new MediaPlayer();
                    return mp;
                }
            }
        }
        return mp;
    }

    synchronized boolean playAudio(AssetFileDescriptor afd) {
        if (getMediaPlayer().isPlaying()){
            return false;
        }
        getMediaPlayer().reset();
        try {
            getMediaPlayer().setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            getMediaPlayer().prepare();
        } catch (Exception e) {
            Log.d("Audio", e.getStackTrace() + "");
        }
        getMediaPlayer().start();
        return true;
    }

    public class GameView extends SurfaceView implements SurfaceHolder.Callback {
        private Bitmap bmp;
        private int x = 0;
        private int xSpeed = 1;
        Paint p;
        Canvas c;
        int radius = 100;

        public GameView(Context context) {
            super(context);
            setWillNotDraw(false);
            getHolder().addCallback(this);
            c = new Canvas();
            p = new Paint();
            p.setColor(Color.WHITE);
        }

        public void doSomething(int degrees) {

            try {
                c = getHolder().lockCanvas();
                synchronized (getHolder()) {
                    drawcanvas(c, degrees);
                }
            }
            finally {
                if (c != null) {
                    getHolder().unlockCanvasAndPost(c);
                }
            }
        }
        // The actual drawing in the Canvas (not the update to the screen).
        private void drawcanvas(Canvas c, int degrees)
        {
            p.setColor(Color.WHITE);
            c.drawPaint(p);
            p.setColor(Color.BLACK);
            c.drawArc(new RectF(c.getWidth() / 2 - 100, c.getHeight() / 2 - 100, c.getWidth() / 2 + 100, c.getHeight() / 2 + 100), -90f, degrees, true, p);
            c.drawLine(c.getWidth() / 2 - 200, c.getHeight() / 2, c.getWidth() / 2 + 200, c.getHeight() / 2, p);
            c.drawLine(c.getWidth() / 2, c.getHeight() / 2  - 200, c.getWidth() / 2, c.getHeight() / 2  + 200, p);
            p.setTextSize(50);
            int xPos = (c.getWidth() / 2);
            int yPos = (c.getHeight() / 2 + 175) ;
            p.setTextAlign(Paint.Align.CENTER);
            c.drawText(degrees + "", xPos, yPos, p);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    }

}
