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

import com.google.gwt.http.client.URL;
import com.google.gwt.jvm.JavaGWTTestCase;

import java.io.UnsupportedEncodingException;

/**
 * Tests for JavaURL
 */
public class JavaURLTest extends JavaGWTTestCase {
  // Numbers and letters don't get encoded
  public static final String ALPHA_NUMERIC =
      "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

  // Raw version of symbols, some get encoded
  public static final String SYMBOLS = "~`!@#$%^&*()_-+={[}]|\\:;\"\'<,>.?/";

  // Encoded version of the symbols above, using the character +
  public static final String ENCODED_SYMBOLS_WITH_PLUS
      = "%7E%60%21%40%23%24%25%5E%26*%28%29_-+%3D%7B%5B%7D%5D%7C%5C%3A%3B%22%27%3C%2C%3E.%3F%2F";

  // Encoded version of the symbols above, using %2B instead of +
  public static final String ENCODED_SYMBOLS_WITHOUT_PLUS
      = "%7E%60%21%40%23%24%25%5E%26*%28%29_-%2B%3D%7B%5B%7D%5D%7C%5C%3A%3B%22%27%3C%2C%3E.%3F%2F";

  // --- Tests below ---

  @SuppressWarnings("deprecation")
  public void testMocking() {
    // simple test to ensure decode and encode are working.
    assertEquals(ALPHA_NUMERIC, URL.decode(URL.encode(ALPHA_NUMERIC)));
    assertEquals(ALPHA_NUMERIC, URL.decodeComponent(URL.encodeComponent(ALPHA_NUMERIC)));
  }

  public void testDecodeImpl() throws UnsupportedEncodingException {
    assertEquals(ALPHA_NUMERIC, JavaURL.decodeImpl(ALPHA_NUMERIC));
    assertEquals(SYMBOLS, JavaURL.decodeImpl(ENCODED_SYMBOLS_WITH_PLUS));
  }

  public void testDecodeComponentImpl() throws UnsupportedEncodingException {
    assertEquals(ALPHA_NUMERIC, JavaURL.decodeQueryStringImpl(ALPHA_NUMERIC));
    assertEquals(SYMBOLS, JavaURL.decodeQueryStringImpl(ENCODED_SYMBOLS_WITHOUT_PLUS));

    assertEquals(" ", JavaURL.decodeQueryStringImpl("+"));
  }

  public void testDecodeComponentRawImpl() throws UnsupportedEncodingException {
    assertEquals(ALPHA_NUMERIC, JavaURL.decodePathSegmentImpl(ALPHA_NUMERIC));
    assertEquals(SYMBOLS, JavaURL.decodePathSegmentImpl(ENCODED_SYMBOLS_WITH_PLUS));

    assertEquals("+", JavaURL.decodePathSegmentImpl("+"));
  }

  public void testEncodeImpl() throws UnsupportedEncodingException {
    assertEquals(ALPHA_NUMERIC, JavaURL.encodeImpl(ALPHA_NUMERIC));
    assertEquals(ENCODED_SYMBOLS_WITH_PLUS, JavaURL.encodeImpl(SYMBOLS));
  }

  public void testEncodeComponentImpl() throws UnsupportedEncodingException {
    assertEquals(ALPHA_NUMERIC, JavaURL.encodeQueryStringImpl(ALPHA_NUMERIC));
    assertEquals(ENCODED_SYMBOLS_WITH_PLUS, JavaURL.encodeQueryStringImpl(SYMBOLS));

    assertEquals("+", JavaURL.encodeQueryStringImpl(" "));
  }

  public void testEncodeComponentRawImpl() throws UnsupportedEncodingException {
    assertEquals(ALPHA_NUMERIC, JavaURL.encodePathSegmentImpl(ALPHA_NUMERIC));
    assertEquals(ENCODED_SYMBOLS_WITHOUT_PLUS, JavaURL.encodePathSegmentImpl(SYMBOLS));

    assertEquals("%2B", JavaURL.encodePathSegmentImpl(" "));
  }
}
