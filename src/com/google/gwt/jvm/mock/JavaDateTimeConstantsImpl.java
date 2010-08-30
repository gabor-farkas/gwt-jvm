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

import com.google.gwt.i18n.client.constants.DateTimeConstants;

/**
 * Mocks {@link DateTimeConstants} implementation which encapsulates
 * a collection of DateTime formatting with static Strings.
 */
@SuppressWarnings("deprecation")
public class JavaDateTimeConstantsImpl implements DateTimeConstants {
  @Override
  public String[] weekendRange() {
    return new String[] {"5", "6"};
  }

  @Override
  public String[] weekdays() {
    return new String[] {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday",
                         "Friday", "Saturday"};
  }

  @Override
  public String[] timeFormats() {
    return new String[] {"h:mm:ss a zzzz", "h:mm:ss a z", "h:mm:ss a", "h:mm a"};
  }

  @Override
  public String[] standaloneWeekdays() {
    return weekdays();
  }

  @Override
  public String[] standaloneShortWeekdays() {
    return new String[] {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
  }

  @Override
  public String[] standaloneShortMonths() {
    return new String[] {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep",
                          "Oct", "Nov", "Dec"};
  }

  @Override
  public String[] standaloneNarrowWeekdays() {
    return new String[] {"S", "M", "T", "W", "T", "F", "S"};
  }

  @Override
  public String[] standaloneNarrowMonths() {
    return new String[] {"J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D"};
  }

  @Override
  public String[] standaloneMonths() {
    return new String[] {"January", "February", "March", "April", "May", "June", "July",
                         "August", "September", "October", "November", "December"};
  }

  @Override
  public String[] shortWeekdays() {
    return new String[] {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
  }

  @Override
  public String[] shortQuarters() {
    return new String[] {"Q1", "Q2", "Q3", "Q4"};
  }

  @Override
  public String[] shortMonths() {
    return new String[] {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep",
                         "Oct", "Nov", "Dec"};
  }

  @Override
  public String[] quarters() {
    return new String[] {"1st quarter", "2nd quarter", "3rd quarter", "4th quarter"};
  }

  @Override
  public String[] narrowWeekdays() {
    return new String[] {"S", "M", "T", "W", "T", "F", "S"};
  }

  @Override
  public String[] narrowMonths() {
    return new String[] {"J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D"};
  }

  @Override
  public String[] months() {
    return new String[] {"January", "February", "March", "April", "May", "June", "July",
               "August", "September", "October", "November", "December"};
  }

  @Override
  public String firstDayOfTheWeek() {
    return "6";
  }

  @Override
  public String[] eras() {
    return new String[] {"BC", "AD"};
  }

  @Override
  public String[] eraNames() {
    return new String[] {"Before Christ", "Anno Domini"};
  }

  @Override
  public String[] dateFormats() {
    return new String[] {"EEEE, MMMM d, y", "MMMM d, y", "MMM d, y", "M/d/yy"};
  }

  @Override
  public String[] ampms() {
    return new String[] {"AM", "PM"};
  }
}
