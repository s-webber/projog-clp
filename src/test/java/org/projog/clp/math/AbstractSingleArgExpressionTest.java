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
package org.projog.clp.math;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import org.projog.clp.ClpConstraintStore;
import org.projog.clp.ConstraintStore;
import org.projog.clp.Expression;
import org.projog.clp.ExpressionResult;
import org.projog.clp.FixedValue;
import org.projog.clp.Variable;
import org.projog.clp.test.Range;
import org.projog.clp.test.RangeParser;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

abstract class AbstractSingleArgExpressionTest {
   private final Map<String, GetterTest> getterTests = new LinkedHashMap<>();
   private final List<SetterTest> setterTests = new ArrayList<>();
   private final Function<Expression, Expression> factory;
   private boolean running;

   AbstractSingleArgExpressionTest(Function<Expression, Expression> factory) {
      this.factory = Objects.requireNonNull(factory);
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

      Set<SetAction> actions = new HashSet<>();
      for (Object[][] tests : new Object[][][] {setMaxTests(), setMinTests(), setNotTests()}) {
         assertFalse(tests.length == 0);
         Set<String> alreadyProcessed = new HashSet<>();
         Set<ExpressionResult> results = new HashSet<>();
         for (Object[] o : tests) {
            assertEquals(1, o.length);
            SetterTest t = (SetterTest) o[0];

            assertNotNull(t.action);
            assertNotNull(t.value);
            assertNotNull(t.result);
            actions.add(t.action);
            results.add(t.result);
            assertTrue(alreadyProcessed.add(t.input + " " + t.value), t.input + " " + t.action + " " + t.value);
         }
         assertEquals(results.size(), ExpressionResult.values().length);
      }
      assertEquals(actions.size(), SetAction.values().length);
   }

   @Test(dataProvider = "getTests")
   public final void testGetters(GetterTest t) {
      ClpConstraintStore.Builder b = new ClpConstraintStore.Builder();
      Variable variable = b.createVariable();
      ConstraintStore store = b.build();
      Expression e = factory.apply(variable);
      variable.setMin(store, t.input.min());
      variable.setMax(store, t.input.max());

      assertEquals(t.expected.min(), e.getMin(store));
      assertEquals(t.expected.max(), e.getMax(store));
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
      ClpConstraintStore.Builder b = new ClpConstraintStore.Builder();
      Variable variable = b.createVariable();
      ConstraintStore store = b.build();
      Expression e = factory.apply(variable);
      variable.setMin(store, t.input.min());
      variable.setMax(store, t.input.max());

      assertSame(t.result, t.action.set(e, store, t.value), t.input + " " + t.value + " " + t.output);
      if (t.result == ExpressionResult.VALID) {
         assertEquals(t.output.min(), variable.getMin(store), t.input + " " + t.value + " " + t.output);
         assertEquals(t.output.max(), variable.getMax(store), t.input + " " + t.value + " " + t.output);
      } else {
         assertSame(t.result, ExpressionResult.INVALID);
         assertNull(t.output);
      }
   }

   @Test
   public final void testWalk() {
      // given
      @SuppressWarnings("unchecked")
      Consumer<Expression> consumer = mock(Consumer.class);
      Expression input = mock(Expression.class);
      Expression testObject = factory.apply(input);

      // when
      testObject.walk(consumer);

      // then
      verify(consumer).accept(testObject);
      verify(input).walk(consumer);
      verifyNoMoreInteractions(consumer, input);
   }

   @Test
   public final void testReplaceVariables() {
      // given
      @SuppressWarnings("unchecked")
      Function<Variable, Variable> function = mock(Function.class);
      Expression input = mock(Expression.class);
      Expression testObject = factory.apply(input);
      org.mockito.Mockito.when(input.replaceVariables(function)).thenReturn(new FixedValue(42));

      // when
      Expression replacement = testObject.replaceVariables(function);
      assertSame(testObject.getClass(), replacement.getClass());
      assertNotSame(testObject, replacement);
      String name = testObject.getClass().getName();
      assertEquals(name.substring(name.lastIndexOf('.') + 1) + " [e=FixedValue [value=42]]", replacement.toString());

      // then
      verify(input).replaceVariables(function);
      verifyNoMoreInteractions(function, input);
   }

   @DataProvider
   public Object[][] getTests() {
      List<Object[]> result = new ArrayList<>();
      for (GetterTest s : getterTests.values()) {
         result.add(new Object[] {s});
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
      }

      return result.toArray(new Object[result.size()][]);
   }

   SetterTest given(String input) {
      assertFalse(running);

      SetterTest g = new SetterTest(input);
      setterTests.add(g);
      return g;
   }

   GetterTest expression(String input) {
      assertFalse(running);

      GetterTest w = new GetterTest(input);
      String key = input;
      if (getterTests.put(key, w) != null) {
         throw new IllegalStateException(key);
      }
      return w;
   }

   static class SetterTest {
      private final Range input;
      private SetAction action;
      private Long value;
      private ExpressionResult result;
      private Range output;

      private SetterTest(String input) {
         this(RangeParser.parseRange(input));
      }

      private SetterTest(Range input) {
         this.input = input;
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

      void then(String output) {
         then(RangeParser.parseRange(output));
      }

      void then(Range output) {
         assertNotNull(this.action);
         assertNull(this.result);
         this.result = ExpressionResult.VALID;
         this.output = output;
      }

      void unchanged() {
         then(input);
      }

      void failed() {
         assertNotNull(this.action);
         assertNull(this.result);
         this.result = ExpressionResult.INVALID;
      }
   }

   static class GetterTest {
      private final Range input;
      private Range expected;

      private GetterTest(String input) {
         this(RangeParser.parseRange(input));
      }

      private GetterTest(Range input) {
         this.input = input;
      }

      void returns(String expected) {
         assertNull(this.expected);
         this.expected = RangeParser.parseRange(expected);
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
