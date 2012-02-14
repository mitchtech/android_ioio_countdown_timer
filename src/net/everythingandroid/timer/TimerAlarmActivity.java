package net.everythingandroid.timer;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.AbstractIOIOActivity;
import net.mitchtech.ioio.countdowntimer.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class TimerAlarmActivity extends AbstractIOIOActivity {
    private static final String TAG = TimerAlarmActivity.class.getSimpleName();
    private static final int LED_PIN = 34;
	private boolean wasVisible = false;
	public static boolean PinState = false;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Log.DEBUG)
			Log.v("TimerAlarmActivity: onCreate()");		

		// Hide the title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.alert);
		Button okbutton = (Button) findViewById(R.id.okbutton);
		okbutton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				PinState = false;
				ManageNotification.clearAll(TimerAlarmActivity.this);
				finish();
			}
		});

		Button newbutton = (Button) findViewById(R.id.newbutton);
		newbutton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent timerActivity = new Intent(TimerAlarmActivity.this, TimerActivity.class);
				startActivity(timerActivity);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (Log.DEBUG)
			Log.v("TimerAlarmActivity: onResume()");

		wasVisible = false;

		Intent i = getIntent();
		int flags = i.getFlags();
		if ((flags & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) == Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) {
			i.setFlags(flags & ~Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
			Intent timerActivity = new Intent(this, TimerActivity.class);
			// alarmDialog.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
			// Intent.FLAG_ACTIVITY_NO_HISTORY);
			if (Log.DEBUG)
				Log.v("TimerAlarmActivity: Starting TimerActivity.class intent");
			startActivity(timerActivity);
		}

	}
	
	public void turnOn (int pinNumber) {
		Log.v(TAG + "turnOn: " + pinNumber);
	}
	
	public void turnOff (int pinNumber) {
		Log.v(TAG + "turnOff: " + pinNumber);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (Log.DEBUG)
			Log.v("TimerAlarmActivity: onPause()");
		if (wasVisible) {
			ClearAllReceiver.removeCancel(this);
			ManageKeyguard.reenableKeyguard();
			ManageWakeLock.release();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (Log.DEBUG)
			Log.v("TimerAlarmActivity: onStop()");
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			wasVisible = true;
			if (Log.DEBUG)
				Log.v("TimerAlarmActivity: onWindowFocusChanged(true)");
		} else {
			if (Log.DEBUG)
				Log.v("TimerAlarmActivity: onWindowFocusChanged(false)");
		}
	}
	
	
	class IOIOThread extends AbstractIOIOActivity.IOIOThread {
		private DigitalOutput mLed1;

		@Override
		protected void setup() throws ConnectionLostException {
			mLed1 = ioio_.openDigitalOutput(LED_PIN, false);
		}

		@Override
		protected void loop() throws ConnectionLostException {
			mLed1.write(PinState);
			try {
				sleep(100);
			} catch (InterruptedException e) {
			}
		}
	}

	@Override
	protected AbstractIOIOActivity.IOIOThread createIOIOThread() {
		return new IOIOThread();
	}

}