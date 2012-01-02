package org.ohmage.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.ohmage.OhmageApplication;
import org.ohmage.R;
import org.ohmage.pdc.OhmagePDVManager;
import org.ohmage.pdc.OhmagePDVManagerCallback;

/**
 * Screen to make configuration choices. No CCNx code here. After user presses
 * Connect button, we startup the Android Manager.
 */
public final class NDNActivity extends Activity implements
		OnClickListener, OhmagePDVManagerCallback {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ndn_settings);

		Log.i(TAG, "onCreate()");
		
		_start_pdv_button = (Button) findViewById(R.id.StartPDV);
		if(_start_pdv_button == null)
			throw new Error("Could not find btnStartPDV");

		_setup_stream_button = (Button) findViewById(R.id.SetupStream);
		if (_setup_stream_button == null)
			throw new Error("Could not find btnStartCCNx");

		_request_pull_button = (Button) findViewById(R.id.btnRequestPull);
		if (_request_pull_button == null)
			throw new Error("Could not find btnSetup");

		_tv_status = (TextView) findViewById(R.id.tvStatus);
		if (_tv_status == null)
			throw new Error("Could not find tvStatus");

		_setup_stream_button.setOnClickListener(this);
		_start_pdv_button.setOnClickListener(this);
		_request_pull_button.setOnClickListener(this);

		_etRecURL = (EditText) findViewById(R.id.etRecURL);
		_etRecAuth = (EditText) findViewById(R.id.etAuth);

		restorePreferences();

		enableView(false);
		if(getApplication() == null)
			Log.i("TIMEPASS", "NULLLLLL");

		_ohmage_PDV_manager = OhmagePDVManager.createNewInstance(
				(OhmageApplication)getApplication(), this);
		
		telephonyManager = (TelephonyManager)getSystemService(
				Context.TELEPHONY_SERVICE);
		
		DEVICE_ID = telephonyManager.getDeviceId();
		
		OHMAGE_PHONE_NAMESPACE = "/ndn/ucla.edu/apps/" +
		OhmagePDVManager.getUsername() 
				+ "/androidclient";
		
		_ohmage_PDV_manager.InitializeCCNx(getApplicationContext());
		
		// switch (AndroidNDNManager.getStatus()) {
		// case NOT_INSTANTIATED:
		// _android_NDN_manager = AndroidNDNManager.createNewInstance(
		// this.getApplicationContext(), this);
		// break;
		// case STOPPED:
		// _android_NDN_manager = AndroidNDNManager.getInstance();
		// _android_NDN_manager.setCallbackInterface(this);
		// AndroidNDNManager.setContext(this.getApplicationContext());
		// break;
		// case STARTED_WAIT:
		// enableView(false);
		// _connect_button.setText("Processing");
		// _android_NDN_manager = AndroidNDNManager.getInstance();
		// _android_NDN_manager.setCallbackInterface(this);
		// AndroidNDNManager.setContext(this.getApplicationContext());
		// break;
		// case STARTED_ACTIVE:
		// _android_NDN_manager = AndroidNDNManager.getInstance();
		// _android_NDN_manager.setCallbackInterface(this);
		// AndroidNDNManager.setContext(this.getApplicationContext());
		// enableView(false);
		// _connect_button.setEnabled(true);
		// _connect_button.setText("Disconnect");
		// }
	}

	@Override
	public void onStop()
	{
		super.onStop();
		savePreferences();
	}

	@Override
	public void onClick(View v)
	{
		Log.d(TAG, "OnClickListener " + String.valueOf(v.getId()));

		switch (v.getId()) {
		
		case R.id.StartPDV:
							_ohmage_PDV_manager.setNamespace(
									OHMAGE_PHONE_NAMESPACE);
							_ohmage_PDV_manager.setRemotehost(
									DEFAULT_REMOTEHOST);
							_ohmage_PDV_manager.
								setRemoteport(
										Integer.parseInt(DEFAULT_REMOTEPORT));
							_ohmage_PDV_manager.setRecUrl(
									_etRecURL.getText().toString());
							if(_ohmage_PDV_manager.startCCNx()) {
								_ohmage_PDV_manager.create();
								_ohmage_PDV_manager.startListening();
							}
							//enableView(true);
							break;
		case R.id.SetupStream:
							_ohmage_PDV_manager.setRecAuth(
									_etRecAuth.getText().toString());
							_ohmage_PDV_manager.setupStreams();
							break;
		
		default:
			break;
		}
	}

	// ==========================================================
	// Internal stuff
	/*
	private void connect()
	{
		try {
			// enableView(false);
			// _connect_button.setText("Processing");

			// final File ff = getDir("storage", Context.MODE_WORLD_READABLE);
			// Log.i(TAG, "getDir = " + ff.getAbsolutePath());

			// UserConfiguration.setUserConfigurationDirectory(ff.getAbsolutePath());
			
			if (ccnxConfigured == false) {
				ccnxConfigured = true;
				Log.i(TAG, "Configuring CCNX");
				CCNxConfiguration.config(getApplicationContext());
			}
			Log.i(TAG, "Streams started.");

			_ohmage_PDV_manager.setNamespace(_etNamespace.getText().toString());
			_ohmage_PDV_manager.setRemotehost(_etRemoteHost.getText().toString());
			_ohmage_PDV_manager.setRemoteport(Integer.parseInt(_etRemotePort
					.getText().toString()));
			_ohmage_PDV_manager.setRecUrl(_etRecURL.getText().toString());
			_ohmage_PDV_manager.setRecAuth(_etRecAuth.getText().toString());

			_ohmage_PDV_manager.start();
		}
		catch (final Exception e) {
			Log.e(TAG, "Error with ContentName", e);
			return;
		}

	}*/

	private void disconnect()
	{
		Log.i(TAG, "Streams Stopped.");

		_ohmage_PDV_manager.shutdown();

		enableView(true);
	}

	private void enableView(boolean enable)
	{
		Log.i(TAG, "Current Connection State" + _conn_state);
		switch (_conn_state) {
		case INITIAL:
			// _etNamespace.setEnabled(false);
			// _etRemoteHost.setEnabled(true);
			// _etRemotePort.setEnabled(true);
			_start_pdv_button.setEnabled(true);
			_setup_stream_button.setEnabled(false);
			_etRecURL.setEnabled(true);
			_etRecAuth.setEnabled(false);
			_request_pull_button.setEnabled(false);
			break;
		case STARTED:
			// _etNamespace.setEnabled(false);
			// _etRemoteHost.setEnabled(false);
			// _etRemotePort.setEnabled(false);
			_start_pdv_button.setEnabled(false);
			_setup_stream_button.setEnabled(true);
			_etRecURL.setEnabled(false);
			_etRecAuth.setEnabled(true);
			_request_pull_button.setEnabled(true);
		}
	}

	private void restorePreferences()
	{
		final SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		final String recURL = settings.getString(PREF_REC_URL, DEFAULT_REC_URL);
		final String recAuth = settings.getString(PREF_REC_AUTH,
		 DEFAULT_REC_AUTH);		
		 _etRecURL.setText(recURL);
		_etRecAuth.setText(recAuth);
	}

	private void savePreferences()
	{
		final SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		final SharedPreferences.Editor editor = settings.edit();

		// editor.putString(PREF_NAMESPACE, _etNamespace.getText().toString());
		// editor.putString(PREF_REMOTEHOST, _etRemoteHost.getText().toString());
		// editor.putString(PREF_REMOTEPORT, _etRemotePort.getText().toString());
		// editor.putString(PREF_REC_URL, _etRecURL.getText().toString());
		editor.putString(PREF_REC_AUTH, _etRecAuth.getText().toString());
		editor.commit();
	}

	@Override
	public void postProcess(boolean status)
	{
		final Message msg = Message.obtain();
		msg.what = 1;
		msg.obj = status;
		_handler.sendMessage(msg);
	}

	@Override
	public void postProcess(String text, boolean show_longer)
	{
		final Message msg = Message.obtain();
		msg.what = 0;
		msg.arg1 = show_longer ? 1 : 0;
		msg.obj = text;
		_handler.sendMessage(msg);
	}

	@Override
	public void postProcess(String text)
	{
		final Message msg = Message.obtain();
		msg.what = 2;
		msg.obj = text;
		_handler.sendMessage(msg);
	}

	private final Handler _handler = new Handler() {
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what) {
			case 0: {
				final String text = (String) msg.obj;
				final Toast toast = Toast.makeText(NDNActivity.this, text,
						msg.arg1 == 0 ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG);
				toast.show();
				break;
			}
			case 1:
				final boolean status = (Boolean) msg.obj;
				Log.i(TAG, "Connection State Changed");
				if (status) {
					_conn_state = ConnectionState.STARTED;
				} else {
					_conn_state = ConnectionState.INITIAL;
				}
				enableView(status);
				break;
			case 2:
				final String text = (String) msg.obj;
				_tv_status.setText(text);
			}
		}
	};

	enum ConnectionState {
		INITIAL, STARTED
	}

	private ConnectionState _conn_state = ConnectionState.INITIAL;

	protected final static String TAG = "NDNSettingsActivity";

	private OhmagePDVManager _ohmage_PDV_manager;

	private static final String PREFS_NAME = "ccnChatPrefs";
	
	private TelephonyManager telephonyManager = null;
	
	private String DEVICE_ID = null;

	private static String OHMAGE_PHONE_NAMESPACE = null;

	private static final String DEFAULT_REC_URL = 
			"ccnx:/ucla.edu/apps/server_pdv";

	private static final String DEFAULT_REMOTEHOST = "192.168.1.4";

	private static final String DEFAULT_REMOTEPORT = "9695";

	private static final String DEFAULT_REC_AUTH = "test";

	protected static final String PREF_NAMESPACE = "namespace";

	protected static final String PREF_REC_URL = "recURL";

	protected static final String PREF_REMOTEHOST = "remotehost";

	protected static final String PREF_REMOTEPORT = "remoteport";

	protected static final String PREF_REC_AUTH = "recAuth";
	
	private static boolean ccnxConfigured = false;

	private Button _setup_stream_button, _request_pull_button, _start_pdv_button;

	private TextView _tv_status;

	private EditText _etRemoteHost, _etRemotePort, _etRecURL,
			_etRecAuth;
}
