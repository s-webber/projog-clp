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
package org.projog.clp.compare;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.projog.clp.test.TestUtils.given;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import org.projog.clp.Constraint;
import org.projog.clp.ConstraintResult;
import org.projog.clp.Expression;
import org.projog.clp.FixedValue;
import org.projog.clp.Variable;
import org.projog.clp.test.Range;
import org.projog.clp.test.RangeParser;
import org.projog.clp.test.TestUtils;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

abstract class AbstractConstraintTest {
   private final Map<String, TestCase> enforceTests = new LinkedHashMap<>();
   private final Map<String, TestCase> preventTests = new LinkedHashMap<>();
   private final BiFunction<Expression, Expression, Constraint> factory;
   private final boolean flip;
   private final org.projog.clp.test.TestUtils.Action enforce;
   private final org.projog.clp.test.TestUtils.Action prevent;
   private final org.projog.clp.test.TestUtils.Action reify;
   private boolean running;

   AbstractConstraintTest(BiFunction<Expression, Expression, Constraint> factory, boolean flip) {
      this.factory = Objects.requireNonNull(factory);
      this.flip = flip;
      this.enforce = (v, x, y) -> factory.apply(x, y).enforce(v);
      this.prevent = (v, x, y) -> factory.apply(x, y).prevent(v);
      this.reify = (v, x, y) -> factory.apply(x, y).reify(v);
   }

   @BeforeTest
   public void before() {
      this.running = true;
   }

   @Test
   public final void testConfiguration() {
      assertFalse(enforceTests.isEmpty());
      for (Object[] o : enforceTest()) {
         assertEquals(1, o.length);
         TestCase t = (TestCase) o[0];

         assertNotNull(t.result);
      }

      assertFalse(preventTests.isEmpty());
      for (Object[] o : preventTest()) {
         assertEquals(1, o.length);
         TestCase t = (TestCase) o[0];

         assertNotNull(t.result);
      }

      assertEquals(enforceTest().length, reifyTest().length);
      for (Object[] o : reifyTest()) {
         assertEquals(1, o.length);
         TestCase t = (TestCase) o[0];

         assertNotNull(t.result);
      }
   }

   @Test(dataProvider = "enforceTest")
   public final void testEnforce(TestCase t) {
      test(enforce, t);
   }

   @Test(dataProvider = "preventTest")
   public final void testPrevent(TestCase t) {
      test(prevent, t);
   }

   @Test(dataProvider = "reifyTest")
   public final void testReify(TestCase t) {
      test(reify, t);
   }

   private void test(TestUtils.Action action, TestCase t) {
      if (t.result == ConstraintResult.FAILED) {
         given(t.inputLeft, t.inputRight).when(action).then(t.result);
      } else {
         given(t.inputLeft, t.inputRight).when(action).then(t.result, t.outputLeft, t.outputRight);
      }
   }

   @Test
   public final void testWalk() {
      // given
      @SuppressWarnings("unchecked")
      Consumer<Expression> consumer = mock(Consumer.class);
      Expression left = mock(Expression.class);
      Expression right = mock(Expression.class);
      Constraint testObject = factory.apply(left, right);

      // when
      testObject.walk(consumer);

      // then
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
      Constraint testObject = factory.apply(left, right);
      when(left.replaceVariables(function)).thenReturn(new FixedValue(42));
      when(right.replaceVariables(function)).thenReturn(new FixedValue(180));

      // when
      Constraint replacement = testObject.replaceVariables(function);
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
   public Object[][] enforceTest() {
      return toArray(enforceTests);
   }

   @DataProvider
   public Object[][] preventTest() {
      return toArray(preventTests);
   }

   @DataProvider
   public Object[][] reifyTest() {
      List<Object[]> result = new ArrayList<>();
      for (TestCase s : enforceTests.values()) {
         ConstraintResult expected;
         if (s.result == ConstraintResult.FAILED) {
            expected = ConstraintResult.FAILED;
         } else if (s.result == ConstraintResult.MATCHED && s.inputLeft.equals(s.outputLeft) && s.inputRight.equals(s.outputRight)) {
            expected = ConstraintResult.MATCHED;
         } else {
            expected = ConstraintResult.UNRESOLVED;
         }
         TestCase testCase = new TestCase(s.inputLeft, s.inputRight);
         testCase.set(expected, s.inputLeft, s.inputRight);
         result.add(new Object[] {testCase});
         if (shouldFlip(s)) {
            result.add(new Object[] {testCase.flip()});
         }
      }
      return result.toArray(new Object[result.size()][]);
   }

   private Object[][] toArray(Map<String, TestCase> testCases) {
      List<Object[]> result = new ArrayList<>();
      for (TestCase s : testCases.values()) {
         result.add(new Object[] {s});
         if (shouldFlip(s)) {
            result.add(new Object[] {s.flip()});
         }
      }
      return result.toArray(new Object[result.size()][]);
   }

   private boolean shouldFlip(TestCase s) {
      return flip && !s.inputLeft.equals(s.inputRight);
   }

   TestCase enforce(String left, String right) {
      return add(enforceTests, left, right);
   }

   TestCase prevent(String left, String right) {
      return add(preventTests, left, right);
   }

   private TestCase add(Map<String, TestCase> testCases, String left, String right) {
      assertFalse(running);

      TestCase testCase = new TestCase(left, right);
      String key = left + "," + right;
      if (testCases.put(key, testCase) != null) {
         throw new IllegalStateException(key);
      }
      if (flip && !left.equals(right) && testCases.containsKey(right + "," + left)) {
         throw new IllegalStateException(key);
      }
      return testCase;
   }

   static class TestCase {
      private final Range inputLeft;
      private final Range inputRight;
      private ConstraintResult result;
      private Range outputLeft;
      private Range outputRight;

      public TestCase(String left, String right) {
         this(RangeParser.parseRange(left), RangeParser.parseRange(right));
      }

      private TestCase(Range left, Range right) {
         this.inputLeft = left;
         this.inputRight = right;
      }

      void matched() {
         set(ConstraintResult.MATCHED, inputLeft, inputRight);
      }

      void matched(String output) {
         set(ConstraintResult.MATCHED, output, output);
      }

      void matched(String outputLeft, String outputRight) {
         set(ConstraintResult.MATCHED, outputLeft, outputRight);
      }

      void unresolved() {
         set(ConstraintResult.UNRESOLVED, inputLeft, inputRight);
      }

      void unresolved(String output) {
         set(ConstraintResult.UNRESOLVED, output, output);
      }

      void unresolved(String outputLeft, String outputRight) {
         set(ConstraintResult.UNRESOLVED, outputLeft, outputRight);
      }

      void failed() {
         assertNull(this.result);
         this.result = ConstraintResult.FAILED;
      }

      private void set(ConstraintResult result, String outputLeft, String outputRight) {
         set(result, RangeParser.parseRange(outputLeft), RangeParser.parseRange(outputRight));
      }

      private void set(ConstraintResult result, Range outputLeft, Range outputRight) {
         assertNull(this.result);
         this.result = Objects.requireNonNull(result);
         this.outputLeft = outputLeft;
         this.outputRight = outputRight;
      }

      private TestCase flip() {
         TestCase flip = new TestCase(inputRight, inputLeft);
         flip.set(result, outputRight, outputLeft);
         return flip;
      }
   }
}
