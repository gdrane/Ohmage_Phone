/*******************************************************************************
 * Copyright 2011 The Regents of the University of California
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.ohmage;

import com.google.android.imageloader.BitmapContentHandler;
import com.google.android.imageloader.ImageLoader;
import com.google.protobuf.InvalidProtocolBufferException;

import edu.ucla.cens.pdc.libpdc.SystemState;
import edu.ucla.cens.pdc.libpdc.iApplication;
import edu.ucla.cens.pdc.libpdc.core.GlobalConfig;
import edu.ucla.cens.pdc.libpdc.exceptions.PDCTransmissionException;
import edu.ucla.cens.pdc.libpdc.stream.DataStream;

import org.ccnx.ccn.CCNHandle;
import org.ccnx.ccn.protocol.ContentName;
import org.ccnx.ccn.protocol.MalformedContentNameStringException;
import org.ohmage.db.DbHelper;
import org.ohmage.pdc.OhmagePDVManager;
import org.ohmage.pdc.OhmagePDVManagerCallback;
import org.ohmage.prompt.multichoicecustom.MultiChoiceCustomDbAdapter;
import org.ohmage.prompt.singlechoicecustom.SingleChoiceCustomDbAdapter;
import org.ohmage.triggers.glue.TriggerFramework;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.net.ContentHandler;
import java.net.URLStreamHandlerFactory;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class OhmageApplication extends Application implements iApplication {
	
	private static final String TAG = "OhmageApplication";
	
	private static final String APP_NAME = "OhmageApplication";
	
	private transient ContentName _base_uri;
	
	private transient Map<String, DataStream> _data_stream;
	
	private long _pull_rate = 0;
	
	private transient Set<String> _to_pull;
	
	private transient ScheduledExecutorService _scheduler = null;
	
	public static final String VIEW_MAP = "ohmage.intent.action.VIEW_MAP";
	
    private static final int IMAGE_TASK_LIMIT = 3;


    // 50% of available memory, up to a maximum of 32MB
    private static final long IMAGE_CACHE_SIZE = Math.min(Runtime.getRuntime().maxMemory() / 2,
            32 * 1024 * 1024);

    private ImageLoader mImageLoader;
    
	@Override
	public void onCreate() {
		super.onCreate();
		
		Log.i(TAG, "onCreate()");
		
		edu.ucla.cens.systemlog.Log.initialize(this, "Ohmage");
		
        mImageLoader = createImageLoader(this);

        int currentVersionCode = 0;
        
        try {
			currentVersionCode = getPackageManager().getPackageInfo("org.ohmage", 0).versionCode;
		} catch (NameNotFoundException e) {
			Log.e(TAG, "unable to retrieve current version code", e);
		}
		
		SharedPreferencesHelper prefs = new SharedPreferencesHelper(this);
		int lastVersionCode = prefs.getLastVersionCode();
		boolean isFirstRun = prefs.isFirstRun();
		
		if (currentVersionCode != lastVersionCode && !isFirstRun) {
			BackgroundManager.initComponents(this);
			
			prefs.setLastVersionCode(currentVersionCode);
		}
		
	}
	
	
	public void resetAll() {
		//clear everything?
		
		//clear triggers
		TriggerFramework.resetAllTriggerSettings(this);
		
		//clear shared prefs
		new SharedPreferencesHelper(this).clearAll();
		
		//clear db
		new DbHelper(this).clearAll();
		
		//clear custom type dbs
		SingleChoiceCustomDbAdapter singleChoiceDbAdapter = new SingleChoiceCustomDbAdapter(this); 
		if (singleChoiceDbAdapter.open()) {
			singleChoiceDbAdapter.clearAll();
			singleChoiceDbAdapter.close();
		}
		MultiChoiceCustomDbAdapter multiChoiceDbAdapter = new MultiChoiceCustomDbAdapter(this); 
		if (multiChoiceDbAdapter.open()) {
			multiChoiceDbAdapter.clearAll();
			multiChoiceDbAdapter.close();
		}
		
		//clear images
		try {
			Utilities.delete(getImageDirectory(this));
		} catch (IOException e) {
			Log.e(TAG, "Error deleting images", e);
		}
	}
	
    private static ImageLoader createImageLoader(Context context) {
        // Install the file cache (if it is not already installed)
        OhmageCache.install(context);
        
        // Just use the default URLStreamHandlerFactory because
        // it supports all of the required URI schemes (http).
        URLStreamHandlerFactory streamFactory = null;

        // Load images using a BitmapContentHandler
        // and cache the image data in the file cache.
        ContentHandler bitmapHandler = OhmageCache.capture(new BitmapContentHandler(), null);

        // For pre-fetching, use a "sink" content handler so that the
        // the binary image data is captured by the cache without actually
        // parsing and loading the image data into memory. After pre-fetching,
        // the image data can be loaded quickly on-demand from the local cache.
        ContentHandler prefetchHandler = OhmageCache.capture(OhmageCache.sink(), null);

        // Perform callbacks on the main thread
        Handler handler = null;
        
        return new ImageLoader(IMAGE_TASK_LIMIT, streamFactory, bitmapHandler, prefetchHandler,
                IMAGE_CACHE_SIZE, handler);
    }

    @Override
    public void onTerminate() {
        mImageLoader = null;
        super.onTerminate();
    }

	
    @Override
    public Object getSystemService(String name) {
        if (ImageLoader.IMAGE_LOADER_SERVICE.equals(name)) {
            return mImageLoader;
        } else {
            return super.getSystemService(name);
        }
    }
    
    public static File getImageDirectory(Context context) {
    	return new File(context.getExternalCacheDir(), "images");
    }

	@Override
	public String getAppName() {
		
		final PackageManager pm = this.getPackageManager();
		String packagename = this.getPackageName();
		ApplicationInfo ai = null;
		try {
			ai = pm.getApplicationInfo(packagename, 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assert ai != null;
		return (String)pm.getApplicationLabel(ai);
	}
	
	// Setup Global Config of the PDV
	public void setupObject(boolean restore) {
		PackageManager pm = getPackageManager();
		ApplicationInfo ai = getApplicationInfo();
		String name = (String) pm.getApplicationLabel(ai);
		if (restore) {
			try {
			GlobalConfig config = GlobalConfig.getInstance();
			byte[] byte_in = null;
			SystemState.Application state;
			CCNHandle handle = config.getCCNHandle();

				byte_in = config.loadConfig("applications", name);
	
			
				state = SystemState.Application.parseFrom(byte_in);
				this._pull_rate = state.getPullRate();
				setupObject();

			for (String stream_name : state.getDataStreamsList()) {
				DataStream stream = new DataStream(handle, this, stream_name, true);
				addDataStream(stream);
			}
			} catch (InvalidProtocolBufferException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			setupObject();
		}
	}
	
	public void setupObject() {
		GlobalConfig config = GlobalConfig.getInstance();
		PackageManager pm = getPackageManager();
		ApplicationInfo ai = getApplicationInfo();
		String name = (String)pm.getApplicationLabel(ai);
		try {
			_base_uri = config.getRoot().append(name);
		}
		catch (MalformedContentNameStringException ex) {
			throw new Error("Unable to set base name", ex);
		}

		_data_stream = new HashMap<String, DataStream>();
		_to_pull = new LinkedHashSet<String>();
	}
	
	SystemState.Application getObjectState()
	{
		SystemState.Application.Builder builder = 
				SystemState.Application.newBuilder();
		PackageManager pm = getPackageManager();
		ApplicationInfo ai = getApplicationInfo();
		String name = (String) pm.getApplicationLabel(ai);

		builder.setName(name);
		builder.addAllDataStreams(_data_stream.keySet());
		builder.setPullRate(_pull_rate);

		return builder.build();
	}

	public byte[] stateToByteArray()
	{
		return getObjectState().toByteArray();
	}
	
	synchronized public void periodicPullStart(long period)
	{
		if (_pull_rate > 0)
			periodicPullStop();

		Log.d(TAG, "Setting up pull for " + period + " seconds");

		_pull_rate = period;

		if (_scheduler == null || _scheduler.isTerminated())
			_scheduler = Executors.newSingleThreadScheduledExecutor();

		_scheduler.schedule(new Runnable() {
			public void run()
			{
				if (_pull_rate == 0)
					return;

				Log.d(TAG, "Performing pull...");
				performPull();
				Log.d(TAG, "Scheduling next task...");

				_scheduler.schedule(this, _pull_rate, TimeUnit.SECONDS);
			}
		}, _pull_rate, TimeUnit.SECONDS);
	}
	
	synchronized private boolean performPull()
	{
		Iterator<String> iter;
		String ds_id;
		DataStream ds;

		if (_to_pull.isEmpty())
			_to_pull.addAll(_data_stream.keySet());

		Log.d(TAG, "Calling pull; iterating through " + _to_pull.size()
				+ " entries ...");

		iter = _to_pull.iterator();
		while (iter.hasNext()) {
			ds_id = iter.next();

			ds = _data_stream.get(ds_id);
			if (ds == null)
				continue;

			if (ds.getPublisher() == null)
				continue;

			iter.remove();

			try {
				Log.d(TAG, "Making " + ds.data_stream_id + " fetch data from the uplink");
				return ds.fetchNewData();
			}
			catch (PDCTransmissionException ex) {
				Log.e(TAG, "Unable to fetch data from uplink: " + ex.getMessage());
			}
		}

		return false;
	}


	synchronized public void periodicPullStop()
	{
		if (_pull_rate == 0)
			return;

		_pull_rate = 0;
		if (_scheduler != null)
			_scheduler.shutdown();
	}

	@Override
	public ContentName getBaseName() {
		return _base_uri;
	}

	@Override
	public boolean isDataStream(String id) {	
		return _data_stream.containsKey(id);
	}

	@Override
	public DataStream getDataStream(String id) {	
		return _data_stream.get(id);
	}

	@Override
	public DataStream addDataStream(DataStream ds) {	
		return _data_stream.put(ds.data_stream_id, ds);
	}

}
