package com.elm;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CCVideoActivity extends Activity {

	private static final String TAG = CCVideoActivity.class.getName();
	private MediaPlayer mediaPlayer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video);
		mediaPlayer = MediaPlayer.create(this, R.raw.big_buck_bunny);
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
	}

	@Override
	protected void onResume() {

		Log.d(TAG, "on resume");
		super.onResume();

		final SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surfaceview);

		final SurfaceHolder surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(new SurfaceHolder.Callback() {
			public void surfaceCreated(final SurfaceHolder surfaceHolder) {
				Log.d(TAG, "surfaceCreated?");
				mediaPlayer.setDisplay(surfaceHolder);
				mediaPlayer.start();
			}

			public void surfaceChanged(final SurfaceHolder surfaceHolder, final int format, final int width, final int height) {
				Log.d(TAG, "surfaceChanged: format=" + format + "; width=" + width + "; height=" + height);
			}

			public void surfaceDestroyed(final SurfaceHolder surfaceHolder) {
				Log.d(TAG, "surfaceDestroyed");
			}
		});

	}

	@Override
	protected void onPause() {
		mediaPlayer.pause();
		super.onPause();
	}

}

