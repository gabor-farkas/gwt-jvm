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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class JavaURL {
  public JavaURL(Object url) {}

  public static String decodeImpl(String url) throws UnsupportedEncodingException {
    return URLDecoder.decode(url.replace("+", "%2B"), "UTF8");
  }

  public static String decodeQueryStringImpl(String url) throws UnsupportedEncodingException {
    return decodePathSegmentImpl(url.replace("+", "%20"));
  }

  public static String decodePathSegmentImpl(String url) throws UnsupportedEncodingException {
    return decodeImpl(url);
  }

  public static String encodeQueryStringImpl(String url) throws UnsupportedEncodingException {
    return encodeImpl(url);
  }

  public static String encodePathSegmentImpl(String url) throws UnsupportedEncodingException {
    return encodeQueryStringImpl(url).replace("+", "%2B");
  }

  public static String encodeImpl(String url) throws UnsupportedEncodingException {
    return URLEncoder.encode(url, "UTF8").replace("%2B", "+");
  }

  // No longer used, kept around for backwards compatibility.
  public static String decodeComponentImpl(String url) throws UnsupportedEncodingException {
    return decodeComponentRawImpl(url.replace("+", "%20"));
  }

  public static String decodeComponentRawImpl(String url) throws UnsupportedEncodingException {
    return decodeImpl(url);
  }

  public static String encodeComponentImpl(String url) throws UnsupportedEncodingException {
    return encodeImpl(url);
  }

  public static String encodeComponentRawImpl(String url) throws UnsupportedEncodingException {
    return encodeComponentImpl(url).replace("+", "%2B");
  }
}
