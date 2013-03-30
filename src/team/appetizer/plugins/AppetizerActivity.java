package team.appetizer.plugins;

import team.appetizer.plugins.adlibr.AdlibrPlugin;
import team.appetizer.plugins.iab3.IAB3Plugin;
import android.content.Intent;
import android.os.Bundle;

import com.unity3d.player.UnityPlayerNativeActivity;

public class AppetizerActivity extends UnityPlayerNativeActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (IAB3Plugin.getInstance().handleActivityResult(requestCode, resultCode, data)) {
			// handled by IAB3Plugin.
			return;
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	protected void onPause() {
		AdlibrPlugin.getInstance().__onPause(); // Adlibr
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		AdlibrPlugin.getInstance().__onResume(); // Adlibr
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		// Very Important
		IAB3Plugin.getInstance().dispose();		// IAB3Plugin
		AdlibrPlugin.getInstance().__onDestroy(); // Adlibr
		super.onDestroy();
	}

}