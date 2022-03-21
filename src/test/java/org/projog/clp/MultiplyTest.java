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

@RunWith(DataProviderRunner.class)
public class MultiplyTest {
   @Test
   @DataProvider({
               "0,0,0",
               "42,0,0",
               "-42,0,0",
               "42,5,210",
               "-5,-42,210",
               "5,-42,-210",
               "0:5,0:5,0:25",
               "-5:0,0:5,-25:0",
               "-5:0,-5:0,0:25",
               "0:5,1:5,0:25",
               "1:5,1:5,1:25",
               "-5:-1,1:5,-25:-1",
               "-5:-1,-5:-1,1:25",
               "-5:1,1:5,-25:5",
               "-5:1,-5:1,-5:25",
               "MAX,1,MAX",
               "MAX,2,MAX",
               "MAX,MAX,MAX",
               "MAX,-1,MIN+1",
               "MAX,-2,MIN",
               "MIN,1,MIN",
               "MIN,-1,MAX"})
   public void testGetMinMax(String leftRange, String rightRange, String expectedRange) {
      assertGetMinMax(parseRange(leftRange), parseRange(rightRange), parseRange(expectedRange));
      assertGetMinMax(parseRange(rightRange), parseRange(leftRange), parseRange(expectedRange));
   }

   private void assertGetMinMax(Range leftRange, Range rightRange, Range expectedRange) {
      Bdd environment = new Bdd(leftRange, rightRange);
      Multiply m = new Multiply(environment.getLeft(), environment.getRight());

      assertEquals(expectedRange.min(), m.getMin(environment.getVariables()));
      assertEquals(expectedRange.max(), m.getMax(environment.getVariables()));
   }

   @Test
   @DataProvider({
               "1,2,2,1,2",
               "7,3,20,7,3",
               "7,3,21,7,3",
               "1:7,2:3,14,5:7,2:3",
               "1:7,2:3,15,5:7,3",
               "1:7,2:3,16,6:7,3",
               "1:7,2:3,17,6:7,3",
               "1:7,2:3,18,6:7,3",
               "1:7,2:3,19,7,3",
               "1:7,2:3,20,7,3",
               "1:7,2:3,21,7,3"})
   public void testSetMin(String inputLeftRange, String inputRightRange, String min, String outputLeftRange, String outputRightRange) {
      assertSetMin(parseRange(inputLeftRange), parseRange(inputRightRange), Long.parseLong(min), parseRange(outputLeftRange), parseRange(outputRightRange));
      assertSetMin(parseRange(inputRightRange), parseRange(inputLeftRange), Long.parseLong(min), parseRange(outputRightRange), parseRange(outputLeftRange));
   }

   private void assertSetMin(Range inputLeftRange, Range inputRightRange, long min, Range outputLeftRange, Range outputRightRange) {
      Bdd environment = new Bdd(inputLeftRange, inputRightRange);
      Variable left = environment.getLeft();
      Variable right = environment.getRight();
      Multiply m = new Multiply(left, right);

      assertNotEquals(ExpressionResult.FAILED, m.setMin(environment.getVariables(), min));
      assertEquals(outputLeftRange.min(), left.getMin(environment.getVariables()));
      assertEquals(outputLeftRange.max(), left.getMax(environment.getVariables()));
      assertEquals(outputRightRange.min(), right.getMin(environment.getVariables()));
      assertEquals(outputRightRange.max(), right.getMax(environment.getVariables()));
   }

   @Test
   @DataProvider({"7,3,22", "7,3,23", "1:7,2:3,22"})
   public void testSetMinFailed(String inputLeftRange, String inputRightRange, String min) {
      assertSetMinFailed(parseRange(inputLeftRange), parseRange(inputRightRange), Long.parseLong(min));
      assertSetMinFailed(parseRange(inputRightRange), parseRange(inputLeftRange), Long.parseLong(min));
   }

   private void assertSetMinFailed(Range inputLeftRange, Range inputRightRange, long min) {
      Bdd environment = new Bdd(inputLeftRange, inputRightRange);
      Variable left = environment.getLeft();
      Variable right = environment.getRight();
      Multiply m = new Multiply(left, right);

      assertEquals(ExpressionResult.FAILED, m.setMin(environment.getVariables(), min));
   }

   @Test
   @DataProvider({
               "1,2,2,1,2",
               "4,6,24,4,6",
               "4,6,25,4,6",
               "7,15,105,7,15",
               "4:7,6:15,24,4,6",
               "4:7,6:15,25,4,6",
               "4:7,6:15,26,4,6",
               "4:7,6:15,27,4,6",
               "4:7,6:15,28,4,6:7",
               "4:7,6:15,29,4,6:7",
               "4:7,6:15,30,4:5,6:7",
               "4:7,6:15,31,4:5,6:7",
               "4:7,6:15,32,4:5,6:8",
               "4:7,6:15,105,4:7,6:15",})
   public void testSetMax(String inputLeftRange, String inputRightRange, String min, String outputLeftRange, String outputRightRange) {
      assertSetMax(parseRange(inputLeftRange), parseRange(inputRightRange), Long.parseLong(min), parseRange(outputLeftRange), parseRange(outputRightRange));
      assertSetMax(parseRange(inputRightRange), parseRange(inputLeftRange), Long.parseLong(min), parseRange(outputRightRange), parseRange(outputLeftRange));
   }

   private void assertSetMax(Range inputLeftRange, Range inputRightRange, long min, Range outputLeftRange, Range outputRightRange) {
      Bdd environment = new Bdd(inputLeftRange, inputRightRange);
      Variable left = environment.getLeft();
      Variable right = environment.getRight();
      Multiply m = new Multiply(left, right);

      assertNotEquals(ExpressionResult.FAILED, m.setMax(environment.getVariables(), min));
      assertEquals(outputLeftRange.min(), left.getMin(environment.getVariables()));
      assertEquals(outputLeftRange.max(), left.getMax(environment.getVariables()));
      assertEquals(outputRightRange.min(), right.getMin(environment.getVariables()));
      assertEquals(outputRightRange.max(), right.getMax(environment.getVariables()));
   }

   @Test
   @DataProvider({"4,6,22", "4,6,23", "4:7,6:15,23"})
   public void testSetMaxFailed(String inputLeftRange, String inputRightRange, String min) {
      assertSetMaxFailed(parseRange(inputLeftRange), parseRange(inputRightRange), Long.parseLong(min));
      assertSetMaxFailed(parseRange(inputRightRange), parseRange(inputLeftRange), Long.parseLong(min));
   }

   private void assertSetMaxFailed(Range inputLeftRange, Range inputRightRange, long min) {
      Bdd environment = new Bdd(inputLeftRange, inputRightRange);
      Variable left = environment.getLeft();
      Variable right = environment.getRight();
      Multiply m = new Multiply(left, right);

      assertEquals(ExpressionResult.FAILED, m.setMax(environment.getVariables(), min));
   }

   @Test
   public void testSetNot() {
      Range leftRange = parseRange("2:4");
      Range rightRange = parseRange("3:5");
      Bdd environment = new Bdd(leftRange, rightRange);
      Variable left = environment.getLeft();
      Variable right = environment.getRight();
      Variables variables = environment.getVariables();
      Multiply m = new Multiply(environment.getLeft(), environment.getRight());
      for (long i = m.getMin(variables) - 1; i <= m.getMax(variables) + 1; i++) {
         assertEquals(ExpressionResult.NO_CHANGE, m.setNot(variables, i));
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
      Multiply m = new Multiply(environment.getLeft(), environment.getRight());
      assertEquals(ExpressionResult.FAILED, m.setNot(variables, m.getMax(variables)));
   }

   @Test
   public void testWalk() {
      // given
      Consumer<Expression> consumer = mock(Consumer.class);
      Expression left = mock(Expression.class);
      Expression right = mock(Expression.class);
      Multiply testObject = new Multiply(left, right);

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
      Multiply testObject = new Multiply(left, right);
      when(left.replace(function)).thenReturn(new FixedValue(42));
      when(right.replace(function)).thenReturn(new FixedValue(180));

      // when
      Multiply replacement = testObject.replace(function);
      assertNotSame(testObject, replacement);
      assertEquals("Multiply [left=FixedValue [value=42], right=FixedValue [value=180]]", replacement.toString());

      // then
      verify(left).replace(function);
      verify(right).replace(function);
      verifyNoMoreInteractions(function, left, right);
   }
}
