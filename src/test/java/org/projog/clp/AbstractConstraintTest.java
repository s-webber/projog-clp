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

import org.projog.clp.test.Range;
import org.projog.clp.test.RangeParser;
import org.projog.clp.test.TestData;
import org.projog.clp.test.TestDataProvider;
import org.projog.clp.test.TestUtils;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public abstract class AbstractConstraintTest {
   private static final Range FULL_RANGE = new Range(Long.MIN_VALUE, Long.MAX_VALUE);

   private final Map<String, TestCase> enforceTests = new LinkedHashMap<>();
   private final Map<String, TestCase> preventTests = new LinkedHashMap<>();
   private final Map<String, TestCase> reifyTests = new LinkedHashMap<>();
   private final BiFunction<LeafExpression, LeafExpression, Constraint> factory;
   private final boolean flip;
   private final TestUtils.Action enforce;
   private final TestUtils.Action prevent;
   private final TestUtils.Action reify;
   private final TestUtils.Action setTrue;
   private final TestUtils.Action setFalse;
   private final TestUtils.Action setNotTrue;
   private final TestUtils.Action setNotFalse;
   private boolean running;

   protected AbstractConstraintTest(BiFunction<LeafExpression, LeafExpression, Constraint> factory, boolean flip) {
      this.factory = Objects.requireNonNull(factory);
      this.flip = flip;
      this.enforce = (v, x, y) -> factory.apply(x, y).enforce(v);
      this.prevent = (v, x, y) -> factory.apply(x, y).prevent(v);
      this.reify = (v, x, y) -> factory.apply(x, y).reify(v);
      this.setTrue = (v, x, y) -> factory.apply(x, y).setMin(v, 1);
      this.setFalse = (v, x, y) -> factory.apply(x, y).setMax(v, 0);
      this.setNotTrue = (v, x, y) -> factory.apply(x, y).setNot(v, 1);
      this.setNotFalse = (v, x, y) -> factory.apply(x, y).setNot(v, 0);
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

   @Test(dataProvider = "enforceTest")
   public final void testSetTrue(TestCase t) {
      test(setTrue, t, t.result == ConstraintResult.FAILED ? ExpressionResult.INVALID : ExpressionResult.VALID);
   }

   @Test(dataProvider = "preventTest")
   public final void testSetFalse(TestCase t) {
      test(setFalse, t, t.result == ConstraintResult.FAILED ? ExpressionResult.INVALID : ExpressionResult.VALID);
   }

   @Test(dataProvider = "enforceTest")
   public final void testSetNotFalse(TestCase t) {
      test(setNotFalse, t, t.result == ConstraintResult.FAILED ? ExpressionResult.INVALID : ExpressionResult.VALID);
   }

   @Test(dataProvider = "preventTest")
   public final void testSetNotTrue(TestCase t) {
      test(setNotTrue, t, t.result == ConstraintResult.FAILED ? ExpressionResult.INVALID : ExpressionResult.VALID);
   }

   private void test(TestUtils.Action action, TestCase t) {
      test(action, t, t.result);
   }

   private void test(TestUtils.Action action, TestCase t, Object expected) {
      if (t.result == ConstraintResult.FAILED) {
         given(t.inputLeft, t.inputRight).when(action).then(expected);
      } else {
         given(t.inputLeft, t.inputRight).when(action).then(expected, t.outputLeft, t.outputRight);
      }
   }

   @Test(dataProvider = "reifyTest")
   public final void testGetMin(TestCase t) {
      Long expectedResult = t.result == ConstraintResult.MATCHED ? 1L : 0L;

      TestUtils.given(t.inputLeft, t.inputRight).when((s, x, y) -> {
         Constraint c = factory.apply(x, y);
         return c.getMin(s);
      }).then(expectedResult, t.inputLeft, t.inputRight);
   }

   @Test(dataProvider = "reifyTest")
   public final void testGetMax(TestCase t) {
      Long expectedResult = t.result == ConstraintResult.FAILED ? 0L : 1L;

      TestUtils.given(t.inputLeft, t.inputRight).when((s, x, y) -> {
         Constraint c = factory.apply(x, y);
         return c.getMax(s);
      }).then(expectedResult, t.inputLeft, t.inputRight);
   }

   @Test(dataProvider = "process", dataProviderClass = TestDataProvider.class)
   @TestData({"2", "3", "" + Long.MAX_VALUE, "-1", "-2", "" + Long.MIN_VALUE})
   public final void testSetNotOutsideRange(Long valueOutsideRange) {
      TestUtils.given(FULL_RANGE, FULL_RANGE).when((s, x, y) -> {
         Constraint c = factory.apply(x, y);
         return c.setNot(s, valueOutsideRange);
      }).then(ExpressionResult.VALID, FULL_RANGE, FULL_RANGE);
   }

   @Test(dataProvider = "process", dataProviderClass = TestDataProvider.class)
   @TestData({"-1", "-2", "" + Long.MIN_VALUE})
   public final void testSetMinBelowZero(Long valueBelowZero) {
      TestUtils.given(FULL_RANGE, FULL_RANGE).when((s, x, y) -> {
         Constraint c = factory.apply(x, y);
         return c.setMin(s, valueBelowZero);
      }).then(ExpressionResult.VALID, FULL_RANGE, FULL_RANGE);
   }

   @Test(dataProvider = "process", dataProviderClass = TestDataProvider.class)
   @TestData({"2", "3", "" + Long.MAX_VALUE})
   public final void testSetMaxAboveOne(Long valueAboveOne) {
      TestUtils.given(FULL_RANGE, FULL_RANGE).when((s, x, y) -> {
         Constraint c = factory.apply(x, y);
         return c.setMax(s, valueAboveOne);
      }).then(ExpressionResult.VALID, FULL_RANGE, FULL_RANGE);
   }

   @Test(dataProvider = "process", dataProviderClass = TestDataProvider.class)
   @TestData({"2", "3", "" + Long.MAX_VALUE})
   public final void testSetMinTooHigh(Long valueAboveOne) {
      TestUtils.given(FULL_RANGE, FULL_RANGE).when((s, x, y) -> {
         Constraint c = factory.apply(x, y);
         return c.setMin(s, valueAboveOne);
      }).then(ExpressionResult.INVALID, FULL_RANGE, FULL_RANGE);
   }

   @Test(dataProvider = "process", dataProviderClass = TestDataProvider.class)
   @TestData({"-1", "-2", "" + Long.MIN_VALUE})
   public final void testSetMaxTooLow(Long valueBelowZero) {
      TestUtils.given(FULL_RANGE, FULL_RANGE).when((s, x, y) -> {
         Constraint c = factory.apply(x, y);
         return c.setMax(s, valueBelowZero);
      }).then(ExpressionResult.INVALID, FULL_RANGE, FULL_RANGE);
   }

   @Test
   public void testWalk() {
      // given
      @SuppressWarnings("unchecked")
      Consumer<Expression> consumer = mock(Consumer.class);
      LeafExpression left = mock(LeafExpression.class);
      LeafExpression right = mock(LeafExpression.class);
      Constraint testObject = factory.apply(left, right);

      // when
      testObject.walk(consumer);

      // then
      verify(left).walk(consumer);
      verify(right).walk(consumer);
      verifyNoMoreInteractions(consumer, left, right);
   }

   @Test
   public void testReplace() {
      // given
      @SuppressWarnings("unchecked")
      Function<LeafExpression, LeafExpression> function = mock(Function.class);
      LeafExpression left = mock(LeafExpression.class);
      LeafExpression right = mock(LeafExpression.class);
      Constraint testObject = factory.apply(left, right);
      when(left.replace(function)).thenReturn(new FixedValue(42));
      when(right.replace(function)).thenReturn(new FixedValue(180));

      // when
      Constraint replacement = testObject.replace(function);
      assertSame(testObject.getClass(), replacement.getClass());
      assertNotSame(testObject, replacement);
      String name = testObject.getClass().getName();
      assertEquals(name.substring(name.lastIndexOf('.') + 1) + " [left=FixedValue [value=42], right=FixedValue [value=180]]", replacement.toString());

      // then
      verify(left).replace(function);
      verify(right).replace(function);
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
      if (!reifyTests.isEmpty()) {
         return toArray(reifyTests);
      }

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

   protected TestCase enforce(String left, String right) {
      return add(enforceTests, left, right);
   }

   protected TestCase prevent(String left, String right) {
      return add(preventTests, left, right);
   }

   protected TestCase reify(String left, String right) {
      return add(reifyTests, left, right);
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

   protected static class TestCase {
      private final Range inputLeft;
      private final Range inputRight;
      private ConstraintResult result;
      private Range outputLeft;
      private Range outputRight;

      private TestCase(String left, String right) {
         this(RangeParser.parseRange(left), RangeParser.parseRange(right));
      }

      private TestCase(Range left, Range right) {
         this.inputLeft = left;
         this.inputRight = right;
      }

      public void matched() {
         set(ConstraintResult.MATCHED, inputLeft, inputRight);
      }

      public void matched(String output) {
         set(ConstraintResult.MATCHED, output, output);
      }

      public void matched(String outputLeft, String outputRight) {
         set(ConstraintResult.MATCHED, outputLeft, outputRight);
      }

      public void unresolved() {
         set(ConstraintResult.UNRESOLVED, inputLeft, inputRight);
      }

      public void unresolved(String output) {
         set(ConstraintResult.UNRESOLVED, output, output);
      }

      public void unresolved(String outputLeft, String outputRight) {
         set(ConstraintResult.UNRESOLVED, outputLeft, outputRight);
      }

      public void failed() {
         assertNull(this.result);
         this.result = ConstraintResult.FAILED;
      }

      public void then(ConstraintResult result, String outputLeft, String outputRight) {
         if (result == ConstraintResult.FAILED) {
            failed();
         } else {
            set(result, outputLeft, outputRight);
         }
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

      @Override
      public String toString() {
         return "TestCase [inputLeft=" + inputLeft + ", inputRight=" + inputRight + ", result=" + result + ", outputLeft=" + outputLeft + ", outputRight=" + outputRight + "]";
      }
   }
}
