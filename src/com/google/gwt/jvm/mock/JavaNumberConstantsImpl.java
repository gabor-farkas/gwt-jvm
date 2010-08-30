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

import com.google.gwt.i18n.client.constants.NumberConstants;

/**
 * Mocks {@link NumberConstants} implementation which encapsulates
 * a collection of Number formatting with static Strings.
 */
public class JavaNumberConstantsImpl implements NumberConstants{

  public String percentPattern() {
    return "#,##0%";
  }

  public String zeroDigit() {
    return "0";
  }

  public String scientificPattern() {
    return "#E0";
  }

  public String decimalSeparator() {
    return ".";
  }

  public String notANumber() {
    return "NaN";
  }

  public String minusSign() {
    return "-";
  }

  public String infinity() {
    return "\u221e";
  }

  public String exponentialSymbol() {
    return "E";
  }

  public String plusSign() {
    return "+";
  }

  public String currencyPattern() {
    return "¤#,##0.00;(¤#,##0.00)";
  }

  public String monetaryGroupingSeparator() {
    return ",";
  }

  public String groupingSeparator() {
    return ",";
  }

  public String perMill() {
    return "\u2030";
  }

  public String monetarySeparator() {
    return ".";
  }

  public String decimalPattern() {
    return "#,##0.###";
  }

  public String percent() {
    return "%";
  }

  public String defCurrencyCode() {
    return "USD";
  }

}
