package com.iptv.play;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.iptv.utils.ForceTvUtils;
import com.iptv.utils.LogUtils;

public class Player  implements Mplayer {

	private static String TAG=Player.class.getName();
	public boolean playing=false,pause=false;
	public boolean isPlaying() {
		return playing;
	}

	public boolean isPause() {
		return pause;
	}

	public static int surfaceCreated = 100, mediaplayerPrepared = 101,
			mediaplayererror = 102, mediaplayerstart = 103,
			mediaplayerbufferedStart = 104, mediaplayerbufferedEnd = 105, mediaplayerserverDied = 106,mediaplayerseekcomplete=107,surfaceDestroyed=108,mediaplayercomplete=109;
	private MediaPlayer mediaplayer = null;
	private SurfaceView playview;
	private SurfaceHolder surfaceholder;
	private surfaceholdercallback callback;
	private mediaplayerlistener listener;
	private Handler handler;

	public Player(Context context, SurfaceView playview, Handler handler) {
		this.playview = playview;
		this.handler = handler;
		LogUtils.write(TAG, "创建 mediaplayer");
		mediaplayer = new MediaPlayer();
		mediaplayer.setScreenOnWhilePlaying(true);
		listener = new mediaplayerlistener();
		mediaplayer.setOnPreparedListener(listener);
		mediaplayer.setOnErrorListener(listener);
		mediaplayer.setOnInfoListener(listener);
		mediaplayer.setOnVideoSizeChangedListener(listener);
		mediaplayer.setOnBufferingUpdateListener(listener);
		mediaplayer.setOnSeekCompleteListener(listener);
		mediaplayer.setOnCompletionListener(listener);
		surfaceholder = playview.getHolder();
		callback = new surfaceholdercallback();
		surfaceholder.addCallback(callback);
	}

	public void setPlayUrl(String url) {
		LogUtils.write(TAG, "设置播放URL"+url);
		mediaplayer.reset();
		try {
			LogUtils.write(TAG, "设置播放Displayholder");
			mediaplayer.setDisplay(surfaceholder);
			mediaplayer.setDataSource(url);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogUtils.write(TAG, "设置url异常");
			handler.sendEmptyMessage(mediaplayererror);
			return;
		}
		LogUtils.write(TAG, "设置 mediaplayer prepareAsync");
		mediaplayer.prepareAsync();
	}
	public long gettotalTime(){
		return mediaplayer.getDuration();
	}
	public long getCurrTime(){
		return mediaplayer.getCurrentPosition();
	}
	public void play() {
		surfaceholder.setFixedSize(mediaplayer.getVideoWidth(), mediaplayer.getVideoHeight());
		mediaplayer.start();
		playing=true;
		pause=false;
	}
	public void pause(){
		mediaplayer.pause();
		playing=false;
		pause=true;
	}
	public void SeeKto(int msec){
		mediaplayer.seekTo(msec);
	}

	public void stop() {
		if (mediaplayer != null) {
			LogUtils.write(TAG, "mediaplayer  stop");
			if(playing){
				mediaplayer.stop();
			}
			mediaplayer.reset();
			playing=false;
			pause=false;
		}
		
	}
	public void release(){
		if(mediaplayer!=null){
			LogUtils.write(TAG, "mediaplayer  release");
			ForceTvUtils.StopChan();
			mediaplayer.release();
		}
	}

	class mediaplayerlistener implements OnPreparedListener, OnErrorListener,
			OnInfoListener, OnVideoSizeChangedListener,
			OnBufferingUpdateListener,OnSeekCompleteListener,OnCompletionListener {

		@Override
		public void onPrepared(MediaPlayer arg0) {
			// TODO Auto-generated method stub
			LogUtils.write(TAG, "播放onPrepared");
			handler.sendEmptyMessage(mediaplayerPrepared);
		}

		@Override
		public boolean onError(MediaPlayer arg0, int code, int arg2) {
			// TODO Auto-generated method stub
			LogUtils.write(TAG, "播放onError " + code + " " + arg2);
			if(code==MediaPlayer.MEDIA_ERROR_SERVER_DIED){
				handler.sendEmptyMessage(mediaplayerserverDied);
			}
			return true;
		}

		@Override
		public boolean onInfo(MediaPlayer arg0, int code, int arg2) {
			// TODO Auto-generated method stub
			LogUtils.write(TAG, "播放onInfo " + code + " " + arg2);
			if (code == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
				handler.sendEmptyMessage(mediaplayerbufferedStart);
			} else if (code == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
				handler.sendEmptyMessage(mediaplayerbufferedEnd);
			}
			return true;
		}

		@Override
		public void onVideoSizeChanged(MediaPlayer arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub
			LogUtils.write(TAG, "播放onVideoSizeChanged " + arg1 + " " + arg2);
			handler.sendEmptyMessage(mediaplayerstart);
		}

		@Override
		public void onBufferingUpdate(MediaPlayer mp, int percent) {
			// TODO Auto-generated method stub
			LogUtils.write(TAG, "播放onBufferingUpdate " + percent);
		}

		@Override
		public void onSeekComplete(MediaPlayer arg0) {
			// TODO Auto-generated method stub
			LogUtils.write(TAG, "播放onSeekComplete ");
			handler.sendEmptyMessage(mediaplayerseekcomplete);
		}

		@Override
		public void onCompletion(MediaPlayer mp) {
			// TODO Auto-generated method stub
			LogUtils.write(TAG, "播放onCompletion ");
			handler.sendEmptyMessage(mediaplayercomplete);
		}

	}

	class surfaceholdercallback implements SurfaceHolder.Callback2,SurfaceHolder.Callback {

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			// TODO Auto-generated method stub
			LogUtils.write(TAG, "播放surfaceChanged "+format+" " + width + " " + height);
			
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			LogUtils.write(TAG, "surfaceCreated");
			handler.sendEmptyMessage(surfaceCreated);
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			LogUtils.write(TAG, "surfaceDestroyed");
			handler.sendEmptyMessage(surfaceDestroyed);
			stop();
			
		}

		@Override
		public void surfaceRedrawNeeded(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			LogUtils.write(TAG, "surfaceRedrawNeeded");
		}

	}

}
