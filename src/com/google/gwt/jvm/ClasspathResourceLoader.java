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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.TreeSet;

/**
 * Loads raw bytes from a given classloader.
 */
public class ClasspathResourceLoader implements ResourceLoader {

  private final ClassLoader classLoader;

  public ClasspathResourceLoader(ClassLoader classLoader) {
    this.classLoader = classLoader;
  }

  public ClasspathResourceLoader() {
    this(ClasspathResourceLoader.class.getClassLoader());
  }

  public byte[] loadBytes(String fileName) {
    InputStream is = classLoader.getResourceAsStream(fileName);
    if (is == null) {
      System.out.println(fileName +  " - null");
      return null;
    }
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      byte[] buf = new byte[1024 * 10];
      int len;
      while ((len = is.read(buf)) >= 0) {
        baos.write(buf, 0, len);
      }
      is.close();
      byte[] bytes2 = baos.toByteArray();
      return bytes2;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public byte[] loadClassBytes(String name) {
    byte[] bytes = loadBytes(name.replace('.', '/') + ".class");
    if (bytes == null) {
      throw new ClassResourceNotFoundException(name);
    }
    return bytes;
  }

  public Set<String> loadSet(String fileName) {
    ByteArrayInputStream is = new ByteArrayInputStream(loadBytes(fileName));
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    TreeSet<String> set = new TreeSet<String>();
    try {
      String line;
      while ((line = reader.readLine()) != null) {
        line = line.trim();
        if (line.length() > 0 && !line.startsWith("#")) {
          set.add(line);
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return set;
  }
}
