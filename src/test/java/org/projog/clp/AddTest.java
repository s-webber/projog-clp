/*
 * Copyright 2022 a. Webber
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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
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
import com.tngtech.java.junit.dataprovider.UseDataProvider;

@RunWith(DataProviderRunner.class)
public class AddTest {
   @Test
   @DataProvider({
               "0,0,0", // 0/0
               "42,0,42", // +/0
               "-42,0,-42", // -/0
               "42,5,47", // +/+
               "-5,-42,-47", // -/-
               "5,-42,-37", // +/-
               // combinations of MIN/MAX
               "MIN,MIN,MIN",
               "MAX,MAX,MAX",
               "MAX,MIN,-1",
               // MIN
               "MIN,3,MIN+3",
               "MIN,2,MIN+2",
               "MIN,1,MIN+1",
               "MIN,0,MIN",
               "MIN,-1,MIN",
               "MIN,-2,MIN",
               "MIN,-3,MIN",
               // MIN+1
               "MIN+1,3,MIN+4",
               "MIN+1,2,MIN+3",
               "MIN+1,1,MIN+2",
               "MIN+1,0,MIN+1",
               "MIN+1,-1,MIN",
               "MIN+1,-2,MIN",
               "MIN+1,-3,MIN",
               // MIN+2
               "MIN+2,3,MIN+5",
               "MIN+2,2,MIN+4",
               "MIN+2,1,MIN+3",
               "MIN+2,0,MIN+2",
               "MIN+2,-1,MIN+1",
               "MIN+2,-2,MIN",
               "MIN+2,-3,MIN",
               // MAX
               "MAX,3,MAX",
               "MAX,2,MAX",
               "MAX,1,MAX",
               "MAX,0,MAX",
               "MAX,-1,MAX-1",
               "MAX,-2,MAX-2",
               "MAX,-3,MAX-3",
               // MAX-1
               "MAX-1,3,MAX",
               "MAX-1,2,MAX",
               "MAX-1,1,MAX",
               "MAX-1,0,MAX-1",
               "MAX-1,-1,MAX-2",
               "MAX-1,-2,MAX-3",
               "MAX-1,-3,MAX-4",
               // MAX-2
               "MAX-2,3,MAX",
               "MAX-2,2,MAX",
               "MAX-2,1,MAX-1",
               "MAX-2,0,MAX-2",
               "MAX-2,-1,MAX-3",
               "MAX-2,-2,MAX-4",
               "MAX-2,-3,MAX-5",
               // -ve/+ve
               "1:10,1:10,2:20", // all positive
               "-10:-1,-10:-1,-20:-2", // all negative
               "-7:12,-6:13,-13:25", // neg:positive,neg:positive
   })
   public void testGetMinMax(String leftRange, String rightRange, String expectedRange) {
      assertGetMinMax(parseRange(leftRange), parseRange(rightRange), parseRange(expectedRange));
      assertGetMinMax(parseRange(rightRange), parseRange(leftRange), parseRange(expectedRange));
   }

   private void assertGetMinMax(Range leftRange, Range rightRange, Range expectedRange) {
      Bdd environment = new Bdd(leftRange, rightRange);
      Add a = new Add(environment.getLeft(), environment.getRight());

      assertEquals(expectedRange.min(), a.getMin(environment.getVariables()));
      assertEquals(expectedRange.max(), a.getMax(environment.getVariables()));
   }

   @Test
   @DataProvider({
               "9,0:3,8,9,0:3",
               "MIN:9,0:3,8,5:9,0:3",
               "9,0:3,9,9,0:3",
               "MIN:9,0:3,9,6:9,0:3",
               "9,0:3,10,9,1:3",
               "MIN:9,0:3,10,7:9,1:3",
               "9,0:3,11,9,2:3",
               "MIN:9,0:3,11,8:9,2:3",
               "9,0:3,12,9,3:3",
               "MIN:9,0:3,12,9:9,3:3",
               "9,1:3,9,9,1:3",
               "MIN:9,1:3,9,6:9,1:3",
               "9,1:3,10,9,1:3",
               "MIN:9,1:3,10,7:9,1:3",
               "9,1:3,11,9,2:3",
               "MIN:9,1:3,11,8:9,2:3",
               "9,1:3,12,9,3:3",
               "MIN:9,1:3,12,9:9,3:3",
               "9,-2:3,6,9,-2:3",
               "MIN:9,-2:3,6,3:9,-2:3",
               "9,-2:3,7,9,-2:3",
               "MIN:9,-2:3,7,4:9,-2:3",
               "9,-2:3,8,9,-1:3",
               "MIN:9,-2:3,8,5:9,-1:3",
               "9,-2:3,9,9,0:3",
               "MIN:9,-2:3,9,6:9,0:3",
               "9,-2:3,10,9,1:3",
               "MIN:9,-2:3,10,7:9,1:3",
               "9,-2:3,11,9,2:3",
               "MIN:9,-2:3,11,8:9,2:3",
               "9,-2:3,12,9,3:3",
               "MIN:9,-2:3,12,9:9,3:3",
               "-9,0:3,-10,-9,0:3",
               "MIN:-9,0:3,-10,-13:-9,0:3",
               "-9,0:3,-9,-9,0:3",
               "MIN:-9,0:3,-9,-12:-9,0:3",
               "-9,0:3,-8,-9,1:3",
               "MIN:-9,0:3,-8,-11:-9,1:3",
               "-9,0:3,-7,-9,2:3",
               "MIN:-9,0:3,-7,-10:-9,2:3",
               "-9,0:3,-6,-9,3:3",
               "MIN:-9,0:3,-6,-9:-9,3:3",
               "-9,1:3,-9,-9,1:3",
               "MIN:-9,1:3,-9,-12:-9,1:3",
               "-9,1:3,-8,-9,1:3",
               "MIN:-9,1:3,-8,-11:-9,1:3",
               "-9,1:3,-7,-9,2:3",
               "MIN:-9,1:3,-7,-10:-9,2:3",
               "-9,1:3,-6,-9,3:3",
               "MIN:-9,1:3,-6,-9:-9,3:3",
               "-9,-2:3,-12,-9,-2:3",
               "MIN:-9,-2:3,-12,-15:-9,-2:3",
               "-9,-2:3,-11,-9,-2:3",
               "MIN:-9,-2:3,-11,-14:-9,-2:3",
               "-9,-2:3,-10,-9,-1:3",
               "MIN:-9,-2:3,-10,-13:-9,-1:3",
               "-9,-2:3,-9,-9,0:3",
               "MIN:-9,-2:3,-9,-12:-9,0:3",
               "-9,-2:3,-8,-9,1:3",
               "MIN:-9,-2:3,-8,-11:-9,1:3",
               "-9,-2:3,-7,-9,2:3",
               "MIN:-9,-2:3,-7,-10:-9,2:3",
               "-9,-2:3,-6,-9,3:3",
               "MIN:-9,-2:3,-6,-9:-9,3:3",
               "0,0:3,-1,0,0:3",
               "MIN:0,0:3,-1,-4:0,0:3",
               "0,0:3,0,0,0:3",
               "MIN:0,0:3,0,-3:0,0:3",
               "0,0:3,1,0,1:3",
               "MIN:0,0:3,1,-2:0,1:3",
               "0,0:3,2,0,2:3",
               "MIN:0,0:3,2,-1:0,2:3",
               "0,0:3,3,0,3:3",
               "MIN:0,0:3,3,0:0,3:3",
               "0,1:3,0,0,1:3",
               "MIN:0,1:3,0,-3:0,1:3",
               "0,1:3,1,0,1:3",
               "MIN:0,1:3,1,-2:0,1:3",
               "0,1:3,2,0,2:3",
               "MIN:0,1:3,2,-1:0,2:3",
               "0,1:3,3,0,3:3",
               "MIN:0,1:3,3,0:0,3:3",
               "0,-2:3,-3,0,-2:3",
               "MIN:0,-2:3,-3,-6:0,-2:3",
               "0,-2:3,-2,0,-2:3",
               "MIN:0,-2:3,-2,-5:0,-2:3",
               "0,-2:3,-1,0,-1:3",
               "MIN:0,-2:3,-1,-4:0,-1:3",
               "0,-2:3,0,0,0:3",
               "MIN:0,-2:3,0,-3:0,0:3",
               "0,-2:3,1,0,1:3",
               "MIN:0,-2:3,1,-2:0,1:3",
               "0,-2:3,2,0,2:3",
               "MIN:0,-2:3,2,-1:0,2:3",
               "0,-2:3,3,0,3:3",
               "MIN:0,-2:3,3,0:0,3:3",})
   public void testSetMin(String inputLeftRange, String inputRightRange, String min, String outputLeftRange, String outputRightRange) {
      assertSetMin(parseRange(inputLeftRange), parseRange(inputRightRange), Long.parseLong(min), parseRange(outputLeftRange), parseRange(outputRightRange));
      assertSetMin(parseRange(inputRightRange), parseRange(inputLeftRange), Long.parseLong(min), parseRange(outputRightRange), parseRange(outputLeftRange));
   }

   private void assertSetMin(Range inputLeftRange, Range inputRightRange, long min, Range outputLeftRange, Range outputRightRange) {
      Bdd environment = new Bdd(inputLeftRange, inputRightRange);
      Variable left = environment.getLeft();
      Variable right = environment.getRight();
      Add a = new Add(left, right);

      assertNotEquals(ExpressionResult.FAILED, a.setMin(environment.getVariables(), min));
      assertEquals(outputLeftRange.min(), left.getMin(environment.getVariables()));
      assertEquals(outputLeftRange.max(), left.getMax(environment.getVariables()));
      assertEquals(outputRightRange.min(), right.getMin(environment.getVariables()));
      assertEquals(outputRightRange.max(), right.getMax(environment.getVariables()));
   }

   @Test
   @DataProvider({
               "9,0:3,9,9,0:0",
               "9:MAX,0:3,9,9:9,0:0",
               "9,0:3,10,9,0:1",
               "9:MAX,0:3,10,9:10,0:1",
               "9,0:3,11,9,0:2",
               "9:MAX,0:3,11,9:11,0:2",
               "9,0:3,12,9,0:3",
               "9:MAX,0:3,12,9:12,0:3",
               "9,0:3,13,9,0:3",
               "9:MAX,0:3,13,9:13,0:3",
               "9,1:3,10,9,1:1",
               "9:MAX,1:3,10,9:9,1:1",
               "9,1:3,11,9,1:2",
               "9:MAX,1:3,11,9:10,1:2",
               "9,1:3,12,9,1:3",
               "9:MAX,1:3,12,9:11,1:3",
               "9,1:3,13,9,1:3",
               "9:MAX,1:3,13,9:12,1:3",
               "9,-2:3,7,9,-2:-2",
               "9:MAX,-2:3,7,9:9,-2:-2",
               "9,-2:3,8,9,-2:-1",
               "9:MAX,-2:3,8,9:10,-2:-1",
               "9,-2:3,9,9,-2:0",
               "9:MAX,-2:3,9,9:11,-2:0",
               "9,-2:3,10,9,-2:1",
               "9:MAX,-2:3,10,9:12,-2:1",
               "9,-2:3,11,9,-2:2",
               "9:MAX,-2:3,11,9:13,-2:2",
               "9,-2:3,12,9,-2:3",
               "9:MAX,-2:3,12,9:14,-2:3",
               "9,-2:3,13,9,-2:3",
               "9:MAX,-2:3,13,9:15,-2:3",
               "-9,0:3,-9,-9,0:0",
               "-9:MAX,0:3,-9,-9:-9,0:0",
               "-9,0:3,-8,-9,0:1",
               "-9:MAX,0:3,-8,-9:-8,0:1",
               "-9,0:3,-7,-9,0:2",
               "-9:MAX,0:3,-7,-9:-7,0:2",
               "-9,0:3,-6,-9,0:3",
               "-9:MAX,0:3,-6,-9:-6,0:3",
               "-9,0:3,-5,-9,0:3",
               "-9:MAX,0:3,-5,-9:-5,0:3",
               "-9,1:3,-8,-9,1:1",
               "-9:MAX,1:3,-8,-9:-9,1:1",
               "-9,1:3,-7,-9,1:2",
               "-9:MAX,1:3,-7,-9:-8,1:2",
               "-9,1:3,-6,-9,1:3",
               "-9:MAX,1:3,-6,-9:-7,1:3",
               "-9,1:3,-5,-9,1:3",
               "-9:MAX,1:3,-5,-9:-6,1:3",
               "-9,-2:3,-11,-9,-2:-2",
               "-9:MAX,-2:3,-11,-9:-9,-2:-2",
               "-9,-2:3,-10,-9,-2:-1",
               "-9:MAX,-2:3,-10,-9:-8,-2:-1",
               "-9,-2:3,-9,-9,-2:0",
               "-9:MAX,-2:3,-9,-9:-7,-2:0",
               "-9,-2:3,-8,-9,-2:1",
               "-9:MAX,-2:3,-8,-9:-6,-2:1",
               "-9,-2:3,-7,-9,-2:2",
               "-9:MAX,-2:3,-7,-9:-5,-2:2",
               "-9,-2:3,-6,-9,-2:3",
               "-9:MAX,-2:3,-6,-9:-4,-2:3",
               "-9,-2:3,-5,-9,-2:3",
               "-9:MAX,-2:3,-5,-9:-3,-2:3",
               "0,0:3,0,0,0:0",
               "0:MAX,0:3,0,0:0,0:0",
               "0,0:3,1,0,0:1",
               "0:MAX,0:3,1,0:1,0:1",
               "0,0:3,2,0,0:2",
               "0:MAX,0:3,2,0:2,0:2",
               "0,0:3,3,0,0:3",
               "0:MAX,0:3,3,0:3,0:3",
               "0,0:3,4,0,0:3",
               "0:MAX,0:3,4,0:4,0:3",
               "0,1:3,1,0,1:1",
               "0:MAX,1:3,1,0:0,1:1",
               "0,1:3,2,0,1:2",
               "0:MAX,1:3,2,0:1,1:2",
               "0,1:3,3,0,1:3",
               "0:MAX,1:3,3,0:2,1:3",
               "0,1:3,4,0,1:3",
               "0:MAX,1:3,4,0:3,1:3",
               "0,-2:3,-2,0,-2:-2",
               "0:MAX,-2:3,-2,0:0,-2:-2",
               "0,-2:3,-1,0,-2:-1",
               "0:MAX,-2:3,-1,0:1,-2:-1",
               "0,-2:3,0,0,-2:0",
               "0:MAX,-2:3,0,0:2,-2:0",
               "0,-2:3,1,0,-2:1",
               "0:MAX,-2:3,1,0:3,-2:1",
               "0,-2:3,2,0,-2:2",
               "0:MAX,-2:3,2,0:4,-2:2",
               "0,-2:3,3,0,-2:3",
               "0:MAX,-2:3,3,0:5,-2:3",
               "0,-2:3,4,0,-2:3",
               "0:MAX,-2:3,4,0:6,-2:3",})
   public void testSetMax(String inputLeftRange, String inputRightRange, String max, String outputLeftRange, String outputRightRange) {
      assertSetMax(parseRange(inputLeftRange), parseRange(inputRightRange), Long.parseLong(max), parseRange(outputLeftRange), parseRange(outputRightRange));
      assertSetMax(parseRange(inputRightRange), parseRange(inputLeftRange), Long.parseLong(max), parseRange(outputRightRange), parseRange(outputLeftRange));
   }

   private void assertSetMax(Range inputLeftRange, Range inputRightRange, long max, Range outputLeftRange, Range outputRightRange) {
      Bdd environment = new Bdd(inputLeftRange, inputRightRange);
      Variable left = environment.getLeft();
      Variable right = environment.getRight();
      Add a = new Add(left, right);

      assertNotEquals(ExpressionResult.FAILED, a.setMax(environment.getVariables(), max));
      assertEquals(outputLeftRange.min(), left.getMin(environment.getVariables()));
      assertEquals(outputLeftRange.max(), left.getMax(environment.getVariables()));
      assertEquals(outputRightRange.min(), right.getMin(environment.getVariables()));
      assertEquals(outputRightRange.max(), right.getMax(environment.getVariables()));
   }

   @DataProvider
   public static Object[] data() {
      return new Object[][] {
                  {"0", "42"},
                  {"42", "0"},
                  {"0", "-42"},
                  {"-42", "0"},
                  {"5", "42"},
                  {"42", "5"},
                  {"5", "-42"},
                  {"-42", "5"},
                  {"1:10", "1:10"},
                  {"-10:-1", "-10:-1"},
                  {"-7:12", "-6:13"}};
   }

   @UseDataProvider("data")
   @Test
   public void testSetMinSuccess(String leftRange, String rightRange) {
      assertSetMinSuccess(parseRange(leftRange), parseRange(rightRange));
      assertSetMinSuccess(parseRange(rightRange), parseRange(leftRange));
   }

   private void assertSetMinSuccess(Range leftRange, Range rightRange) {
      Bdd environment = new Bdd(leftRange, rightRange);
      Variables variables = environment.getVariables();
      Add a = new Add(environment.getLeft(), environment.getRight());

      long min = a.getMin(variables);
      long max = a.getMax(variables);
      assertEquals(min == max ? ExpressionResult.NO_CHANGE : ExpressionResult.UPDATED, a.setMin(variables, max));
      assertEquals(max, a.getMin(variables));
   }

   @UseDataProvider("data")
   @Test
   public void testSetMaxSuccess(String leftRange, String rightRange) {
      assertSetMaxSuccess(parseRange(leftRange), parseRange(rightRange));
      assertSetMaxSuccess(parseRange(rightRange), parseRange(leftRange));
   }

   private void assertSetMaxSuccess(Range leftRange, Range rightRange) {
      Bdd environment = new Bdd(leftRange, rightRange);
      Variables variables = environment.getVariables();
      Add a = new Add(environment.getLeft(), environment.getRight());

      long min = a.getMin(variables);
      long max = a.getMax(variables);
      assertEquals(min == max ? ExpressionResult.NO_CHANGE : ExpressionResult.UPDATED, a.setMax(variables, min));
      assertEquals(min, a.getMax(variables));
   }

   @UseDataProvider("data")
   @Test
   public void testSetMinFailed(String leftRange, String rightRange) {
      assertSetMinFailed(parseRange(leftRange), parseRange(rightRange));
      assertSetMinFailed(parseRange(rightRange), parseRange(leftRange));
   }

   private void assertSetMinFailed(Range leftRange, Range rightRange) {
      for (int i = 1; i < 4; i++) {
         Bdd environment = new Bdd(leftRange, rightRange);
         Add a = new Add(environment.getLeft(), environment.getRight());

         long max = a.getMax(environment.getVariables());
         long newMax = max + i;
         assertEquals(ExpressionResult.FAILED, a.setMin(environment.getVariables(), newMax));
      }
   }

   @UseDataProvider("data")
   @Test
   public void testSetMaxFailed(String leftRange, String rightRange) {
      assertSetMaxFailed(parseRange(leftRange), parseRange(rightRange));
      assertSetMaxFailed(parseRange(rightRange), parseRange(leftRange));
   }

   private void assertSetMaxFailed(Range leftRange, Range rightRange) {
      for (int i = 1; i < 4; i++) {
         Bdd environment = new Bdd(leftRange, rightRange);
         Add a = new Add(environment.getLeft(), environment.getRight());

         long min = a.getMin(environment.getVariables());
         long newMin = min - i;
         assertEquals(ExpressionResult.FAILED, a.setMax(environment.getVariables(), newMin));
      }
   }

   @Test
   @DataProvider({"MAX,MAX", "MAX,1", "MAX-1,2", "MAX-1,3", "MAX-2,3", "MAX-3,4",})
   public void testSetMaxOverflow(String leftRange, String rightRange) {
      assertSetMaxOverflow(parseRange(leftRange), parseRange(rightRange));
      assertSetMaxOverflow(parseRange(rightRange), parseRange(leftRange));
   }

   private void assertSetMaxOverflow(Range leftRange, Range rightRange) {
      Bdd environment = new Bdd(leftRange, rightRange);
      Add a = new Add(environment.getLeft(), environment.getRight());

      assertEquals(ExpressionResult.FAILED, a.setMax(environment.getVariables(), a.getMin(environment.getVariables())));
   }

   @Test
   @DataProvider({"MIN,MIN", "MIN,-1", "MIN+1,-2", "MIN+1,-3", "MIN+2,-3", "MIN+3,-4",})
   public void testSetMinOverflow(String leftRange, String rightRange) {
      assertSetMinOverflow(parseRange(leftRange), parseRange(rightRange));
      assertSetMinOverflow(parseRange(rightRange), parseRange(leftRange));
   }

   private void assertSetMinOverflow(Range leftRange, Range rightRange) {
      Bdd environment = new Bdd(leftRange, rightRange);
      Add a = new Add(environment.getLeft(), environment.getRight());

      assertEquals(ExpressionResult.FAILED, a.setMin(environment.getVariables(), a.getMax(environment.getVariables())));
   }

   @Test
   public void testSetNot() {
      Range leftRange = parseRange("2:4");
      Range rightRange = parseRange("3:5");
      Bdd environment = new Bdd(leftRange, rightRange);
      Variable left = environment.getLeft();
      Variable right = environment.getRight();
      Variables variables = environment.getVariables();
      Add a = new Add(environment.getLeft(), environment.getRight());
      for (long i = a.getMin(variables) - 1; i <= a.getMax(variables) + 1; i++) {
         assertEquals(ExpressionResult.NO_CHANGE, a.setNot(variables, i));
         assertEquals(leftRange.min(), left.getMin(variables));
         assertEquals(leftRange.max(), left.getMax(variables));
         assertEquals(rightRange.min(), right.getMin(variables));
         assertEquals(rightRange.max(), right.getMax(variables));
      }
   }

   @Test
   public void testSetNotFailed() {
      Bdd environment = new Bdd(parseRange("3"), parseRange("5"));
      Variables variables = environment.getVariables();
      Add a = new Add(environment.getLeft(), environment.getRight());
      assertEquals(ExpressionResult.FAILED, a.setNot(variables, a.getMax(variables)));
   }

   @Test
   public void testWalk() {
      // given
      Consumer<Expression> consumer = mock(Consumer.class);
      Expression left = mock(Expression.class);
      Expression right = mock(Expression.class);
      Add testObject = new Add(left, right);

      // when
      testObject.walk(consumer);

      // then
      verify(consumer).accept(testObject);
      verify(left).walk(consumer);
      verify(right).walk(consumer);
      verifyNoMoreInteractions(consumer, left, right);
   }

   @Test
   public void testReplace() {
      // given
      Function<Expression, Expression> function = mock(Function.class);
      Expression left = mock(Expression.class);
      Expression right = mock(Expression.class);
      Add testObject = new Add(left, right);
      when(left.replace(function)).thenReturn(new FixedValue(42));
      when(right.replace(function)).thenReturn(new FixedValue(180));

      // when
      Add replacement = testObject.replace(function);
      assertNotSame(testObject, replacement);
      assertEquals("Add [left=FixedValue [value=42], right=FixedValue [value=180]]", replacement.toString());

      // then
      verify(left).replace(function);
      verify(right).replace(function);
      verifyNoMoreInteractions(function, left, right);
   }
}
