/* Copyright 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.gwt.jvm.mock;

import java.util.Date;
import java.util.HashMap;

import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.google.gwt.jvm.GwtBrowserEmulator;

/**
 * Mock browser cookies implementation. Built on top of HtmlUnit's
 * CookieManager.
 * 
 * Note: JavaCookies currently respects neither the secure flag nor domains. 
 */
public class JavaCookies {
  // This is reset by GwtBrowserEmulator.reset()
  private static CookieManager currentCookieManager;
  
  public static CookieManager getCookieManager() {
    return currentCookieManager;
  }

  public static void reset(GwtBrowserEmulator browser) {
    currentCookieManager = browser.getCookieManager();
  }
  
  static void loadCookies(HashMap<String, String> m) {
    for (Cookie c : getCookieManager().getCookies()) {
      m.put(c.getName(), c.getValue());
    }
  }
  
  @SuppressWarnings("unused")
  private static boolean needsRefresh() {
    return true;
  }

  @SuppressWarnings("unused")
  private static void removeCookieNative(String name) {
    CookieManager manager = getCookieManager();
    manager.removeCookie(manager.getCookie(name));
  }
  
  public static void removeCookieNative(String name, String path) {
    CookieManager manager = getCookieManager();
    for (Cookie c : manager.getCookies()) {
      if (c.getName().equalsIgnoreCase(name)
          && c.getPath().equals(path)) {
        manager.removeCookie(c);
      }
    }
  }
  
  @SuppressWarnings("unused")
  private static void setCookieImpl(String name, String value,
      double expires, String domain, String path, boolean secure) {
    Cookie c = new Cookie(domain, name, value,
        path, new Date((long) expires), secure);
    
    getCookieManager().addCookie(c);
  }

  @SuppressWarnings("unused")
  private static String uriEncode(String s) {
    // There's no need to URI encode cookie names.
    return s;
  }
}
