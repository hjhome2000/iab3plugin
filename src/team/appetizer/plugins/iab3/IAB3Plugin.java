package team.appetizer.plugins.iab3;

import java.util.ArrayList;
import java.util.List;

import team.appetizer.plugins.iab3.util.IabException;
import team.appetizer.plugins.iab3.util.IabHelper;
import team.appetizer.plugins.iab3.util.IabHelper.OnConsumeFinishedListener;
import team.appetizer.plugins.iab3.util.IabHelper.OnIabPurchaseFinishedListener;
import team.appetizer.plugins.iab3.util.IabHelper.OnIabSetupFinishedListener;
import team.appetizer.plugins.iab3.util.IabResult;
import team.appetizer.plugins.iab3.util.Inventory;
import team.appetizer.plugins.iab3.util.Purchase;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class IAB3Plugin {

	// Singleton
	private static IAB3Plugin instance = null;
	public static IAB3Plugin getInstance() {
		if (instance == null)
			instance = new IAB3Plugin();
		
		return instance;
	}
	// ***
	
	// Logger
	private static void Log (String msg) {
		Log.d("IAB3Plugin", msg);
	}
	// ***
	
	private IabHelper iabHelper = null;
	private Activity unityActivity = null;
	private IAB3UnityListener unityListener = new IAB3UnityListener();
	
	// for android side
	public boolean handleActivityResult(int requestCode, int resultCode, Intent data) {
		if (iabHelper == null)
			return false;
		
		return iabHelper.handleActivityResult(requestCode, resultCode, data);
	}
	
	public void dispose() {
		if (iabHelper == null)
			return;
		
		iabHelper.dispose();
	}
	
	// for unity side
	public void create(Activity activity, String publicKey, boolean debug) {
		Log("create");
		
		unityActivity = activity;
		
		iabHelper = new IabHelper(activity, publicKey);
		iabHelper.enableDebugLogging(debug);
		
		iabHelper.startSetup(new OnIabSetupFinishedListener() {
			@Override
			public void onIabSetupFinished(IabResult result) {
				Log("iabHelper.onIabSetupFinished");
				if (!result.isSuccess()) {
					Log("SetupFailed");
					// Failed
					unityListener.setupCompleted(IAB3UnityListener.RESULT_FAILED);
					return;
				}
				Log("SetupSucceed");
				unityListener.setupCompleted(IAB3UnityListener.RESULT_SUCCEED);
			}
		});
	}
	
	// Consume remaining item
	public void consumeRemains(String productId) {
		Log("consumeRemains");
		final String sku = productId;
		
		if (iabHelper == null)
			return;
		
		Log("iabHelper.queryInventory");
		List<String> skus = new ArrayList<String>();
		skus.add(sku);
		
		try {
			Inventory inv = iabHelper.queryInventory(false, skus);
			if (inv == null)
				return;
			
			final Purchase purchase = inv.getPurchase(sku);
			
			if (purchase == null) {
				Log("purchase is null");
				return;
			}
			
			// Consume
			Log("iabHelper.consumeAsync");
			// Creates a Handler with MainLooper
			// (because this is called by GLThread, that has no looper inside the thread)
			Handler handler = new Handler(Looper.getMainLooper());
			handler.post(new Runnable() {
				@Override
				public void run() {
					iabHelper.consumeAsync(purchase, new OnConsumeFinishedListener() {
						@Override
						public void onConsumeFinished(Purchase purchase, IabResult result) {
							Log("iabHelper.onConsumeFinished");
							if (!result.isSuccess())
								return;
							// Succeed
							Log("consumeCompleted");
							unityListener.consumeCompleted(purchase.getSku());
						}
					});
				}
			});
		} catch (IabException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Purchase sequence
	 * 1. Send purchase.
	 * 2. Receive message from onActivityResult.
	 * 3. If Purchase failed, send failed message.
	 *    If Purchase succeed, send consume.
	 * 4. If Consume failed, send failed message.
	 *    If Consume succeed, finally send succeed. 
	 */
	private static final int REQUEST_PURCHASE = 100001;
	private OnIabPurchaseFinishedListener purchaseFinishedListener = 
		new OnIabPurchaseFinishedListener() {
		@Override
		public void onIabPurchaseFinished(IabResult result, Purchase info) {
			int response = result.getResponse();
			// Failed (info is null)
			if (!result.isSuccess()) {
				if (response == IabHelper.IABHELPER_USER_CANCELLED) {
					// User Cancelled
					unityListener.purchaseCancelled("");
				} else {
					// Failed by unknown reasons
					unityListener.purchaseFailed("");
				}
				
				return;
			}
			
			// Succeed
			// - Let's Consume purchased product.
			iabHelper.consumeAsync(info, consumeFinishedListener);
		}
	};
	
	private OnConsumeFinishedListener consumeFinishedListener =
		new OnConsumeFinishedListener() {
		@Override
		public void onConsumeFinished(Purchase purchase, IabResult result) {
			if (!result.isSuccess()) {
				// Failed
				unityListener.purchaseFailed("");
				return;
			}
			String productId = purchase.getSku();
			
			// Succeed
			unityListener.purchaseSucceed(productId);
		}
	};
	
	public void purchase(String productId) {
		iabHelper.launchPurchaseFlow(unityActivity, productId, REQUEST_PURCHASE, 
									purchaseFinishedListener);
	}
	
}
