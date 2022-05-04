package com.secullum.reactnativestartservice;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

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
}
