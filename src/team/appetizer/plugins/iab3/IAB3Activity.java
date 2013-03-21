package team.appetizer.plugins.iab3;

import android.content.Intent;
import android.os.Bundle;

import com.unity3d.player.UnityPlayerNativeActivity;

public class IAB3Activity extends UnityPlayerNativeActivity {
	
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

}
