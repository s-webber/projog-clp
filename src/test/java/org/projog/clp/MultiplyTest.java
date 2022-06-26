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

import static org.projog.clp.TestDataParser.parseRange;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class MultiplyTest extends AbstractExpressionTest {
   public MultiplyTest() {
      super(Multiply::new, true);

      when("0", "0").then("0");
      when("42", "0").then("0");
      when("-42", "0").then("0");
      when("42", "5").then("210");
      when("-5", "-42").then("210");
      when("5", "-42").then("-210");
      when("0:5", "0:5").then("0:25");
      when("-5:0", "0:5").then("-25:0");
      when("-5:0", "-5:0").then("0:25");
      when("0:5", "1:5").then("0:25");
      when("1:5", "1:5").then("1:25");
      when("-5:-1", "1:5").then("-25:-1");
      when("-5:-1", "-5:-1").then("1:25");
      when("-5:1", "1:5").then("-25:5");
      when("-5:1", "-5:1").then("-5:25");
      when("MAX", "1").then("MAX");
      when("MAX", "2").then("MAX");
      when("MAX", "MAX").then("MAX");
      when("MAX", "-1").then("MIN+1");
      when("MAX", "-2").then("MIN");
      when("MIN", "1").then("MIN");
      when("MIN", "-1").then("MAX");

      given("-1:0", "1:2").setMin(0).then("0", "1:2");
      given("0:1", "1:2").setMin(0).then("0:1", "1:2");
      given("-1:1", "1:2").setMin(0).then("0:1", "1:2");
      given("1", "2").setMin(2).then("1", "2");
      given("7", "3").setMin(20).then("7", "3");
      given("7", "3").setMin(21).then("7", "3");
      given("1:7", "2:3").setMin(14).then("5:7", "2:3");
      given("1:7", "2:3").setMin(15).then("5:7", "3");
      given("1:7", "2:3").setMin(16).then("6:7", "3");
      given("1:7", "2:3").setMin(17).then("6:7", "3");
      given("1:7", "2:3").setMin(18).then("6:7", "3");
      given("1:7", "2:3").setMin(19).then("7", "3");
      given("1:7", "2:3").setMin(20).then("7", "3");
      given("1:7", "2:3").setMin(21).then("7", "3");
      given("-1", "-2").setMin(2).then("-1", "-2");
      given("-7", "-3").setMin(20).then("-7", "-3");
      given("-7", "-3").setMin(21).then("-7", "-3");
      given("-7:-1", "-3:-2").setMin(14).then("-7:-5", "-3:-2");
      given("-7:-1", "-3:-2").setMin(15).then("-7:-5", "-3");
      given("-7:-1", "-3:-2").setMin(16).then("-7:-6", "-3");
      given("-7:-1", "-3:-2").setMin(17).then("-7:-6", "-3");
      given("-7:-1", "-3:-2").setMin(18).then("-7:-6", "-3");
      given("-7:-1", "-3:-2").setMin(19).then("-7", "-3");
      given("-7:-1", "-3:-2").setMin(20).then("-7", "-3");
      given("-7:-1", "-3:-2").setMin(21).then("-7", "-3");

      given("-1:0", "-2:-1").setMax(0).then("0", "-2:-1");
      given("-1:0", "-2:1").setMax(0).then("-1:0", "-2:1");
      given("-1:1", "1:2").setMax(0).then("-1:0", "1:2");
      given("1", "2").setMax(2).then("1", "2");
      given("4", "6").setMax(24).then("4", "6");
      given("4", "6").setMax(25).then("4", "6");
      given("7", "15").setMax(105).then("7", "15");
      given("4:7", "6:15").setMax(24).then("4", "6");
      given("4:7", "6:15").setMax(25).then("4", "6");
      given("4:7", "6:15").setMax(26).then("4", "6");
      given("4:7", "6:15").setMax(27).then("4", "6");
      given("4:7", "6:15").setMax(28).then("4", "6:7");
      given("4:7", "6:15").setMax(29).then("4", "6:7");
      given("4:7", "6:15").setMax(30).then("4:5", "6:7");
      given("4:7", "6:15").setMax(31).then("4:5", "6:7");
      given("4:7", "6:15").setMax(32).then("4:5", "6:8");
      given("4:7", "6:15").setMax(105).then("4:7", "6:15");
      given("-1", "-2").setMax(2).then("-1", "-2");
      given("-4", "-6").setMax(24).then("-4", "-6");
      given("-4", "-6").setMax(25).then("-4", "-6");
      given("-7", "-15").setMax(105).then("-7", "-15");
      given("-7:-4", "-15:-6").setMax(24).then("-4", "-6");
      given("-7:-4", "-15:-6").setMax(25).then("-4", "-6");
      given("-7:-4", "-15:-6").setMax(26).then("-4", "-6");
      given("-7:-4", "-15:-6").setMax(27).then("-4", "-6");
      given("-7:-4", "-15:-6").setMax(28).then("-4", "-7:-6");
      given("-7:-4", "-15:-6").setMax(29).then("-4", "-7:-6");
      given("-7:-4", "-15:-6").setMax(30).then("-5:-4", "-7:-6");
      given("-7:-4", "-15:-6").setMax(31).then("-5:-4", "-7:-6");
      given("-7:-4", "-15:-6").setMax(32).then("-5:-4", "-8:-6");
      given("-7:-4", "-15:-6").setMax(105).then("-7:-4", "-15:-6");
   }

   @Test(dataProvider = "process", dataProviderClass = TestDataProvider.class)
   @TestData({"7,3,22", "7,3,23", "1:7,2:3,22", "-1,2,0", "-1,2,1"})
   public void testSetMinFailed(String inputLeftRange, String inputRightRange, String min) {
      assertSetMinFailed(parseRange(inputLeftRange), parseRange(inputRightRange), Long.parseLong(min));
      assertSetMinFailed(parseRange(inputRightRange), parseRange(inputLeftRange), Long.parseLong(min));
   }

   private void assertSetMinFailed(Range inputLeftRange, Range inputRightRange, long min) {
      TestUtils environment = new TestUtils(inputLeftRange, inputRightRange);
      Variable left = environment.getLeft();
      Variable right = environment.getRight();
      Multiply m = new Multiply(left, right);

      assertEquals(ExpressionResult.INVALID, m.setMin(environment.getConstraintStore(), min));
   }

   @Test(dataProvider = "process", dataProviderClass = TestDataProvider.class)
   @TestData({"4,6,22", "4,6,23", "4:7,6:15,23", "1,2,0", "-1,-2,0", "1,2,-1", "-1,-2,-1"})
   public void testSetMaxFailed(String inputLeftRange, String inputRightRange, String min) {
      assertSetMaxFailed(parseRange(inputLeftRange), parseRange(inputRightRange), Long.parseLong(min));
      assertSetMaxFailed(parseRange(inputRightRange), parseRange(inputLeftRange), Long.parseLong(min));
   }

   private void assertSetMaxFailed(Range inputLeftRange, Range inputRightRange, long min) {
      TestUtils environment = new TestUtils(inputLeftRange, inputRightRange);
      Variable left = environment.getLeft();
      Variable right = environment.getRight();
      Multiply m = new Multiply(left, right);

      assertEquals(ExpressionResult.INVALID, m.setMax(environment.getConstraintStore(), min));
   }

   @Test
   public void testSetNotNoChange() {
      Range leftRange = parseRange("2:4");
      Range rightRange = parseRange("3:5");
      TestUtils environment = new TestUtils(leftRange, rightRange);
      Variable left = environment.getLeft();
      Variable right = environment.getRight();
      ConstraintStore variables = environment.getConstraintStore();
      Multiply m = new Multiply(environment.getLeft(), environment.getRight());
      for (long i = m.getMin(variables) - 1; i <= m.getMax(variables) + 1; i++) {
         assertEquals(ExpressionResult.VALID, m.setNot(variables, i));
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
      Multiply m = new Multiply(environment.getLeft(), environment.getRight());
      assertEquals(ExpressionResult.VALID, m.setNot(variables, m.getMin(variables)));
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
      Multiply m = new Multiply(environment.getLeft(), environment.getRight());
      assertEquals(ExpressionResult.VALID, m.setNot(variables, m.getMax(variables)));
      assertEquals(leftRange.min(), left.getMin(variables));
      assertEquals(leftRange.max(), left.getMax(variables));
      assertEquals(rightRange.min(), right.getMin(variables));
      assertEquals(rightRange.max() - 1, right.getMax(variables));
   }

   @Test
   public void testSetNotFailed() {
      TestUtils environment = new TestUtils(parseRange("3"), parseRange("5"));
      ConstraintStore variables = environment.getConstraintStore();
      Multiply m = new Multiply(environment.getLeft(), environment.getRight());
      assertEquals(ExpressionResult.INVALID, m.setNot(variables, m.getMax(variables)));
   }
}
