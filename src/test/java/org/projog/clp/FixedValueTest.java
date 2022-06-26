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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

import java.util.function.Consumer;
import java.util.function.Function;

import org.testng.annotations.Test;

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

      assertSame(ExpressionResult.VALID, f.setMax(null, value));
      assertSame(ExpressionResult.VALID, f.setMax(null, value + 1));
      assertSame(ExpressionResult.INVALID, f.setMax(null, value - 1));
   }

   @Test
   public void testSetMin() {
      int value = 7;
      FixedValue f = new FixedValue(value);

      assertSame(ExpressionResult.VALID, f.setMin(null, value));
      assertSame(ExpressionResult.VALID, f.setMin(null, value - 1));
      assertSame(ExpressionResult.INVALID, f.setMin(null, value + 1));
   }

   @Test
   public void testSetNot() {
      int value = 7;
      FixedValue f = new FixedValue(value);

      assertSame(ExpressionResult.VALID, f.setNot(null, value - 1));
      assertSame(ExpressionResult.VALID, f.setNot(null, value + 1));
      assertSame(ExpressionResult.INVALID, f.setNot(null, value));
   }

   @Test
   public void testReifyMatched() {
      FixedValue f = new FixedValue(1);
      assertSame(ConstraintResult.MATCHED, f.reify(null));
   }

   @Test
   public void testReifyFailed() {
      FixedValue f = new FixedValue(0);
      assertSame(ConstraintResult.FAILED, f.reify(null));
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testReifyToHigh() {
      FixedValue f = new FixedValue(2);
      assertSame(ConstraintResult.FAILED, f.reify(null));
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testReifyToLow() {
      FixedValue f = new FixedValue(-1);
      assertSame(ConstraintResult.FAILED, f.reify(null));
   }

   @Test
   public void testEnforceMatched() {
      FixedValue f = new FixedValue(1);
      assertSame(ConstraintResult.MATCHED, f.enforce(null));
   }

   @Test
   public void testEnforceFailed() {
      FixedValue f = new FixedValue(0);
      assertSame(ConstraintResult.FAILED, f.enforce(null));
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testEnforceToHigh() {
      FixedValue f = new FixedValue(2);
      assertSame(ConstraintResult.FAILED, f.enforce(null));
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testEnforceToLow() {
      FixedValue f = new FixedValue(-1);
      assertSame(ConstraintResult.FAILED, f.enforce(null));
   }

   @Test
   public void testPreventMatched() {
      FixedValue f = new FixedValue(0);
      assertSame(ConstraintResult.MATCHED, f.prevent(null));
   }

   @Test
   public void testPreventFailed() {
      FixedValue f = new FixedValue(1);
      assertSame(ConstraintResult.FAILED, f.prevent(null));
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testPreventToHigh() {
      FixedValue f = new FixedValue(2);
      assertSame(ConstraintResult.FAILED, f.prevent(null));
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testPreventToLow() {
      FixedValue f = new FixedValue(-1);
      assertSame(ConstraintResult.FAILED, f.prevent(null));
   }

   @Test
   public void testWalk() {
      // given
      FixedValue testObject = new FixedValue(7);
      @SuppressWarnings("unchecked")
      Consumer<Expression> consumer = mock(Consumer.class);

      // when
      testObject.walk(consumer);

      // then
      verify(consumer).accept(testObject);
      verifyNoMoreInteractions(consumer);
   }

   @Test
   public void testReplaceVariables() {
      // given
      FixedValue testObject = new FixedValue(7);
      @SuppressWarnings("unchecked")
      Function<Variable, Variable> function = mock(Function.class);

      // when
      FixedValue replacement = testObject.replaceVariables(function);
      assertSame(testObject, replacement);

      // then
      verifyNoMoreInteractions(function);
   }
}
