package asia.remix.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class MainService extends Service{
	final static String TAG = "MainService";
	String sChannelId = "Channel ID";

	@Override
	public void onCreate(){
		super.onCreate();
		Log.d( TAG, "●onCreate" );
	}

	// バインドのみ利用する場合 onStartCommand オーバーライド不要
	@Override
	public int onStartCommand( Intent intent, int flags, int startId ){
		Log.d( TAG, "●onStartCommand" );
		Context context = getApplicationContext();
		Toast.makeText( context, "onStartCommand", Toast.LENGTH_SHORT ).show();
		String sTitle = context.getString( R.string.app_name );

		if( Build.VERSION.SDK_INT >= 26 ){
			//API26 Android 8以上 通知を送信する前に通知チャネルを作成する必要がある
			NotificationChannel channel
			= new NotificationChannel( sChannelId, sTitle, NotificationManager.IMPORTANCE_DEFAULT );
			NotificationManager notificationManager
			= (NotificationManager)context.getSystemService( Context.NOTIFICATION_SERVICE );
			notificationManager.createNotificationChannel( channel );
		}

		NotificationCompat.Builder builder;
		if( Build.VERSION.SDK_INT >= 26 ){
			builder = new NotificationCompat.Builder( context, sChannelId );
		}else{
			builder = new NotificationCompat.Builder( context );
		}
		builder.setContentTitle( sTitle );
		builder.setSmallIcon( android.R.drawable.btn_star );
		builder.setColor( android.graphics.Color.RED );
		builder.setContentText( "Text" );
		builder.setSubText( "SubText" );
		builder.setContentInfo( "Info" );
		builder.setTicker( "Ticker" );//通知到着時に通知バー表示(4.4まで)
		//通知はタップ応答する必要があり、ここではMainActivityを立ち上げる
		Intent i =new Intent( this, MainActivity.class );
		PendingIntent pendingIntent = PendingIntent.getActivity( context, 0, i, 0 );
		builder.setContentIntent( pendingIntent );

		Notification notification = builder.build();
		startForeground( 1, notification );//0は通知表示されない

		return super.onStartCommand( intent, flags, startId );
	}

	// オーバーライド必須、バインド不要の場合はnullを返す。
	// IBinderを継承したクラスを返す必要がある
	@Override
	public IBinder onBind( Intent intent ){
		Log.d( TAG, "●onBind" );
		Toast.makeText( getApplicationContext(), "onBind", Toast.LENGTH_SHORT ).show();
		return new MainBinder();
	}

	@Override
	public boolean onUnbind( Intent intent ){
		Log.d( TAG, "●onUnbind" );
		Toast.makeText( getApplicationContext(), "onUnbind", Toast.LENGTH_SHORT ).show();
		return true;
	}

	@Override
	public void onRebind( Intent intent ){
		Log.d( TAG, "●onRebind" );
		Toast.makeText( getApplicationContext(), "onRebind", Toast.LENGTH_SHORT ).show();
	}

	@Override
	public void onDestroy(){
		Log.d( TAG, "●onDestroy" );
		Toast.makeText( getApplicationContext(), "onDestroy", Toast.LENGTH_SHORT ).show();
		super.onDestroy();
	}

	public class MainBinder extends Binder{
		public MainService getService(){
			return MainService.this;
		}
	}

	public void doMethod( String msg ){
		Log.d( TAG, "●doMethod" );
		Toast.makeText( getApplicationContext()
		, String.format( "isUiThread %s / %s", isUiThread(), msg ), Toast.LENGTH_SHORT ).show();
	}

	boolean isUiThread(){
		return Thread.currentThread() == Looper.getMainLooper().getThread();
	}
}