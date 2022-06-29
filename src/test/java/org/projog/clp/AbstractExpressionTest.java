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
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

abstract class AbstractExpressionTest {
   private final Map<String, GetterTest> getterTests = new LinkedHashMap<>();
   private final List<SetterTest> setterTests = new ArrayList<>();
   private final BiFunction<Expression, Expression, Expression> factory;
   private final boolean flip;
   private boolean running;

   AbstractExpressionTest(BiFunction<Expression, Expression, Expression> factory, boolean flip) {
      this.factory = Objects.requireNonNull(factory);
      this.flip = flip;
   }

   @BeforeTest
   public void before() {
      this.running = true;
   }

   @Test
   public final void testConfiguration() {
      assertFalse(getterTests.isEmpty());
      for (GetterTest t : getterTests.values()) {
         assertNotNull(t.expected);
      }

      // TODO check no duplicates in setterTests
      Set<SetAction> actions = new HashSet<>();
      for (Object[][] tests : new Object[][][] {setMaxTests(), setMinTests(), setNotTests()}) {
         assertFalse(tests.length == 0);
         Set<ExpressionResult> results = new HashSet<>();
         for (Object[] o : tests) {
            assertEquals(1, o.length);
            SetterTest t = (SetterTest) o[0];

            assertNotNull(t.action);
            assertNotNull(t.value);
            assertNotNull(t.result);
            actions.add(t.action);
            results.add(t.result);
         }
         assertEquals(results.size(), ExpressionResult.values().length);
      }
      assertEquals(actions.size(), SetAction.values().length);
   }

   @Test(dataProvider = "getTests")
   public final void testGetters(GetterTest t) {
      TestUtils environment = new TestUtils(t.inputLeft, t.inputRight);
      ConstraintStore store = environment.getConstraintStore();
      Expression a = factory.apply(environment.getLeft(), environment.getRight());

      assertEquals(t.expected.min(), a.getMin(store));
      assertEquals(t.expected.max(), a.getMax(store));
   }

   @Test(dataProvider = "setMinTests")
   public final void testSetMin(SetterTest t) {
      testSetter(t);
   }

   @Test(dataProvider = "setMaxTests")
   public final void testSetMax(SetterTest t) {
      testSetter(t);
   }

   @Test(dataProvider = "setNotTests")
   public final void testSetNot(SetterTest t) {
      testSetter(t);
   }

   private void testSetter(SetterTest t) {
      TestUtils environment = new TestUtils(t.inputLeft, t.inputRight);
      ConstraintStore store = environment.getConstraintStore();
      Variable left = environment.getLeft();
      Variable right = environment.getRight();
      Expression e = factory.apply(left, right);

      assertSame(t.result, t.action.set(e, store, t.value));
      if (t.result == ExpressionResult.VALID) {
         assertEquals(t.outputLeft.min(), left.getMin(store));
         assertEquals(t.outputLeft.max(), left.getMax(store));
         assertEquals(t.outputRight.min(), right.getMin(store));
         assertEquals(t.outputRight.max(), right.getMax(store));
      } else {
         assertNull(t.outputLeft);
         assertNull(t.outputRight);
      }
   }

   @Test
   public final void testWalk() {
      // given
      @SuppressWarnings("unchecked")
      Consumer<Expression> consumer = mock(Consumer.class);
      Expression left = mock(Expression.class);
      Expression right = mock(Expression.class);
      Expression testObject = factory.apply(left, right);

      // when
      testObject.walk(consumer);

      // then
      verify(consumer).accept(testObject);
      verify(left).walk(consumer);
      verify(right).walk(consumer);
      verifyNoMoreInteractions(consumer, left, right);
   }

   @Test
   public final void testReplaceVariables() {
      // given
      @SuppressWarnings("unchecked")
      Function<Variable, Variable> function = mock(Function.class);
      Expression left = mock(Expression.class);
      Expression right = mock(Expression.class);
      Expression testObject = factory.apply(left, right);
      org.mockito.Mockito.when(left.replaceVariables(function)).thenReturn(new FixedValue(42));
      org.mockito.Mockito.when(right.replaceVariables(function)).thenReturn(new FixedValue(180));

      // when
      Expression replacement = testObject.replaceVariables(function);
      assertSame(testObject.getClass(), replacement.getClass());
      assertNotSame(testObject, replacement);
      String name = testObject.getClass().getName();
      assertEquals(name.substring(name.lastIndexOf('.') + 1) + " [left=FixedValue [value=42], right=FixedValue [value=180]]", replacement.toString());

      // then
      verify(left).replaceVariables(function);
      verify(right).replaceVariables(function);
      verifyNoMoreInteractions(function, left, right);
   }

   @DataProvider
   public Object[][] getTests() {
      List<Object[]> result = new ArrayList<>();
      for (GetterTest s : getterTests.values()) {
         result.add(new Object[] {s});
         if (flip && !s.inputLeft.equals(s.inputRight)) {
            result.add(new Object[] {s.flip()});
         }
      }
      return result.toArray(new Object[result.size()][]);
   }

   @DataProvider
   public Object[][] setMinTests() {
      return selectSetterTests(SetAction.MIN);
   }

   @DataProvider
   public Object[][] setMaxTests() {
      return selectSetterTests(SetAction.MAX);
   }

   @DataProvider
   public Object[][] setNotTests() {
      return selectSetterTests(SetAction.NOT);
   }

   private Object[][] selectSetterTests(SetAction action) {
      List<Object[]> result = new ArrayList<>();

      for (SetterTest s : setterTests) {
         if (action != s.action) {
            continue;
         }

         result.add(new Object[] {s});
         if (flip && !s.inputLeft.equals(s.inputRight)) {
            result.add(new Object[] {s.flip()});
         }
      }

      return result.toArray(new Object[result.size()][]);
   }

   Range getMinMax(String inputLeft, String inputRight) {
      TestUtils environment = new TestUtils(TestDataParser.parseRange(inputLeft), TestDataParser.parseRange(inputRight));
      ConstraintStore store = environment.getConstraintStore();
      Variable left = environment.getLeft();
      Variable right = environment.getRight();
      Expression e = factory.apply(left, right);

      return new Range(e.getMin(store), e.getMax(store));
   }

   SetterTest given(String left, String right) {
      assertFalse(running);

      SetterTest g = new SetterTest(left, right);
      setterTests.add(g);
      return g;
   }

   GetterTest when(String left, String right) {
      assertFalse(running);

      GetterTest w = new GetterTest(left, right);
      String key = left + "," + right;
      if (getterTests.put(key, w) != null) {
         throw new IllegalStateException(key);
      }
      if (flip && !left.equals(right) && getterTests.containsKey(right + "," + left)) {
         throw new IllegalStateException(key);
      }
      return w;
   }

   static class SetterTest {
      private final Range inputLeft;
      private final Range inputRight;
      private SetAction action;
      private Long value;
      private ExpressionResult result;
      private Range outputLeft;
      private Range outputRight;

      private SetterTest(String inputLeft, String inputRight) {
         this(TestDataParser.parseRange(inputLeft), TestDataParser.parseRange(inputRight));
      }

      private SetterTest(Range inputLeft, Range inputRight) {
         this.inputLeft = inputLeft;
         this.inputRight = inputRight;
      }

      SetterTest setMin(long value) {
         assertNull(this.action);
         this.action = SetAction.MIN;
         this.value = value;
         return this;
      }

      SetterTest setMax(long value) {
         assertNull(this.action);
         this.action = SetAction.MAX;
         this.value = value;
         return this;
      }

      SetterTest setNot(long value) {
         assertNull(this.action);
         this.action = SetAction.NOT;
         this.value = value;
         return this;
      }

      void then(String outputLeft, String outputRight) {
         // TODO if outputLeft==outputRight then throw exception to force use of unchanged()
         then(TestDataParser.parseRange(outputLeft), TestDataParser.parseRange(outputRight));
      }

      void then(Range outputLeft, Range outputRight) {
         assertNull(this.result);
         this.result = ExpressionResult.VALID;
         this.outputLeft = outputLeft;
         this.outputRight = outputRight;
      }

      void unchanged() {
         then(inputLeft, inputRight);
      }

      void failed() {
         assertNull(this.result);
         this.result = ExpressionResult.INVALID;
      }

      private SetterTest flip() {
         SetterTest flip = new SetterTest(inputRight, inputLeft);
         flip.action = this.action;
         flip.value = this.value;
         flip.result = this.result;
         flip.outputLeft = this.outputRight;
         flip.outputRight = this.outputLeft;
         return flip;
      }
   }

   static class GetterTest {
      private final Range inputLeft;
      private final Range inputRight;
      private Range expected;

      private GetterTest(String inputLeft, String inputRight) {
         this(TestDataParser.parseRange(inputLeft), TestDataParser.parseRange(inputRight));
      }

      private GetterTest(Range inputLeft, Range inputRight) {
         this.inputLeft = inputLeft;
         this.inputRight = inputRight;
      }

      void then(String expected) {
         this.expected = TestDataParser.parseRange(expected);
      }

      private GetterTest flip() {
         GetterTest flip = new GetterTest(inputRight, inputLeft);
         flip.expected = this.expected;
         return flip;
      }
   }

   private static enum SetAction {
      MIN,
      MAX,
      NOT;

      ExpressionResult set(Expression e, ConstraintStore store, long value) {
         switch (this) {
            case MIN:
               return e.setMin(store, value);
            case MAX:
               return e.setMax(store, value);
            case NOT:
               return e.setNot(store, value);
            default:
               throw new IllegalArgumentException();
         }
      }
   }
}
