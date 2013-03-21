package team.appetizer.plugins.iab3;

import com.unity3d.player.UnityPlayer;

public class IAB3UnityListener {
	private final static String UNITY_OBJECT = "IAB3PluginCallback";
	public final static String RESULT_SUCCEED = "SUCCEED";
	public final static String RESULT_FAILED = "FAILED";
	
	public void purchaseSucceed(String productId) {
		UnityPlayer.UnitySendMessage(UNITY_OBJECT, "purchaseSucceed", productId);
	}
	
	public void purchaseFailed(String productId) {
		UnityPlayer.UnitySendMessage(UNITY_OBJECT, "purchaseFailed", productId);
	}
	
	public void purchaseCancelled(String productId) {
		UnityPlayer.UnitySendMessage(UNITY_OBJECT, "purchaseCancelled", productId);
	}
	
	public void consumeCompleted(String productId) {
		UnityPlayer.UnitySendMessage(UNITY_OBJECT, "consumeCompleted", productId);
	}
	
	public void setupCompleted(String result) {
		UnityPlayer.UnitySendMessage(UNITY_OBJECT, "setupCompleted", result);
	}
}
