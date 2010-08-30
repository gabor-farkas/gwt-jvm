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

import com.google.common.collect.Sets;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * A mock PrefixTree. THis class is used in the MultiWordSuggestOracle which 
 * is in turns used by default in the SuggestBox unless an oracle is provided.
 * 
 * This mock uses a a treeset instead of a trie, and returns suggestions by 
 * successive calls to String.startswith
 */
public class JavaPrefixTree extends AbstractCollection<String> {

  private final Set<String> items = Sets.newTreeSet();
  
  public JavaPrefixTree(final Object prefixTree) {
    // the PrefixTree class is not visible
  }
  
  @Override
  public void clear() {
    items.clear();
  }
  
  @Override
  public Iterator<String> iterator() {
    return items.iterator();
  }

  @Override
  public int size() {
    return items.size();
  }

  @Override
  public boolean add(String s) {
    return items.add(s);
  }
  
  @Override
  public boolean contains(Object o) {
    return items.contains(o);
  }
  
  public boolean contains(String s) {
    return items.contains(s);
  }
  
  protected void suggestImpl(String search, String prefix, Collection<String> output, int limit) {
    int count = 0;
    for (String item : items) {
      if (item.startsWith(search) && count < limit) {
        output.add(item);
        ++count;
      }
      if (output.size() >= limit) {
        break;
      }
    }
  }
}
