/*
 * Copyright 2022 S. Webber
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.projog.clp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.projog.clp.TestDataParser.parseRange;

import java.util.function.Consumer;
import java.util.function.Function;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;

@RunWith(DataProviderRunner.class)
public class AbsoluteTest {
   @Test
   @DataProvider({
               "0,0", // 0
               "2,2", // +
               "-7,7", // -
               "0:43,0:43", // 0/+
               "-47:0,0:47", // -/0
               "5:42,5:42", // +/+
               "-42:-5,5:42", // -/-
               "-42:5,0:42", // -/+
               "-5:42,0:42", // -/+
               "MIN,MAX",
               "MAX,MAX",
               "MIN:MAX,0:MAX"})
   public void testGetMinMax(String input, String expected) {
      Range inputRange = parseRange(input);
      Range expectedRange = parseRange(expected);
      ClpConstraintStore.Builder b = new ClpConstraintStore.Builder();
      Variable v = b.createVariable();
      ClpConstraintStore store = b.build();
      v.setMin(store, inputRange.min());
      v.setMax(store, inputRange.max());

      Absolute a = new Absolute(v);

      assertEquals(expectedRange.min(), a.getMin(store));
      assertEquals(expectedRange.max(), a.getMax(store));
   }

   @Test
   @DataProvider({ //
               "0:100,1,1:100",
               "0:100,100,100",
               "-100:0,1,-100:-1",
               "-100:1,100,-100",
               "7:100,8,8:100",
               "-100:-7,8,-100:-8",
               "-45:7,8,-45:-8",
               "-7:45,8,8:45"})
   public void testSetMin_updated(String input, long min, String expected) {
      Range inputRange = parseRange(input);
      Range expectedRange = parseRange(expected);
      ClpConstraintStore.Builder b = new ClpConstraintStore.Builder();
      Variable v = b.createVariable();
      ClpConstraintStore store = b.build();
      v.setMin(store, inputRange.min());
      v.setMax(store, inputRange.max());

      Absolute a = new Absolute(v);
      ExpressionResult result = a.setMin(store, min);

      assertSame(ExpressionResult.UPDATED, result);
      assertEquals(expectedRange.min(), v.getMin(store));
      assertEquals(expectedRange.max(), v.getMax(store));
      assertEquals(min, a.getMin(store));
   }

   @Test
   @DataProvider({ //
               "0:100,0",
               "0:100,-1",
               "0:100,-101",
               "7:100,-1",
               "7:100,7",
               "7:100,6",
               "-100:0,0",
               "-100:0,-1",
               "-100:0,-101",
               "-100:-7,-1",
               "-100:-7,7",
               "-100:-7,6",
               "-100:100,7",
               "-100:100,0"})
   public void testSetMin_no_change(String input, long min) {
      Range inputRange = parseRange(input);
      ClpConstraintStore.Builder b = new ClpConstraintStore.Builder();
      Variable v = b.createVariable();
      ClpConstraintStore store = b.build();
      v.setMin(store, inputRange.min());
      v.setMax(store, inputRange.max());

      Absolute a = new Absolute(v);
      ExpressionResult result = a.setMin(store, min);

      assertSame(ExpressionResult.NO_CHANGE, result);
      assertEquals(inputRange.min(), v.getMin(store));
      assertEquals(inputRange.max(), v.getMax(store));
   }

   @Test
   @DataProvider({"0:100,101", "-100:0,101", "-100:100,101"})
   public void testSetMin_failed(String input, long min) {
      Range inputRange = parseRange(input);
      ClpConstraintStore.Builder b = new ClpConstraintStore.Builder();
      Variable v = b.createVariable();
      ClpConstraintStore store = b.build();
      v.setMin(store, inputRange.min());
      v.setMax(store, inputRange.max());

      Absolute a = new Absolute(v);
      ExpressionResult result = a.setMin(store, min);

      assertSame(ExpressionResult.FAILED, result);
   }

   @Test
   @DataProvider({ //
               "0:100,0,0",
               "0:100,1,0:1",
               "0:100,99,0:99",
               "-100:0,0,0",
               "-100:0,1,-1:0",
               "-100:0,99,-99:0",
               "7:100,7,7",
               "7:100,8,7:8",
               "-100:-7,7,-7",
               "-100:-7,8,-8:-7",
               "-45:7,6,-6:6",
               "-7:45,6,-6:6",
               "-45:7,8,-8:7",
               "-7:45,8,-7:8"})
   public void testSetMax_updated(String input, long max, String expected) {
      Range inputRange = parseRange(input);
      Range expectedRange = parseRange(expected);
      ClpConstraintStore.Builder b = new ClpConstraintStore.Builder();
      Variable v = b.createVariable();
      ClpConstraintStore store = b.build();
      v.setMin(store, inputRange.min());
      v.setMax(store, inputRange.max());

      Absolute a = new Absolute(v);
      ExpressionResult result = a.setMax(store, max);

      assertSame(ExpressionResult.UPDATED, result);
      assertEquals(expectedRange.min(), v.getMin(store));
      assertEquals(expectedRange.max(), v.getMax(store));
      assertEquals(max, a.getMax(store));
   }

   @Test
   @DataProvider({ //
               "0:100,101",
               "0:100,100",
               "7:100,100",
               "7:100,101",
               "-100:0,100",
               "-100:0,101",
               "-100:-7,100",
               "-100:-7,101",
               "-100:100,100",
               "-100:100,101"})
   public void testSetMax_no_change(String input, long max) {
      Range inputRange = parseRange(input);
      ClpConstraintStore.Builder b = new ClpConstraintStore.Builder();
      Variable v = b.createVariable();
      ClpConstraintStore store = b.build();
      v.setMin(store, inputRange.min());
      v.setMax(store, inputRange.max());

      Absolute a = new Absolute(v);
      ExpressionResult result = a.setMax(store, max);

      assertSame(ExpressionResult.NO_CHANGE, result);
      assertEquals(inputRange.min(), v.getMin(store));
      assertEquals(inputRange.max(), v.getMax(store));
   }

   @Test
   @DataProvider({"1:100,0", "-100:-1,0", "-100:100,-101"})
   public void testSetMax_failed(String input, long max) {
      Range inputRange = parseRange(input);
      ClpConstraintStore.Builder b = new ClpConstraintStore.Builder();
      Variable v = b.createVariable();
      ClpConstraintStore store = b.build();
      v.setMin(store, inputRange.min());
      v.setMax(store, inputRange.max());

      Absolute a = new Absolute(v);
      ExpressionResult result = a.setMax(store, max);

      assertSame(ExpressionResult.FAILED, result);
   }

   @Test
   @DataProvider({"1:3,1", "1:3,2", "1:3,2", "1,1", "-3:3,3"})
   public void testSetNot(String input, long not) {
      Range inputRange = parseRange(input);
      ClpConstraintStore.Builder b = new ClpConstraintStore.Builder();
      Variable v = b.createVariable();
      ClpConstraintStore store = b.build();
      v.setMin(store, inputRange.min());
      v.setMax(store, inputRange.max());

      Absolute a = new Absolute(v);
      ExpressionResult result = a.setNot(store, not);

      assertSame(ExpressionResult.NO_CHANGE, result);
      assertEquals(inputRange.min(), v.getMin(store));
      assertEquals(inputRange.max(), v.getMax(store));
   }

   @Test
   public void testWalk() {
      // given
      @SuppressWarnings("unchecked")
      Consumer<Expression> consumer = mock(Consumer.class);
      Expression e = mock(Expression.class);
      Absolute testObject = new Absolute(e);

      // when
      testObject.walk(consumer);

      // then
      verify(consumer).accept(testObject);
      verify(e).walk(consumer);
      verifyNoMoreInteractions(consumer, e);
   }

   @Test
   public void testReplaceVariables() {
      // given
      @SuppressWarnings("unchecked")
      Function<Variable, Variable> function = mock(Function.class);
      Expression e = mock(Expression.class);
      Absolute testObject = new Absolute(e);
      when(e.replaceVariables(function)).thenReturn(new FixedValue(42));

      // when
      Absolute replacement = testObject.replaceVariables(function);
      assertNotSame(testObject, replacement);
      assertEquals("Absolute [e=FixedValue [value=42]]", replacement.toString());

      // then
      verify(e).replaceVariables(function);
      verifyNoMoreInteractions(function, e);
   }
}
