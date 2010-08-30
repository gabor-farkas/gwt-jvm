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

import com.google.common.collect.Lists;
import com.google.gwt.jvm.JavaGWTTestCase;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Callback;
import com.google.gwt.user.client.ui.SuggestOracle.Request;
import com.google.gwt.user.client.ui.SuggestOracle.Response;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

import java.util.Collection;
import java.util.List;

/**
 * Tests for {@link JavaPrefixTree}.
 */
public class JavaPrefixTreeTest extends JavaGWTTestCase {
  
  /**
   * A multi-word suggest box uses the prefix tree internally
   */
  public void testMultiWordSuggetBox() {
    MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();
    oracle.add("str1");
    oracle.add("str2");
    oracle.add("str3");
    oracle.add("str4");
    
    // interesting enough, the multiword suggestbox returns limit +1 items
    final int limit = 3;
    final Request suggestionRequest = new Request("str", limit);
    Callback callback = new Callback() {
      @Override
      public void onSuggestionsReady(Request request, Response response) {
        assertEquals(suggestionRequest, request);
        Collection<? extends Suggestion> suggestions = response.getSuggestions();
        assertEquals(limit + 1, response.getSuggestions().size());
      }
    };
    
    oracle.requestSuggestions(suggestionRequest, callback);
  }
  
  public void testGetSuggestions() {
    JavaPrefixTree tree = new JavaPrefixTree(null);
    tree.add("str1");
    tree.add("str2");
    tree.add("str3");
    tree.add("int1");
    tree.add("int2");
    
    // number of suggestions is limited by the limit parameter
    List<String> suggestions = Lists.newArrayList();
    tree.suggestImpl("str", "", suggestions, 2);
    assertEquals(2, suggestions.size());
    assertEquals("str1", suggestions.get(0));
    assertEquals("str2", suggestions.get(1));
    
    // number of suggestions can be less than the limit
    suggestions.clear();
    tree.suggestImpl("str", "", suggestions, 5);
    assertEquals(3, suggestions.size());
    
    // an empty string is a prefix of everything
    suggestions.clear();
    tree.suggestImpl("", "", suggestions, 10);
    assertEquals(5, suggestions.size());
    
    // nothing is returned when the string is not found
    suggestions.clear();
    tree.suggestImpl("foo", "", suggestions, 4);
    assertEquals(0, suggestions.size());
  }
}
