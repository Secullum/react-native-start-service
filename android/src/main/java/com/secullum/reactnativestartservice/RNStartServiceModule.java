package com.secullum.reactnativestartservice;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.ResultReceiver;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.WritableMap;

import java.util.Set;

public class RNStartServiceModule extends ReactContextBaseJavaModule {
  private final ReactApplicationContext reactContext;

  public RNStartServiceModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNStartService";
  }

  @ReactMethod
  public void start(String packageName, String className, String action, ReadableMap params, Promise promise) {
    try {
      Intent intent = new Intent();

      intent.setComponent(new ComponentName(packageName, className));
      intent.setAction(action);

      setParams(intent, params);
      setReceiver(intent, promise);

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        reactContext.startForegroundService(intent);
      } else {
        reactContext.startService(intent);
      }
    } catch (Exception e) {
      promise.reject(e);
    }
  }

  private void setParams(Intent intent, ReadableMap params) {
    Bundle bundle = new Bundle();
    ReadableMapKeySetIterator iterator = params.keySetIterator();

    while (iterator.hasNextKey()) {
      String key = iterator.nextKey();
      ReadableType type = params.getType(key);

      switch (type) {
        case String:
          bundle.putString(key, params.getString(key));
          break;
        case Number:
          bundle.putDouble(key, params.getDouble(key));
          break;
        case Boolean:
          bundle.putBoolean(key, params.getBoolean(key));
          break;
      }
    }

    intent.putExtra("params", bundle);
  }

  private void setReceiver(Intent intent, final Promise promise) {
    intent.putExtra("receiver", receiverForSending(new ResultReceiver(new Handler()) {
      @Override
      protected void onReceiveResult(int resultCode, Bundle resultData) {
        WritableMap result = Arguments.createMap();
        Set<String> keys = resultData.keySet();

        for (String key: keys) {
          Object value = resultData.get(key);

          if (value instanceof String) {
            result.putString(key, (String)value);
          } else if (value instanceof Double) {
            result.putDouble(key, (Double)value);
          } else if (value instanceof Boolean) {
            result.putBoolean(key, (Boolean)value);
          }
        }

        promise.resolve(result);
      }
    }));
  }

  // https://stackoverflow.com/a/12183036
  private static ResultReceiver receiverForSending(ResultReceiver actualReceiver) {
    Parcel parcel = Parcel.obtain();
    actualReceiver.writeToParcel(parcel,0);
    parcel.setDataPosition(0);
    ResultReceiver receiverForSending = ResultReceiver.CREATOR.createFromParcel(parcel);
    parcel.recycle();
    return receiverForSending;
  }
}
