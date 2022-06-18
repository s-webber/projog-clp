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
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.function.Consumer;
import java.util.function.Function;

import org.junit.Test;

public final class VariableTest {
   @Test
   public void testGetId() {
      assertEquals(0, new Variable(0).getId());
      assertEquals(1, new Variable(1).getId());
      assertEquals(2, new Variable(2).getId());
      assertEquals(Integer.MAX_VALUE, new Variable(Integer.MAX_VALUE).getId());
   }

   @Test
   public void testGetMin() {
      Variable testObject = new Variable(1);
      ConstraintStore e = mock(ConstraintStore.class);
      long expected = 7;

      when(e.getMin(testObject)).thenReturn(expected);

      assertEquals(expected, testObject.getMin(e));

      verify(e).getMin(testObject);
      verifyNoMoreInteractions(e);
   }

   @Test
   public void testGetMax() {
      Variable testObject = new Variable(1);
      ConstraintStore e = mock(ConstraintStore.class);
      long expected = 7;

      when(e.getMax(testObject)).thenReturn(expected);

      assertEquals(expected, testObject.getMax(e));

      verify(e).getMax(testObject);
      verifyNoMoreInteractions(e);
   }

   @Test
   public void testSetValue() {
      Variable testObject = new Variable(1);
      ConstraintStore e = mock(ConstraintStore.class);
      long value = 7;
      ExpressionResult expected = ExpressionResult.UPDATED;

      when(e.setValue(testObject, value)).thenReturn(expected);

      assertEquals(expected, testObject.setValue(e, value));

      verify(e).setValue(testObject, value);
      verifyNoMoreInteractions(e);
   }

   @Test
   public void testSetNot() {
      Variable testObject = new Variable(1);
      ConstraintStore e = mock(ConstraintStore.class);
      long value = 7;
      ExpressionResult expected = ExpressionResult.FAILED;

      when(e.setNot(testObject, value)).thenReturn(expected);

      assertEquals(expected, testObject.setNot(e, value));

      verify(e).setNot(testObject, value);
      verifyNoMoreInteractions(e);
   }

   @Test
   public void testSetMin() {
      Variable testObject = new Variable(1);
      ConstraintStore e = mock(ConstraintStore.class);
      long value = 7;
      ExpressionResult expected = ExpressionResult.NO_CHANGE;

      when(e.setMin(testObject, value)).thenReturn(expected);

      assertEquals(expected, testObject.setMin(e, value));

      verify(e).setMin(testObject, value);
      verifyNoMoreInteractions(e);
   }

   @Test
   public void testSetMax() {
      Variable testObject = new Variable(1);
      ConstraintStore e = mock(ConstraintStore.class);
      long value = 7;
      ExpressionResult expected = ExpressionResult.UPDATED;

      when(e.setMax(testObject, value)).thenReturn(expected);

      assertEquals(expected, testObject.setMax(e, value));

      verify(e).setMax(testObject, value);
      verifyNoMoreInteractions(e);
   }

   @Test
   public void testWalk() {
      // given
      @SuppressWarnings("unchecked")
      Consumer<Expression> consumer = mock(Consumer.class);
      Variable testObject = new Variable(1);

      // when
      testObject.walk(consumer);

      // then
      verify(consumer).accept(testObject);
      verifyNoMoreInteractions(consumer);
   }

   @Test
   public void testReplaceVariables_null() {
      // given
      @SuppressWarnings("unchecked")
      Function<Variable, Variable> function = mock(Function.class);
      Variable testObject = new Variable(1);
      when(function.apply(testObject)).thenReturn(null);

      // when
      Expression replacement = testObject.replaceVariables(function);
      assertSame(testObject, replacement);

      // then
      verify(function).apply(testObject);
      verifyNoMoreInteractions(function);
   }

   @Test
   public void testReplaceVariables_other() {
      // given
      @SuppressWarnings("unchecked")
      Function<Variable, Variable> function = mock(Function.class);
      Variable testObject = new Variable(1);
      Variable expected = new Variable(30);
      when(function.apply(testObject)).thenReturn(expected);

      // when
      Expression replacement = testObject.replaceVariables(function);
      assertSame(expected, replacement);

      // then
      verify(function).apply(testObject);
      verifyNoMoreInteractions(function);
   }
}