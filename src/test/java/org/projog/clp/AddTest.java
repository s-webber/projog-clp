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

import static org.projog.clp.TestDataParser.parseRange;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class AddTest extends AbstractExpressionTest {
   public AddTest() {
      super(Add::new, true);

      when("0", "0").then("0"); // 0/0
      when("42", "0").then("42"); // +/0
      when("-42", "0").then("-42"); // -/0
      when("42", "5").then("47"); // +/+
      when("-5", "-42").then("-47"); // -/-
      when("5", "-42").then("-37"); // +/-
      // combinations of MIN/MAX
      when("MIN", "MIN").then("MIN");
      when("MAX", "MAX").then("MAX");
      when("MAX", "MIN").then("-1");
      // MIN
      when("MIN", "3").then("MIN+3");
      when("MIN", "2").then("MIN+2");
      when("MIN", "1").then("MIN+1");
      when("MIN", "0").then("MIN");
      when("MIN", "-1").then("MIN");
      when("MIN", "-2").then("MIN");
      when("MIN", "-3").then("MIN");
      // MIN+1
      when("MIN+1", "3").then("MIN+4");
      when("MIN+1", "2").then("MIN+3");
      when("MIN+1", "1").then("MIN+2");
      when("MIN+1", "0").then("MIN+1");
      when("MIN+1", "-1").then("MIN");
      when("MIN+1", "-2").then("MIN");
      when("MIN+1", "-3").then("MIN");
      // MIN+2
      when("MIN+2", "3").then("MIN+5");
      when("MIN+2", "2").then("MIN+4");
      when("MIN+2", "1").then("MIN+3");
      when("MIN+2", "0").then("MIN+2");
      when("MIN+2", "-1").then("MIN+1");
      when("MIN+2", "-2").then("MIN");
      when("MIN+2", "-3").then("MIN");
      // MAX
      when("MAX", "3").then("MAX");
      when("MAX", "2").then("MAX");
      when("MAX", "1").then("MAX");
      when("MAX", "0").then("MAX");
      when("MAX", "-1").then("MAX-1");
      when("MAX", "-2").then("MAX-2");
      when("MAX", "-3").then("MAX-3");
      // MAX-1
      when("MAX-1", "3").then("MAX");
      when("MAX-1", "2").then("MAX");
      when("MAX-1", "1").then("MAX");
      when("MAX-1", "0").then("MAX-1");
      when("MAX-1", "-1").then("MAX-2");
      when("MAX-1", "-2").then("MAX-3");
      when("MAX-1", "-3").then("MAX-4");
      // MAX-2
      when("MAX-2", "3").then("MAX");
      when("MAX-2", "2").then("MAX");
      when("MAX-2", "1").then("MAX-1");
      when("MAX-2", "0").then("MAX-2");
      when("MAX-2", "-1").then("MAX-3");
      when("MAX-2", "-2").then("MAX-4");
      when("MAX-2", "-3").then("MAX-5");
      // -ve/+ve
      when("1:10", "1:10").then("2:20"); // all positive
      when("-10:-1", "-10:-1").then("-20:-2"); // all negative
      when("-7:12", "-6:13").then("-13:25"); // neg:positive,neg:positive

      given("9", "0:3").setMin(8).then("9", "0:3");
      given("MIN:9", "0:3").setMin(8).then("5:9", "0:3");
      given("9", "0:3").setMin(9).then("9", "0:3");
      given("MIN:9", "0:3").setMin(9).then("6:9", "0:3");
      given("9", "0:3").setMin(10).then("9", "1:3");
      given("MIN:9", "0:3").setMin(10).then("7:9", "1:3");
      given("9", "0:3").setMin(11).then("9", "2:3");
      given("MIN:9", "0:3").setMin(11).then("8:9", "2:3");
      given("9", "0:3").setMin(12).then("9", "3:3");
      given("MIN:9", "0:3").setMin(12).then("9:9", "3:3");
      given("9", "1:3").setMin(9).then("9", "1:3");
      given("MIN:9", "1:3").setMin(9).then("6:9", "1:3");
      given("9", "1:3").setMin(10).then("9", "1:3");
      given("MIN:9", "1:3").setMin(10).then("7:9", "1:3");
      given("9", "1:3").setMin(11).then("9", "2:3");
      given("MIN:9", "1:3").setMin(11).then("8:9", "2:3");
      given("9", "1:3").setMin(12).then("9", "3:3");
      given("MIN:9", "1:3").setMin(12).then("9:9", "3:3");
      given("9", "-2:3").setMin(6).then("9", "-2:3");
      given("MIN:9", "-2:3").setMin(6).then("3:9", "-2:3");
      given("9", "-2:3").setMin(7).then("9", "-2:3");
      given("MIN:9", "-2:3").setMin(7).then("4:9", "-2:3");
      given("9", "-2:3").setMin(8).then("9", "-1:3");
      given("MIN:9", "-2:3").setMin(8).then("5:9", "-1:3");
      given("9", "-2:3").setMin(9).then("9", "0:3");
      given("MIN:9", "-2:3").setMin(9).then("6:9", "0:3");
      given("9", "-2:3").setMin(10).then("9", "1:3");
      given("MIN:9", "-2:3").setMin(10).then("7:9", "1:3");
      given("9", "-2:3").setMin(11).then("9", "2:3");
      given("MIN:9", "-2:3").setMin(11).then("8:9", "2:3");
      given("9", "-2:3").setMin(12).then("9", "3:3");
      given("MIN:9", "-2:3").setMin(12).then("9:9", "3:3");
      given("-9", "0:3").setMin(-10).then("-9", "0:3");
      given("MIN:-9", "0:3").setMin(-10).then("-13:-9", "0:3");
      given("-9", "0:3").setMin(-9).then("-9", "0:3");
      given("MIN:-9", "0:3").setMin(-9).then("-12:-9", "0:3");
      given("-9", "0:3").setMin(-8).then("-9", "1:3");
      given("MIN:-9", "0:3").setMin(-8).then("-11:-9", "1:3");
      given("-9", "0:3").setMin(-7).then("-9", "2:3");
      given("MIN:-9", "0:3").setMin(-7).then("-10:-9", "2:3");
      given("-9", "0:3").setMin(-6).then("-9", "3:3");
      given("MIN:-9", "0:3").setMin(-6).then("-9:-9", "3:3");
      given("-9", "1:3").setMin(-9).then("-9", "1:3");
      given("MIN:-9", "1:3").setMin(-9).then("-12:-9", "1:3");
      given("-9", "1:3").setMin(-8).then("-9", "1:3");
      given("MIN:-9", "1:3").setMin(-8).then("-11:-9", "1:3");
      given("-9", "1:3").setMin(-7).then("-9", "2:3");
      given("MIN:-9", "1:3").setMin(-7).then("-10:-9", "2:3");
      given("-9", "1:3").setMin(-6).then("-9", "3:3");
      given("MIN:-9", "1:3").setMin(-6).then("-9:-9", "3:3");
      given("-9", "-2:3").setMin(-12).then("-9", "-2:3");
      given("MIN:-9", "-2:3").setMin(-12).then("-15:-9", "-2:3");
      given("-9", "-2:3").setMin(-11).then("-9", "-2:3");
      given("MIN:-9", "-2:3").setMin(-11).then("-14:-9", "-2:3");
      given("-9", "-2:3").setMin(-10).then("-9", "-1:3");
      given("MIN:-9", "-2:3").setMin(-10).then("-13:-9", "-1:3");
      given("-9", "-2:3").setMin(-9).then("-9", "0:3");
      given("MIN:-9", "-2:3").setMin(-9).then("-12:-9", "0:3");
      given("-9", "-2:3").setMin(-8).then("-9", "1:3");
      given("MIN:-9", "-2:3").setMin(-8).then("-11:-9", "1:3");
      given("-9", "-2:3").setMin(-7).then("-9", "2:3");
      given("MIN:-9", "-2:3").setMin(-7).then("-10:-9", "2:3");
      given("-9", "-2:3").setMin(-6).then("-9", "3:3");
      given("MIN:-9", "-2:3").setMin(-6).then("-9:-9", "3:3");
      given("0", "0:3").setMin(-1).then("0", "0:3");
      given("MIN:0", "0:3").setMin(-1).then("-4:0", "0:3");
      given("0", "0:3").setMin(0).then("0", "0:3");
      given("MIN:0", "0:3").setMin(0).then("-3:0", "0:3");
      given("0", "0:3").setMin(1).then("0", "1:3");
      given("MIN:0", "0:3").setMin(1).then("-2:0", "1:3");
      given("0", "0:3").setMin(2).then("0", "2:3");
      given("MIN:0", "0:3").setMin(2).then("-1:0", "2:3");
      given("0", "0:3").setMin(3).then("0", "3:3");
      given("MIN:0", "0:3").setMin(3).then("0:0", "3:3");
      given("0", "1:3").setMin(0).then("0", "1:3");
      given("MIN:0", "1:3").setMin(0).then("-3:0", "1:3");
      given("0", "1:3").setMin(1).then("0", "1:3");
      given("MIN:0", "1:3").setMin(1).then("-2:0", "1:3");
      given("0", "1:3").setMin(2).then("0", "2:3");
      given("MIN:0", "1:3").setMin(2).then("-1:0", "2:3");
      given("0", "1:3").setMin(3).then("0", "3:3");
      given("MIN:0", "1:3").setMin(3).then("0:0", "3:3");
      given("0", "-2:3").setMin(-3).then("0", "-2:3");
      given("MIN:0", "-2:3").setMin(-3).then("-6:0", "-2:3");
      given("0", "-2:3").setMin(-2).then("0", "-2:3");
      given("MIN:0", "-2:3").setMin(-2).then("-5:0", "-2:3");
      given("0", "-2:3").setMin(-1).then("0", "-1:3");
      given("MIN:0", "-2:3").setMin(-1).then("-4:0", "-1:3");
      given("0", "-2:3").setMin(0).then("0", "0:3");
      given("MIN:0", "-2:3").setMin(0).then("-3:0", "0:3");
      given("0", "-2:3").setMin(1).then("0", "1:3");
      given("MIN:0", "-2:3").setMin(1).then("-2:0", "1:3");
      given("0", "-2:3").setMin(2).then("0", "2:3");
      given("MIN:0", "-2:3").setMin(2).then("-1:0", "2:3");
      given("0", "-2:3").setMin(3).then("0", "3:3");
      given("MIN:0", "-2:3").setMin(3).then("0:0", "3:3");

      given("9", "0:3").setMax(9).then("9", "0:0");
      given("9:MAX", "0:3").setMax(9).then("9:9", "0:0");
      given("9", "0:3").setMax(10).then("9", "0:1");
      given("9:MAX", "0:3").setMax(10).then("9:10", "0:1");
      given("9", "0:3").setMax(11).then("9", "0:2");
      given("9:MAX", "0:3").setMax(11).then("9:11", "0:2");
      given("9", "0:3").setMax(12).then("9", "0:3");
      given("9:MAX", "0:3").setMax(12).then("9:12", "0:3");
      given("9", "0:3").setMax(13).then("9", "0:3");
      given("9:MAX", "0:3").setMax(13).then("9:13", "0:3");
      given("9", "1:3").setMax(10).then("9", "1:1");
      given("9:MAX", "1:3").setMax(10).then("9:9", "1:1");
      given("9", "1:3").setMax(11).then("9", "1:2");
      given("9:MAX", "1:3").setMax(11).then("9:10", "1:2");
      given("9", "1:3").setMax(12).then("9", "1:3");
      given("9:MAX", "1:3").setMax(12).then("9:11", "1:3");
      given("9", "1:3").setMax(13).then("9", "1:3");
      given("9:MAX", "1:3").setMax(13).then("9:12", "1:3");
      given("9", "-2:3").setMax(7).then("9", "-2:-2");
      given("9:MAX", "-2:3").setMax(7).then("9:9", "-2:-2");
      given("9", "-2:3").setMax(8).then("9", "-2:-1");
      given("9:MAX", "-2:3").setMax(8).then("9:10", "-2:-1");
      given("9", "-2:3").setMax(9).then("9", "-2:0");
      given("9:MAX", "-2:3").setMax(9).then("9:11", "-2:0");
      given("9", "-2:3").setMax(10).then("9", "-2:1");
      given("9:MAX", "-2:3").setMax(10).then("9:12", "-2:1");
      given("9", "-2:3").setMax(11).then("9", "-2:2");
      given("9:MAX", "-2:3").setMax(11).then("9:13", "-2:2");
      given("9", "-2:3").setMax(12).then("9", "-2:3");
      given("9:MAX", "-2:3").setMax(12).then("9:14", "-2:3");
      given("9", "-2:3").setMax(13).then("9", "-2:3");
      given("9:MAX", "-2:3").setMax(13).then("9:15", "-2:3");
      given("-9", "0:3").setMax(-9).then("-9", "0:0");
      given("-9:MAX", "0:3").setMax(-9).then("-9:-9", "0:0");
      given("-9", "0:3").setMax(-8).then("-9", "0:1");
      given("-9:MAX", "0:3").setMax(-8).then("-9:-8", "0:1");
      given("-9", "0:3").setMax(-7).then("-9", "0:2");
      given("-9:MAX", "0:3").setMax(-7).then("-9:-7", "0:2");
      given("-9", "0:3").setMax(-6).then("-9", "0:3");
      given("-9:MAX", "0:3").setMax(-6).then("-9:-6", "0:3");
      given("-9", "0:3").setMax(-5).then("-9", "0:3");
      given("-9:MAX", "0:3").setMax(-5).then("-9:-5", "0:3");
      given("-9", "1:3").setMax(-8).then("-9", "1:1");
      given("-9:MAX", "1:3").setMax(-8).then("-9:-9", "1:1");
      given("-9", "1:3").setMax(-7).then("-9", "1:2");
      given("-9:MAX", "1:3").setMax(-7).then("-9:-8", "1:2");
      given("-9", "1:3").setMax(-6).then("-9", "1:3");
      given("-9:MAX", "1:3").setMax(-6).then("-9:-7", "1:3");
      given("-9", "1:3").setMax(-5).then("-9", "1:3");
      given("-9:MAX", "1:3").setMax(-5).then("-9:-6", "1:3");
      given("-9", "-2:3").setMax(-11).then("-9", "-2:-2");
      given("-9:MAX", "-2:3").setMax(-11).then("-9:-9", "-2:-2");
      given("-9", "-2:3").setMax(-10).then("-9", "-2:-1");
      given("-9:MAX", "-2:3").setMax(-10).then("-9:-8", "-2:-1");
      given("-9", "-2:3").setMax(-9).then("-9", "-2:0");
      given("-9:MAX", "-2:3").setMax(-9).then("-9:-7", "-2:0");
      given("-9", "-2:3").setMax(-8).then("-9", "-2:1");
      given("-9:MAX", "-2:3").setMax(-8).then("-9:-6", "-2:1");
      given("-9", "-2:3").setMax(-7).then("-9", "-2:2");
      given("-9:MAX", "-2:3").setMax(-7).then("-9:-5", "-2:2");
      given("-9", "-2:3").setMax(-6).then("-9", "-2:3");
      given("-9:MAX", "-2:3").setMax(-6).then("-9:-4", "-2:3");
      given("-9", "-2:3").setMax(-5).then("-9", "-2:3");
      given("-9:MAX", "-2:3").setMax(-5).then("-9:-3", "-2:3");
      given("0", "0:3").setMax(0).then("0", "0:0");
      given("0:MAX", "0:3").setMax(0).then("0:0", "0:0");
      given("0", "0:3").setMax(1).then("0", "0:1");
      given("0:MAX", "0:3").setMax(1).then("0:1", "0:1");
      given("0", "0:3").setMax(2).then("0", "0:2");
      given("0:MAX", "0:3").setMax(2).then("0:2", "0:2");
      given("0", "0:3").setMax(3).then("0", "0:3");
      given("0:MAX", "0:3").setMax(3).then("0:3", "0:3");
      given("0", "0:3").setMax(4).then("0", "0:3");
      given("0:MAX", "0:3").setMax(4).then("0:4", "0:3");
      given("0", "1:3").setMax(1).then("0", "1:1");
      given("0:MAX", "1:3").setMax(1).then("0:0", "1:1");
      given("0", "1:3").setMax(2).then("0", "1:2");
      given("0:MAX", "1:3").setMax(2).then("0:1", "1:2");
      given("0", "1:3").setMax(3).then("0", "1:3");
      given("0:MAX", "1:3").setMax(3).then("0:2", "1:3");
      given("0", "1:3").setMax(4).then("0", "1:3");
      given("0:MAX", "1:3").setMax(4).then("0:3", "1:3");
      given("0", "-2:3").setMax(-2).then("0", "-2:-2");
      given("0:MAX", "-2:3").setMax(-2).then("0:0", "-2:-2");
      given("0", "-2:3").setMax(-1).then("0", "-2:-1");
      given("0:MAX", "-2:3").setMax(-1).then("0:1", "-2:-1");
      given("0", "-2:3").setMax(0).then("0", "-2:0");
      given("0:MAX", "-2:3").setMax(0).then("0:2", "-2:0");
      given("0", "-2:3").setMax(1).then("0", "-2:1");
      given("0:MAX", "-2:3").setMax(1).then("0:3", "-2:1");
      given("0", "-2:3").setMax(2).then("0", "-2:2");
      given("0:MAX", "-2:3").setMax(2).then("0:4", "-2:2");
      given("0", "-2:3").setMax(3).then("0", "-2:3");
      given("0:MAX", "-2:3").setMax(3).then("0:5", "-2:3");
      given("0", "-2:3").setMax(4).then("0", "-2:3");
      given("0:MAX", "-2:3").setMax(4).then("0:6", "-2:3");
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

   @Test(dataProvider = "data")
   public void testSetMinSuccess(String leftRange, String rightRange) {
      assertSetMinSuccess(parseRange(leftRange), parseRange(rightRange));
      assertSetMinSuccess(parseRange(rightRange), parseRange(leftRange));
   }

   private void assertSetMinSuccess(Range leftRange, Range rightRange) {
      TestUtils environment = new TestUtils(leftRange, rightRange);
      ConstraintStore variables = environment.getConstraintStore();
      Add a = new Add(environment.getLeft(), environment.getRight());

      long max = a.getMax(variables);
      assertEquals(ExpressionResult.VALID, a.setMin(variables, max));
      assertEquals(max, a.getMin(variables));
   }

   @Test(dataProvider = "data")
   public void testSetMaxSuccess(String leftRange, String rightRange) {
      assertSetMaxSuccess(parseRange(leftRange), parseRange(rightRange));
      assertSetMaxSuccess(parseRange(rightRange), parseRange(leftRange));
   }

   private void assertSetMaxSuccess(Range leftRange, Range rightRange) {
      TestUtils environment = new TestUtils(leftRange, rightRange);
      ConstraintStore variables = environment.getConstraintStore();
      Add a = new Add(environment.getLeft(), environment.getRight());

      long min = a.getMin(variables);
      assertEquals(ExpressionResult.VALID, a.setMax(variables, min));
      assertEquals(min, a.getMax(variables));
   }

   @Test(dataProvider = "data")
   public void testSetMinFailed(String leftRange, String rightRange) {
      assertSetMinFailed(parseRange(leftRange), parseRange(rightRange));
      assertSetMinFailed(parseRange(rightRange), parseRange(leftRange));
   }

   private void assertSetMinFailed(Range leftRange, Range rightRange) {
      for (int i = 1; i < 4; i++) {
         TestUtils environment = new TestUtils(leftRange, rightRange);
         Add a = new Add(environment.getLeft(), environment.getRight());

         long max = a.getMax(environment.getConstraintStore());
         long newMax = max + i;
         assertEquals(ExpressionResult.INVALID, a.setMin(environment.getConstraintStore(), newMax));
      }
   }

   @Test(dataProvider = "data")
   public void testSetMaxFailed(String leftRange, String rightRange) {
      assertSetMaxFailed(parseRange(leftRange), parseRange(rightRange));
      assertSetMaxFailed(parseRange(rightRange), parseRange(leftRange));
   }

   private void assertSetMaxFailed(Range leftRange, Range rightRange) {
      for (int i = 1; i < 4; i++) {
         TestUtils environment = new TestUtils(leftRange, rightRange);
         Add a = new Add(environment.getLeft(), environment.getRight());

         long min = a.getMin(environment.getConstraintStore());
         long newMin = min - i;
         assertEquals(ExpressionResult.INVALID, a.setMax(environment.getConstraintStore(), newMin));
      }
   }

   @Test(dataProvider = "process", dataProviderClass = TestDataProvider.class)
   @TestData({"MAX,MAX", "MAX,1", "MAX-1,2", "MAX-1,3", "MAX-2,3", "MAX-3,4",})
   public void testSetMaxOverflow(String leftRange, String rightRange) {
      assertSetMaxOverflow(parseRange(leftRange), parseRange(rightRange));
      assertSetMaxOverflow(parseRange(rightRange), parseRange(leftRange));
   }

   private static void assertSetMaxOverflow(Range leftRange, Range rightRange) {
      TestUtils environment = new TestUtils(leftRange, rightRange);
      Add a = new Add(environment.getLeft(), environment.getRight());

      assertEquals(ExpressionResult.INVALID, a.setMax(environment.getConstraintStore(), a.getMin(environment.getConstraintStore())));
   }

   @Test
   public void testSetNotNoChange() {
      Range leftRange = parseRange("2:4");
      Range rightRange = parseRange("3:5");
      TestUtils environment = new TestUtils(leftRange, rightRange);
      Variable left = environment.getLeft();
      Variable right = environment.getRight();
      ConstraintStore variables = environment.getConstraintStore();
      Add a = new Add(environment.getLeft(), environment.getRight());
      for (long i = a.getMin(variables) - 1; i <= a.getMax(variables) + 1; i++) {
         assertEquals(ExpressionResult.VALID, a.setNot(variables, i));
         assertEquals(leftRange.min(), left.getMin(variables));
         assertEquals(leftRange.max(), left.getMax(variables));
         assertEquals(rightRange.min(), right.getMin(variables));
         assertEquals(rightRange.max(), right.getMax(variables));
      }
   }

   @Test
   public void testSetNotUpdatedMin() {
      Range leftRange = parseRange("3");
      Range rightRange = parseRange("3:5");
      TestUtils environment = new TestUtils(leftRange, rightRange);
      Variable left = environment.getLeft();
      Variable right = environment.getRight();
      ConstraintStore variables = environment.getConstraintStore();
      Add a = new Add(environment.getLeft(), environment.getRight());
      assertEquals(ExpressionResult.VALID, a.setNot(variables, a.getMin(variables)));
      assertEquals(leftRange.min(), left.getMin(variables));
      assertEquals(leftRange.max(), left.getMax(variables));
      assertEquals(rightRange.min() + 1, right.getMin(variables));
      assertEquals(rightRange.max(), right.getMax(variables));
   }

   @Test
   public void testSetNotUpdatedMax() {
      Range leftRange = parseRange("3");
      Range rightRange = parseRange("3:5");
      TestUtils environment = new TestUtils(leftRange, rightRange);
      Variable left = environment.getLeft();
      Variable right = environment.getRight();
      ConstraintStore variables = environment.getConstraintStore();
      Add a = new Add(environment.getLeft(), environment.getRight());
      assertEquals(ExpressionResult.VALID, a.setNot(variables, a.getMax(variables)));
      assertEquals(leftRange.min(), left.getMin(variables));
      assertEquals(leftRange.max(), left.getMax(variables));
      assertEquals(rightRange.min(), right.getMin(variables));
      assertEquals(rightRange.max() - 1, right.getMax(variables));
   }

   @Test
   public void testSetNotFailed() {
      TestUtils environment = new TestUtils(parseRange("3"), parseRange("5"));
      ConstraintStore variables = environment.getConstraintStore();
      Add a = new Add(environment.getLeft(), environment.getRight());
      assertEquals(ExpressionResult.INVALID, a.setNot(variables, a.getMax(variables)));
   }
}
