package asia.remix.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity{
	final static String TAG = "MainActivity";

	@Override
	protected void onCreate( Bundle savedInstanceState ){
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );
	}

	boolean isServiceBound = false;
	MainService mainService;
	ServiceConnection connection = new ServiceConnection(){
		@Override
		public void onServiceConnected( ComponentName cn, IBinder ib ){
			Log.d( TAG, "ServiceConnection#onServiceConnected()" );
			MainService.MainBinder binder = (MainService.MainBinder)ib;
			mainService = binder.getService();
			isServiceBound = true;
		}

		@Override
		public void onServiceDisconnected( ComponentName cn ){
			Log.d( TAG, "ServiceConnection#onServiceDisconnected() Serviceが深刻なエラーで停止した" );
			unbindMyService();
		}
	};

	void unbindMyService(){
		if( isServiceBound ){
			isServiceBound = false;
			unbindService( connection );
		}
	}

	public void onClickServiceStart( View v ){
		Intent intent = new Intent( getApplication(), MainService.class );
		if( Build.VERSION.SDK_INT >= 26 ){
			startForegroundService( intent );
		}else{
			startService( intent );
		}
	}

	public void onClickServiceBind( View v ){
		Intent intent = new Intent( getApplication(), MainService.class );
		bindService( intent, connection, Context.BIND_AUTO_CREATE );
	}

	public void onClickServiceMethod( View v ){
		if( isServiceBound ){
			mainService.doMethod( "Hello MyService!" );
		}
	}

	public void onClickServiceUnbind( View v ){
		unbindMyService();
	}

	public void onClickServiceStop( View v ){
		unbindMyService();
		Intent intent = new Intent( getApplication(), MainService.class );
		stopService( intent );
	}
}