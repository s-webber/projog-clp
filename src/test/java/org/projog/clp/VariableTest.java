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
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.fail;

import java.util.function.Consumer;
import java.util.function.Function;

import org.testng.annotations.Test;

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
      ExpressionResult expected = ExpressionResult.VALID;

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
      ExpressionResult expected = ExpressionResult.INVALID;

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
      ExpressionResult expected = ExpressionResult.VALID;

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
      ExpressionResult expected = ExpressionResult.VALID;

      when(e.setMax(testObject, value)).thenReturn(expected);

      assertEquals(expected, testObject.setMax(e, value));

      verify(e).setMax(testObject, value);
      verifyNoMoreInteractions(e);
   }

   @Test
   public void testReifyMatched() {
      Variable v = new Variable(99);
      ConstraintStore s = new DummyConstraintStore(v, 1);
      assertSame(ConstraintResult.MATCHED, v.reify(s));
   }

   @Test
   public void testReifyFailed() {
      Variable v = new Variable(99);
      ConstraintStore s = new DummyConstraintStore(v, 0);
      assertSame(ConstraintResult.FAILED, v.reify(s));
   }

   @Test
   public void testReifyUnresolved() {
      Variable v = new Variable(99);
      ConstraintStore s = new DummyConstraintStore(v, 0, 1);
      assertSame(ConstraintResult.UNRESOLVED, v.reify(s));
   }

   @Test
   public void testReifyToHigh() {
      Variable v = new Variable(99);
      ConstraintStore s = new DummyConstraintStore(v, 2);
      try {
         v.reify(s);
         fail();
      } catch (IllegalStateException e) {
         assertEquals("Expected 0 or 1 but got 2", e.getMessage());
      }
   }

   @Test
   public void testReifyToLow() {
      Variable v = new Variable(99);
      ConstraintStore s = new DummyConstraintStore(v, -1);
      try {
         v.reify(s);
         fail();
      } catch (IllegalStateException e) {
         assertEquals("Expected 0 or 1 but got -1", e.getMessage());
      }
   }

   @Test
   public void testEnforceMatched() {
      Variable v = new Variable(99);
      ConstraintStore s = new DummyConstraintStore(v, 1);
      assertSame(ConstraintResult.MATCHED, v.enforce(s));
   }

   @Test
   public void testEnforceMatchedAndUpdated() {
      Variable v = new Variable(99);
      ConstraintStore s = new DummyConstraintStore(v, Long.MIN_VALUE, Long.MAX_VALUE);
      assertSame(ConstraintResult.MATCHED, v.enforce(s));
      assertEquals(1, s.getMin(v));
      assertEquals(1, s.getMax(v));
   }

   @Test
   public void testEnforceFailed() {
      Variable v = new Variable(99);
      ConstraintStore s = new DummyConstraintStore(v, 0);
      assertSame(ConstraintResult.FAILED, v.enforce(s));
   }

   @Test
   public void testEnforceToHigh() {
      Variable v = new Variable(99);
      ConstraintStore s = new DummyConstraintStore(v, 2);
      try {
         v.enforce(s);
         fail();
      } catch (IllegalStateException e) {
         assertEquals("Expected 0 or 1", e.getMessage());
      }
   }

   @Test
   public void testEnforceToLow() {
      Variable v = new Variable(99);
      ConstraintStore s = new DummyConstraintStore(v, -1);
      try {
         v.enforce(s);
         fail();
      } catch (IllegalStateException e) {
         assertEquals("Expected 0 or 1", e.getMessage());
      }
   }

   @Test
   public void testPreventMatched() {
      Variable v = new Variable(99);
      ConstraintStore s = new DummyConstraintStore(v, 0);
      assertSame(ConstraintResult.MATCHED, v.prevent(s));
   }

   @Test
   public void testPreventMatchedAndUpdated() {
      Variable v = new Variable(99);
      ConstraintStore s = new DummyConstraintStore(v, Long.MIN_VALUE, Long.MAX_VALUE);
      assertSame(ConstraintResult.MATCHED, v.prevent(s));
      assertEquals(0, s.getMin(v));
      assertEquals(0, s.getMax(v));
   }

   @Test
   public void testPreventFailed() {
      Variable v = new Variable(99);
      ConstraintStore s = new DummyConstraintStore(v, 1);
      assertSame(ConstraintResult.FAILED, v.prevent(s));
   }

   @Test
   public void testPreventToHigh() {
      Variable v = new Variable(99);
      ConstraintStore s = new DummyConstraintStore(v, 2);
      try {
         v.prevent(s);
         fail();
      } catch (IllegalStateException e) {
         assertEquals("Expected 0 or 1", e.getMessage());
      }
   }

   @Test
   public void testPreventToLow() {
      Variable v = new Variable(99);
      ConstraintStore s = new DummyConstraintStore(v, -1);
      try {
         v.prevent(s);
         fail();
      } catch (IllegalStateException e) {
         assertEquals("Expected 0 or 1", e.getMessage());
      }
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
   public void testReplace_null() {
      // given
      @SuppressWarnings("unchecked")
      Function<LeafExpression, LeafExpression> function = mock(Function.class);
      Variable testObject = new Variable(1);
      when(function.apply(testObject)).thenReturn(null);

      // when
      LeafExpression replacement = testObject.replace(function);
      assertSame(testObject, replacement);

      // then
      verify(function).apply(testObject);
      verifyNoMoreInteractions(function);
   }

   @Test
   public void testReplace_other() {
      // given
      @SuppressWarnings("unchecked")
      Function<LeafExpression, LeafExpression> function = mock(Function.class);
      Variable testObject = new Variable(1);
      Variable expected = new Variable(30);
      when(function.apply(testObject)).thenReturn(expected);

      // when
      LeafExpression replacement = testObject.replace(function);
      assertSame(expected, replacement);

      // then
      verify(function).apply(testObject);
      verifyNoMoreInteractions(function);
   }
}

class DummyConstraintStore implements ConstraintStore {
   private final Variable variable;
   private final VariableState state;

   DummyConstraintStore(Variable variable, long value) {
      this(variable, value, value);
   }

   DummyConstraintStore(Variable variable, long min, long max) {
      if (max < min) {
         throw new IllegalArgumentException();
      }
      this.variable = variable;
      this.state = new VariableState();
      state.setMin(min);
      state.setMax(max);
   }

   @Override
   public long getMin(Expression id) {
      assertSame(variable, id);
      return state.getMin();
   }

   @Override
   public long getMax(Expression id) {
      assertSame(variable, id);
      return state.getMax();
   }

   @Override
   public ExpressionResult setValue(Expression id, long value) {
      assertSame(variable, id);
      return toResult(state.setValue(value));
   }

   @Override
   public ExpressionResult setMin(Expression id, long min) {
      assertSame(variable, id);
      return toResult(state.setMin(min));
   }

   @Override
   public ExpressionResult setMax(Expression id, long max) {
      assertSame(variable, id);
      return toResult(state.setMax(max));
   }

   @Override
   public ExpressionResult setNot(Expression id, long not) {
      assertSame(variable, id);
      return toResult(state.setNot(not));
   }

   private ExpressionResult toResult(VariableStateResult s) {
      return s == VariableStateResult.FAILED ? ExpressionResult.INVALID : ExpressionResult.VALID;
   }
}
