package com.elm;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.TimedText;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import java.io.*;

public class CCVideoActivity extends Activity {

	private static final String TAG = CCVideoActivity.class.getName();
	private MediaPlayer mediaPlayer;
	private TextView textView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video);
		textView = (TextView) findViewById(R.id.text_view);

		mediaPlayer = MediaPlayer.create(this, R.raw.big_buck_bunny);
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mediaPlayer.setOnTimedTextListener(new MediaPlayer.OnTimedTextListener() {
			public void onTimedText(MediaPlayer mediaPlayer, TimedText timedText) {
				setTimedText(timedText);
			}
		});

		final String resourceName = ("android.resource://" + getPackageName() + "/" + R.raw.big_buck_bunny_cc);
		final Uri uri = Uri.parse(resourceName);
		try {
			mediaPlayer.addTimedTextSource(
					getSubtitleFile(R.raw.big_buck_bunny_cc),
					MediaPlayer.MEDIA_MIMETYPE_TEXT_SUBRIP
			);
			Log.d(TAG, "added " + uri.toString() + " as timed text");
		} catch (IOException e) {
			throw new RuntimeException(e.toString(), e);
		}
		final MediaPlayer.TrackInfo[] trackInfo = mediaPlayer.getTrackInfo();
		findTrackIndexFor(MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT, trackInfo);
		final int srtTrack = findTrackIndexFor(MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT, trackInfo);
		Log.d(TAG, "srtTrack: " + srtTrack);
		if (srtTrack > 0) {
			mediaPlayer.selectTrack(srtTrack);
		}

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

		surfaceView.setOnClickListener(new View.OnClickListener() {
			public void onClick(final View view) {
				Log.d(TAG, "time: " + mediaPlayer.getCurrentPosition());
			}
		});

	}

	private String getSubtitleFile(int resId) {
		String fileName = getResources().getResourceEntryName(resId);
		File subtitleFile = getFileStreamPath(fileName);
//		if (subtitleFile.exists()) {
//			Log.d(TAG, "Subtitle already exists");
//			return subtitleFile.getAbsolutePath();
//		}
		Log.d(TAG, "Subtitle does not exists, copy it from res/raw");

		// Copy the file from the res/raw folder to your app folder on the
		// device
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			inputStream = getResources().openRawResource(resId);
			outputStream = new FileOutputStream(subtitleFile, false);
			copyFile(inputStream, outputStream);
			return subtitleFile.getAbsolutePath();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeStreams(inputStream, outputStream);
		}
		return "";
	}

	private void copyFile(InputStream inputStream, OutputStream outputStream)
			throws IOException {
		final int BUFFER_SIZE = 1024;
		byte[] buffer = new byte[BUFFER_SIZE];
		int length = -1;
		while ((length = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, length);
		}
	}

	// A handy method I use to close all the streams
	private void closeStreams(Closeable... closeables) {
		if (closeables != null) {
			for (Closeable stream : closeables) {
				if (stream != null) {
					try {
						stream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	protected void onPause() {
		mediaPlayer.pause();
		super.onPause();
	}

	private int findTrackIndexFor(int mediaTrackType, MediaPlayer.TrackInfo[] trackInfo) {
		int index = -1;
		for (int track = 0; track < trackInfo.length; track++) {
			final int trackType = trackInfo[track].getTrackType();
			Log.d(TAG, "track: " + track + "; type: " + trackType);
			if (trackType == mediaTrackType) {
				return track;
			}
		}
		return index;
	}

	private void setTimedText(TimedText timedText) {
		Log.d(TAG, "text @ " + mediaPlayer.getCurrentPosition());
		if (null != timedText) {
			Log.d(TAG, "text:" + timedText.getText());
			textView.setText(timedText.getText());
		}else{
			Log.d(TAG, "(clear)");
			textView.setText("");
		}
	}

}

