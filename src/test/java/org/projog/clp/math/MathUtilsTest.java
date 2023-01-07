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

import static org.projog.clp.test.RangeParser.parseLong;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.math.BigInteger;

import org.projog.clp.test.TestData;
import org.projog.clp.test.TestDataProvider;
import org.testng.annotations.Test;

public class MathUtilsTest {
   @Test(dataProvider = "process", dataProviderClass = TestDataProvider.class)
   @TestData({
               "0,0,0", // 0/0
               "42,0,42", // +/0
               "-42,0,-42", // -/0
               "42,5,47", // +/+
               "-5,-42,-47", // -/-
               "5,-42,-37", // +/-
               // combinations of MIN/MAX
               "MIN,MIN,MIN",
               "MAX,MAX,MAX",
               "MIN,MAX,-1",
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
               "MAX-2,-3,MAX-5",})
   public void testAdd(String left, String right, String expected) {
      assertAdd(parseLong(left), parseLong(right), parseLong(expected));
      assertAdd(parseLong(right), parseLong(left), parseLong(expected));
   }

   private static void assertAdd(long left, long right, long expected) {
      assertEquals(expected, MathUtils.safeAdd(left, right));

      // if safeAdd produces different result than normal addition then confirm result has the correct sign
      BigInteger bigInteger = new BigInteger("" + left).add(new BigInteger("" + right));
      if (expected != left + right) {
         assertOverflowRounding(expected, bigInteger);
      } else {
         assertEquals(bigInteger, new BigInteger("" + expected));
      }
   }

   @Test(dataProvider = "process", dataProviderClass = TestDataProvider.class)
   @TestData({
               "0,0,0", // 0/0
               "0,42,-42", // 0/+
               "42,0,42", // +/0
               "0,-42,42", // 0/-
               "-42,0,-42", // -/0
               "5,42,-37", // +/+
               "42,5,37", // +/+
               "-5,-42,37", // -/-
               "-42,-5,-37", // -/-
               "5,-42,47", // +/-
               "-42,5,-47", // -/+
               // combinations of MIN/MAX
               "MIN,MIN,0",
               "MAX,MAX,0",
               "MIN,MAX,MIN",
               "MAX,MIN,MAX",
               // MIN
               "MIN,3,MIN",
               "MIN,2,MIN",
               "MIN,1,MIN",
               "MIN,0,MIN",
               "MIN,-1,MIN+1",
               "MIN,-2,MIN+2",
               "MIN,-3,MIN+3",
               // MIN+1
               "MIN+1,3,MIN",
               "MIN+1,2,MIN",
               "MIN+1,1,MIN",
               "MIN+1,0,MIN+1",
               "MIN+1,-1,MIN+2",
               "MIN+1,-2,MIN+3",
               "MIN+1,-3,MIN+4",
               // MIN+2
               "MIN+2,3,MIN",
               "MIN+2,2,MIN",
               "MIN+2,1,MIN+1",
               "MIN+2,0,MIN+2",
               "MIN+2,-1,MIN+3",
               "MIN+2,-2,MIN+4",
               "MIN+2,-3,MIN+5",
               // MAX
               "MAX,3,MAX-3",
               "MAX,2,MAX-2",
               "MAX,1,MAX-1",
               "MAX,0,MAX",
               "MAX,-1,MAX",
               "MAX,-2,MAX",
               "MAX,-3,MAX",
               // MAX-1
               "MAX-1,3,MAX-4",
               "MAX-1,2,MAX-3",
               "MAX-1,1,MAX-2",
               "MAX-1,0,MAX-1",
               "MAX-1,-1,MAX",
               "MAX-1,-2,MAX",
               "MAX-1,-3,MAX",
               // MAX-2
               "MAX-2,3,MAX-5",
               "MAX-2,2,MAX-4",
               "MAX-2,1,MAX-3",
               "MAX-2,0,MAX-2",
               "MAX-2,-1,MAX-1",
               "MAX-2,-2,MAX",
               "MAX-2,-3,MAX",})
   public void testSubtract(String left, String right, String expected) {
      assertSubtract(parseLong(left), parseLong(right), parseLong(expected));
   }

   private static void assertSubtract(long left, long right, long expected) {
      assertEquals(expected, MathUtils.safeSubtract(left, right));

      // if safeSubtract produces different result than normal subtraction then confirm result has the correct sign
      BigInteger bigInteger = new BigInteger("" + left).subtract(new BigInteger("" + right));
      if (expected != left - right) {
         assertOverflowRounding(expected, bigInteger);
      } else {
         assertEquals(bigInteger, new BigInteger("" + expected));
      }
   }

   @Test(dataProvider = "process", dataProviderClass = TestDataProvider.class)
   @TestData({
               "0,0,0", // 0/0
               "42,0,0", // +/0
               "-42,0,0", // -/0
               "42,5,210", // +/+
               "-5,-42,210", // -/-
               "5,-42,-210", // +/-
               "MAX,1,MAX",
               "MAX,2,MAX",
               "MAX,MAX,MAX",
               "MAX,-1,MIN+1",
               "MAX,-2,MIN",
               "MIN,1,MIN",
               "MIN,-1,MAX"})
   public void testMultiply(String left, String right, String expected) {
      assertMultiply(parseLong(left), parseLong(right), parseLong(expected));
      assertMultiply(parseLong(right), parseLong(left), parseLong(expected));
   }

   private static void assertMultiply(long left, long right, long expected) {
      assertEquals(expected, MathUtils.safeMultiply(left, right));

      // if safeMultiply produces different result than normal multiplication then confirm result has the correct sign
      BigInteger bigInteger = new BigInteger("" + left).multiply(new BigInteger("" + right));
      if (expected != left * right) {
         assertOverflowRounding(expected, bigInteger);
      } else {
         assertEquals(bigInteger, new BigInteger("" + expected));
      }
   }

   @Test(dataProvider = "process", dataProviderClass = TestDataProvider.class)
   @TestData({
               "27,9,3", //
               "35,9,3",
               "-27,-9,3",
               "-35,-9,3",
               "-27,9,-3",
               "-35,9,-3",
               "27,-9,-3",
               "35,-9,-3",
               "MAX,MAX,1",
               "MIN,MIN,1",
               "MAX,MIN,0",
               "MIN,MAX,-1",
               "MAX,1,MAX",
               "MIN,1,MIN",
               "MAX,-1,MIN+1",
               "MIN,-1,MAX"})
   public void testDivide(String left, String right, String expected) {
      assertDivide(parseLong(left), parseLong(right), parseLong(expected));
   }

   private static void assertDivide(long left, long right, long expected) {
      assertEquals(expected, MathUtils.safeDivide(left, right));

      // if safeDivide produces different result than normal division then confirm result has the correct sign
      BigInteger bigInteger = new BigInteger("" + left).divide(new BigInteger("" + right));
      if (expected != left / right) {
         assertOverflowRounding(expected, bigInteger);
      } else {
         assertEquals(bigInteger, new BigInteger("" + expected));
      }
   }

   @Test(dataProvider = "process", dataProviderClass = TestDataProvider.class)
   @TestData({"0,0", "42,42", "-42,42", "MAX,MAX", "MAX-1,MAX-1", "MAX-2,MAX-2", "MIN,MAX", "MIN+1,MAX", "MIN+2,MAX-1"})
   public void testAbsolute(String input, String expected) {
      assertEquals(parseLong(expected), MathUtils.safeAbs(parseLong(input)));
   }

   @Test(dataProvider = "process", dataProviderClass = TestDataProvider.class)
   @TestData({"0,0", "42,-42", "42,-42", "MAX,MIN+1", "MAX-1,MIN+2", "MAX-2,MIN+3", "MIN,MAX", "MIN+1,MAX", "MIN+2,MAX-1"})
   public void testMinus(String inputString, String expected) {
      long input = parseLong(inputString);
      assertEquals(parseLong(expected), MathUtils.safeMinus(input));
      assertEquals(MathUtils.safeSubtract(0, input), MathUtils.safeMinus(input));
   }

   private static void assertOverflowRounding(long expected, BigInteger bigInteger) {
      try {
         bigInteger.longValueExact();
         fail();
      } catch (ArithmeticException e) {
         // expected
         assertEquals("BigInteger out of long range", e.getMessage());
      }

      if (expected == Long.MAX_VALUE) {
         assertTrue(bigInteger.compareTo(new BigInteger("" + expected)) > 0);
      } else if (expected == Long.MIN_VALUE) {
         assertTrue(bigInteger.compareTo(new BigInteger("" + expected)) < 0);
      } else {
         fail();
      }
   }
}
