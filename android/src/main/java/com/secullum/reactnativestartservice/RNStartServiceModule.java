package com.secullum.reactnativestartservice;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
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
import com.facebook.react.bridge.WritableArray;
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
  public void isPackageInstalledAsync(String packageName, Promise promise) {
    try {
      ApplicationInfo applicationInfo = reactContext.getPackageManager().getApplicationInfo(packageName, 0);
      promise.resolve(applicationInfo.enabled);
    } catch (PackageManager.NameNotFoundException e) {
      promise.resolve(false);
    } catch (Exception e) {
      promise.reject(e);
    }
  }

  @ReactMethod
  public void startAsync(String packageName, String className, String action, ReadableMap params, Promise promise) {
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
    if (params == null) {
      return;
    }

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
          } else if (value instanceof String[]) {
            WritableArray array = Arguments.createArray();

            for (String item : (String[])value) {
              array.pushString(item);
            }

            result.putArray(key, array);
          } else if (value instanceof int[]) {
            WritableArray array = Arguments.createArray();

            for (int item : (int[])value) {
              array.pushInt(item);
            }

            result.putArray(key, array);
          } else if (value instanceof double[]) {
            WritableArray array = Arguments.createArray();

            for (double item : (double[])value) {
              array.pushDouble(item);
            }

            result.putArray(key, array);
          } else if (value instanceof boolean[]) {
            WritableArray array = Arguments.createArray();

            for (boolean item : (boolean[])value) {
              array.pushBoolean(item);
            }

            result.putArray(key, array);
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
