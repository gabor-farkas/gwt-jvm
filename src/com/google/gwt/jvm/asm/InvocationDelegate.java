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
package com.google.gwt.jvm.asm;

import static java.lang.reflect.Modifier.isStatic;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Delegate which simplifies the generation of bytecodes for the GWTClassLoader.
 */
public class InvocationDelegate {

  private final Object delegate;
  private final Method method;
  private final List<Object> arguments = new ArrayList<Object>();

  public InvocationDelegate(Object delegate, Method method) {
    this.delegate = delegate;
    this.method = method;
    if (!isStatic(method.getModifiers()) && delegate == null) {
      throw new IllegalStateException();
    }
  }

  public boolean invokeReturnZ() throws IllegalArgumentException, IllegalAccessException,
      InvocationTargetException {
    return (Boolean) method.invoke(delegate, args());
  }

  public byte invokeReturnB() throws IllegalArgumentException, IllegalAccessException,
      InvocationTargetException {
    return (Byte) method.invoke(delegate, args());
  }

  public short invokeReturnS() throws IllegalArgumentException, IllegalAccessException,
    InvocationTargetException {
    return (Short) method.invoke(delegate, args());
  }
  
  public char invokeReturnC() throws IllegalArgumentException, IllegalAccessException,
      InvocationTargetException {
    return (Character) method.invoke(delegate, args());
  }

  public int invokeReturnI() throws IllegalArgumentException, IllegalAccessException,
      InvocationTargetException {
    return (Integer) method.invoke(delegate, args());
  }

  public long invokeReturnJ() throws IllegalArgumentException, IllegalAccessException,
      InvocationTargetException {
    return (Long) method.invoke(delegate, args());
  }

  public float invokeReturnF() throws IllegalArgumentException, IllegalAccessException,
      InvocationTargetException {
    return (Float) method.invoke(delegate, args());
  }

  public double invokeReturnD() throws IllegalArgumentException, IllegalAccessException,
      InvocationTargetException {
    return (Double) method.invoke(delegate, args());
  }

  public Object invokeReturnObject() throws IllegalArgumentException, IllegalAccessException,
      InvocationTargetException {
    return method.invoke(delegate, args());
  }

  public void invokeReturnV() throws IllegalArgumentException, IllegalAccessException,
      InvocationTargetException {
    method.invoke(delegate, args());
  }

  public void addArg(boolean arg) {
    arguments.add(arg);
  }

  public void addArg(byte arg) {
    arguments.add(arg);
  }

  public void addArg(char arg) {
    arguments.add(arg);
  }

  public void addArg(short arg) {
    arguments.add(arg);
  }

  public void addArg(int arg) {
    arguments.add(arg);
  }

  public void addArg(long arg) {
    arguments.add(arg);
  }

  public void addArg(float arg) {
    arguments.add(arg);
  }

  public void addArg(double arg) {
    arguments.add(arg);
  }

  public void addArg(Object arg) {
    arguments.add(arg);
  }

  private Object[] args() {
    return arguments.toArray();
  }

}
