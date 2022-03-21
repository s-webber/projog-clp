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

public class FixedValueTest {
   @Test
   public void testGetters() {
      int value = 7;
      FixedValue f = new FixedValue(value);

      assertEquals(value, f.getMin(null));
      assertEquals(value, f.getMax(null));
   }

   @Test
   public void testSetMax() {
      int value = 7;
      FixedValue f = new FixedValue(value);

      assertSame(ExpressionResult.NO_CHANGE, f.setMax(null, value));
      assertSame(ExpressionResult.NO_CHANGE, f.setMax(null, value + 1));
      assertSame(ExpressionResult.FAILED, f.setMax(null, value - 1));
   }

   @Test
   public void testSetMin() {
      int value = 7;
      FixedValue f = new FixedValue(value);

      assertSame(ExpressionResult.NO_CHANGE, f.setMin(null, value));
      assertSame(ExpressionResult.NO_CHANGE, f.setMin(null, value - 1));
      assertSame(ExpressionResult.FAILED, f.setMin(null, value + 1));
   }

   @Test
   public void testWalk() {
      // given
      Consumer<Expression> consumer = mock(Consumer.class);
      FixedValue testObject = new FixedValue(7);

      // when
      testObject.walk(consumer);

      // then
      verify(consumer).accept(testObject);
      verifyNoMoreInteractions(consumer);
   }

   @Test
   public void testReplace_null() {
      // given
      FixedValue testObject = new FixedValue(7);
      Function<Expression, Expression> function = mock(Function.class);
      when(testObject.replace(function)).thenReturn(null);

      // when
      Expression replacement = testObject.replace(function);
      assertSame(testObject, replacement);

      // then
      verify(function).apply(testObject);
      verifyNoMoreInteractions(function);
   }

   @Test
   public void testReplace_replacement() {
      // given
      FixedValue testObject = new FixedValue(7);
      FixedValue expectedReplacement = new FixedValue(180);
      Function<Expression, Expression> function = mock(Function.class);
      when(testObject.replace(function)).thenReturn(expectedReplacement);

      // when
      Expression replacement = testObject.replace(function);
      assertSame(expectedReplacement, replacement);

      // then
      verify(function).apply(testObject);
      verifyNoMoreInteractions(function);
   }
}
