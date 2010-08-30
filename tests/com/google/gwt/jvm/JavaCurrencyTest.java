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
package com.google.gwt.jvm;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.CurrencyData;
import com.google.gwt.i18n.client.CurrencyList;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.i18n.client.constants.CurrencyCodeMapConstants;
import com.google.gwt.i18n.client.constants.NumberConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * Tests of GWT i18n Currency constants and implementations.
 */
public class JavaCurrencyTest extends JavaGWTTestCase {

  private NumberConstants numberConstants;
  private Map<?, ?> map;
  private CurrencyList currencyList;

  public void testNumberConstants() {
    numberConstants = LocaleInfo.getCurrentLocale().getNumberConstants();
    assertEquals(numberConstants.defCurrencyCode(), "USD");
  }

  @SuppressWarnings("cast")
  public void testCurrencyList() {
    currencyList = GWT.create(CurrencyList.class);
    assertTrue(currencyList.getDefault() instanceof CurrencyData);
  }

  public void testCurrencyCodeMapConstants() {
    map = ((CurrencyCodeMapConstants) GWT.create(CurrencyCodeMapConstants.class))
        .currencyMap();
    assertEquals(map, new HashMap<String, String>());
  }

}
