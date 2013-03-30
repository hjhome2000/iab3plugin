package team.appetizer.plugins.adlibr;

import java.util.HashMap;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import com.mocoplex.adlib.AdlibAdViewContainer;
import com.mocoplex.adlib.AdlibConfig;
import com.mocoplex.adlib.AdlibManager;
import com.unity3d.player.UnityPlayer;

public class AdlibrPlugin {
	
	private static AdlibrPlugin instance = null;
	
	public static AdlibrPlugin getInstance() {
		if (instance == null)
			instance = new AdlibrPlugin();
		
		return instance;
	}
	
	AdlibManager adlibManager;
	AdlibAdViewContainer adView;
	
	// Called before setup
	public void setupKey(String platform, String key) {
		platformKeys.put(platform, key);
	}
	
	public void setup(String apiKey) {
		final Activity activity = UnityPlayer.currentActivity;
		
		// Platform Binding
        AdlibConfig.getInstance().bindPlatform("CAULY","team.appetizer.plugins.adlibr.subview.SubAdlibAdViewCauly");
        // ***
        
        // API Key setup
        AdlibConfig.getInstance().setAdlibKey(apiKey);
        
        // Create View
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				adlibManager = new AdlibManager();
				adlibManager.onCreate(activity);
				
				// And this is the same, but done programmatically 
		        LinearLayout layout = new LinearLayout(UnityPlayer.currentActivity.getApplicationContext());
		        layout.setOrientation(LinearLayout.HORIZONTAL); 
		         
		        layout.setGravity(Gravity.TOP); 
		        activity.addContentView(layout, 
		        						new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		        
		        // Create adView
		        adView = new AdlibAdViewContainer(activity);
		        layout.addView(adView);
		        
		        adlibManager.bindAdsContainer(adView);
		        adView.setVisibility(View.GONE);
			}
		});
	}
	
	public void show() {
		UnityPlayer.currentActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				adView.setVisibility(View.VISIBLE);
			}
		});
	}
	
	public void hide() {
		UnityPlayer.currentActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				adView.setVisibility(View.GONE);
			}
		});
	}
	
	/*
	 * SubView Operations
	 */
	HashMap<String, String> platformKeys = new HashMap<String, String>();
	public String __getAPIKey (String platform) {
		if (platformKeys.containsKey(platform))
			return platformKeys.get(platform);
		
		return "TEST";
	}
	
	/*
	 * Activity Operations
	 */
	public void __onResume() {
		if (adlibManager == null)
			return;
		adlibManager.onResume(UnityPlayer.currentActivity);
	}
	
	public void __onPause() {
		if (adlibManager == null)
			return;
		adlibManager.onPause();
	}
	
	public void __onDestroy() {
		if (adlibManager == null)
			return;
		adlibManager.onDestroy(UnityPlayer.currentActivity);
	}
	
}
