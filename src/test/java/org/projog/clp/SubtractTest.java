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
import static org.projog.clp.TestDataParser.parseRange;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

@RunWith(DataProviderRunner.class)
public class SubtractTest extends AbstractExpressionTest {
   public SubtractTest() {
      super(Subtract::new);
   }

   @Test
   @DataProvider({
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
               "MAX-2,-3,MAX",
               // -ve/+ve
               "1:10,1:10,-9:9", // all positive
               "-10:-1,-10:-1,-9:9", // all negative
               "-7:12,-6:13,-20:18", // neg:positive,neg:positive
   })
   public void testGetMinMax(String leftRange, String rightRange, String range) {
      TestUtils environment = new TestUtils(parseRange(leftRange), parseRange(rightRange));
      Subtract s = new Subtract(environment.getLeft(), environment.getRight());

      assertEquals(parseRange(range).min(), s.getMin(environment.getConstraintStore()));
      assertEquals(parseRange(range).max(), s.getMax(environment.getConstraintStore()));
   }

   @Test
   @DataProvider({
               "9,0:3,5,9,0:3",
               "MIN:9,0:3,5,5:9,0:3",
               "9,0:3,6,9,0:3",
               "MIN:9,0:3,6,6:9,0:3",
               "9,0:3,7,9,0:2",
               "MIN:9,0:3,7,7:9,0:2",
               "9,0:3,8,9,0:1",
               "MIN:9,0:3,8,8:9,0:1",
               "9,0:3,9,9,0:0",
               "MIN:9,0:3,9,9:9,0:0",
               "9,1:3,5,9,1:3",
               "MIN:9,1:3,5,6:9,1:3",
               "9,1:3,6,9,1:3",
               "MIN:9,1:3,6,7:9,1:3",
               "9,1:3,7,9,1:2",
               "MIN:9,1:3,7,8:9,1:2",
               "9,1:3,8,9,1:1",
               "MIN:9,1:3,8,9:9,1:1",
               "9,-2:3,5,9,-2:3",
               "MIN:9,-2:3,5,3:9,-2:3",
               "9,-2:3,6,9,-2:3",
               "MIN:9,-2:3,6,4:9,-2:3",
               "9,-2:3,7,9,-2:2",
               "MIN:9,-2:3,7,5:9,-2:2",
               "9,-2:3,8,9,-2:1",
               "MIN:9,-2:3,8,6:9,-2:1",
               "9,-2:3,9,9,-2:0",
               "MIN:9,-2:3,9,7:9,-2:0",
               "9,-2:3,10,9,-2:-1",
               "MIN:9,-2:3,10,8:9,-2:-1",
               "9,-2:3,11,9,-2:-2",
               "MIN:9,-2:3,11,9:9,-2:-2",
               "-9,0:3,-13,-9,0:3",
               "MIN:-9,0:3,-13,-13:-9,0:3",
               "-9,0:3,-12,-9,0:3",
               "MIN:-9,0:3,-12,-12:-9,0:3",
               "-9,0:3,-11,-9,0:2",
               "MIN:-9,0:3,-11,-11:-9,0:2",
               "-9,0:3,-10,-9,0:1",
               "MIN:-9,0:3,-10,-10:-9,0:1",
               "-9,0:3,-9,-9,0:0",
               "MIN:-9,0:3,-9,-9:-9,0:0",
               "-9,1:3,-13,-9,1:3",
               "MIN:-9,1:3,-13,-12:-9,1:3",
               "-9,1:3,-12,-9,1:3",
               "MIN:-9,1:3,-12,-11:-9,1:3",
               "-9,1:3,-11,-9,1:2",
               "MIN:-9,1:3,-11,-10:-9,1:2",
               "-9,1:3,-10,-9,1:1",
               "MIN:-9,1:3,-10,-9:-9,1:1",
               "-9,-2:3,-13,-9,-2:3",
               "MIN:-9,-2:3,-13,-15:-9,-2:3",
               "-9,-2:3,-12,-9,-2:3",
               "MIN:-9,-2:3,-12,-14:-9,-2:3",
               "-9,-2:3,-11,-9,-2:2",
               "MIN:-9,-2:3,-11,-13:-9,-2:2",
               "-9,-2:3,-10,-9,-2:1",
               "MIN:-9,-2:3,-10,-12:-9,-2:1",
               "-9,-2:3,-9,-9,-2:0",
               "MIN:-9,-2:3,-9,-11:-9,-2:0",
               "-9,-2:3,-8,-9,-2:-1",
               "MIN:-9,-2:3,-8,-10:-9,-2:-1",
               "-9,-2:3,-7,-9,-2:-2",
               "MIN:-9,-2:3,-7,-9:-9,-2:-2",
               "0,0:3,-4,0,0:3",
               "MIN:0,0:3,-4,-4:0,0:3",
               "0,0:3,-3,0,0:3",
               "MIN:0,0:3,-3,-3:0,0:3",
               "0,0:3,-2,0,0:2",
               "MIN:0,0:3,-2,-2:0,0:2",
               "0,0:3,-1,0,0:1",
               "MIN:0,0:3,-1,-1:0,0:1",
               "0,0:3,0,0,0:0",
               "MIN:0,0:3,0,0:0,0:0",
               "0,1:3,-4,0,1:3",
               "MIN:0,1:3,-4,-3:0,1:3",
               "0,1:3,-3,0,1:3",
               "MIN:0,1:3,-3,-2:0,1:3",
               "0,1:3,-2,0,1:2",
               "MIN:0,1:3,-2,-1:0,1:2",
               "0,1:3,-1,0,1:1",
               "MIN:0,1:3,-1,0:0,1:1",
               "0,-2:3,-4,0,-2:3",
               "MIN:0,-2:3,-4,-6:0,-2:3",
               "0,-2:3,-3,0,-2:3",
               "MIN:0,-2:3,-3,-5:0,-2:3",
               "0,-2:3,-2,0,-2:2",
               "MIN:0,-2:3,-2,-4:0,-2:2",
               "0,-2:3,-1,0,-2:1",
               "MIN:0,-2:3,-1,-3:0,-2:1",
               "0,-2:3,0,0,-2:0",
               "MIN:0,-2:3,0,-2:0,-2:0",
               "0,-2:3,1,0,-2:-1",
               "MIN:0,-2:3,1,-1:0,-2:-1",
               "0,-2:3,2,0,-2:-2",
               "MIN:0,-2:3,2,0:0,-2:-2",})
   public void testSetMin(String inputLeftRange, String inputRightRange, String min, String outputLeftRange, String outputRightRange) {
      assertSetMin(parseRange(inputLeftRange), parseRange(inputRightRange), Long.parseLong(min), parseRange(outputLeftRange), parseRange(outputRightRange));
   }

   private void assertSetMin(Range inputLeftRange, Range inputRightRange, long min, Range outputLeftRange, Range outputRightRange) {
      TestUtils environment = new TestUtils(inputLeftRange, inputRightRange);
      Variable left = environment.getLeft();
      Variable right = environment.getRight();
      ConstraintStore variables = environment.getConstraintStore();
      Subtract s = new Subtract(left, right);

      assertNotEquals(ExpressionResult.FAILED, s.setMin(variables, min));
      assertEquals(outputLeftRange.min(), left.getMin(variables));
      assertEquals(outputLeftRange.max(), left.getMax(variables));
      assertEquals(outputRightRange.min(), right.getMin(variables));
      assertEquals(outputRightRange.max(), right.getMax(variables));
   }

   @Test
   @DataProvider({
               "9,0:3,6,9,3:3",
               "9:MAX,0:3,6,9:9,3:3",
               "9,0:3,7,9,2:3",
               "9:MAX,0:3,7,9:10,2:3",
               "9,0:3,8,9,1:3",
               "9:MAX,0:3,8,9:11,1:3",
               "9,0:3,9,9,0:3",
               "9:MAX,0:3,9,9:12,0:3",
               "9,0:3,10,9,0:3",
               "9:MAX,0:3,10,9:13,0:3",
               "9,1:3,6,9,3:3",
               "9:MAX,1:3,6,9:9,3:3",
               "9,1:3,7,9,2:3",
               "9:MAX,1:3,7,9:10,2:3",
               "9,1:3,8,9,1:3",
               "9:MAX,1:3,8,9:11,1:3",
               "9,1:3,9,9,1:3",
               "9:MAX,1:3,9,9:12,1:3",
               "9,-2:3,6,9,3:3",
               "9:MAX,-2:3,6,9:9,3:3",
               "9,-2:3,7,9,2:3",
               "9:MAX,-2:3,7,9:10,2:3",
               "9,-2:3,8,9,1:3",
               "9:MAX,-2:3,8,9:11,1:3",
               "9,-2:3,9,9,0:3",
               "9:MAX,-2:3,9,9:12,0:3",
               "9,-2:3,10,9,-1:3",
               "9:MAX,-2:3,10,9:13,-1:3",
               "9,-2:3,11,9,-2:3",
               "9:MAX,-2:3,11,9:14,-2:3",
               "9,-2:3,12,9,-2:3",
               "9:MAX,-2:3,12,9:15,-2:3",
               "-9,0:3,-12,-9,3:3",
               "-9:MAX,0:3,-12,-9:-9,3:3",
               "-9,0:3,-11,-9,2:3",
               "-9:MAX,0:3,-11,-9:-8,2:3",
               "-9,0:3,-10,-9,1:3",
               "-9:MAX,0:3,-10,-9:-7,1:3",
               "-9,0:3,-9,-9,0:3",
               "-9:MAX,0:3,-9,-9:-6,0:3",
               "-9,0:3,-8,-9,0:3",
               "-9:MAX,0:3,-8,-9:-5,0:3",
               "-9,1:3,-12,-9,3:3",
               "-9:MAX,1:3,-12,-9:-9,3:3",
               "-9,1:3,-11,-9,2:3",
               "-9:MAX,1:3,-11,-9:-8,2:3",
               "-9,1:3,-10,-9,1:3",
               "-9:MAX,1:3,-10,-9:-7,1:3",
               "-9,1:3,-9,-9,1:3",
               "-9:MAX,1:3,-9,-9:-6,1:3",
               "-9,-2:3,-12,-9,3:3",
               "-9:MAX,-2:3,-12,-9:-9,3:3",
               "-9,-2:3,-11,-9,2:3",
               "-9:MAX,-2:3,-11,-9:-8,2:3",
               "-9,-2:3,-10,-9,1:3",
               "-9:MAX,-2:3,-10,-9:-7,1:3",
               "-9,-2:3,-9,-9,0:3",
               "-9:MAX,-2:3,-9,-9:-6,0:3",
               "-9,-2:3,-8,-9,-1:3",
               "-9:MAX,-2:3,-8,-9:-5,-1:3",
               "-9,-2:3,-7,-9,-2:3",
               "-9:MAX,-2:3,-7,-9:-4,-2:3",
               "-9,-2:3,-6,-9,-2:3",
               "-9:MAX,-2:3,-6,-9:-3,-2:3",
               "0,0:3,-3,0,3:3",
               "0:MAX,0:3,-3,0:0,3:3",
               "0,0:3,-2,0,2:3",
               "0:MAX,0:3,-2,0:1,2:3",
               "0,0:3,-1,0,1:3",
               "0:MAX,0:3,-1,0:2,1:3",
               "0,0:3,0,0,0:3",
               "0:MAX,0:3,0,0:3,0:3",
               "0,0:3,1,0,0:3",
               "0:MAX,0:3,1,0:4,0:3",
               "0,1:3,-3,0,3:3",
               "0:MAX,1:3,-3,0:0,3:3",
               "0,1:3,-2,0,2:3",
               "0:MAX,1:3,-2,0:1,2:3",
               "0,1:3,-1,0,1:3",
               "0:MAX,1:3,-1,0:2,1:3",
               "0,1:3,0,0,1:3",
               "0:MAX,1:3,0,0:3,1:3",
               "0,-2:3,-3,0,3:3",
               "0:MAX,-2:3,-3,0:0,3:3",
               "0,-2:3,-2,0,2:3",
               "0:MAX,-2:3,-2,0:1,2:3",
               "0,-2:3,-1,0,1:3",
               "0:MAX,-2:3,-1,0:2,1:3",
               "0,-2:3,0,0,0:3",
               "0:MAX,-2:3,0,0:3,0:3",
               "0,-2:3,1,0,-1:3",
               "0:MAX,-2:3,1,0:4,-1:3",
               "0,-2:3,2,0,-2:3",
               "0:MAX,-2:3,2,0:5,-2:3",
               "0,-2:3,3,0,-2:3",
               "0:MAX,-2:3,3,0:6,-2:3",})
   public void testSetMax(String inputLeftRange, String inputRightRange, String max, String outputLeftRange, String outputRightRange) {
      assertSetMax(parseRange(inputLeftRange), parseRange(inputRightRange), Long.parseLong(max), parseRange(outputLeftRange), parseRange(outputRightRange));
   }

   private void assertSetMax(Range inputLeftRange, Range inputRightRange, long max, Range outputLeftRange, Range outputRightRange) {
      TestUtils environment = new TestUtils(inputLeftRange, inputRightRange);
      Variable left = environment.getLeft();
      Variable right = environment.getRight();
      ConstraintStore variables = environment.getConstraintStore();
      Subtract s = new Subtract(left, right);

      assertNotEquals(ExpressionResult.FAILED, s.setMax(variables, max));
      assertEquals(outputLeftRange.min(), left.getMin(variables));
      assertEquals(outputLeftRange.max(), left.getMax(variables));
      assertEquals(outputRightRange.min(), right.getMin(variables));
      assertEquals(outputRightRange.max(), right.getMax(variables));
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
   }

   private void assertSetMinSuccess(Range leftRange, Range rightRange) {
      TestUtils environment = new TestUtils(leftRange, rightRange);
      ConstraintStore variables = environment.getConstraintStore();
      Subtract s = new Subtract(environment.getLeft(), environment.getRight());

      long min = s.getMin(variables);
      long max = s.getMax(variables);
      assertEquals(min == max ? ExpressionResult.NO_CHANGE : ExpressionResult.UPDATED, s.setMin(variables, max));
      assertEquals(max, s.getMin(variables));
   }

   @UseDataProvider("data")
   @Test
   public void testSetMaxSuccess(String leftRange, String rightRange) {
      assertSetMaxSuccess(parseRange(leftRange), parseRange(rightRange));
   }

   private void assertSetMaxSuccess(Range leftRange, Range rightRange) {
      TestUtils environment = new TestUtils(leftRange, rightRange);
      ConstraintStore variables = environment.getConstraintStore();
      Subtract s = new Subtract(environment.getLeft(), environment.getRight());

      long min = s.getMin(variables);
      long max = s.getMax(variables);
      assertEquals(min == max ? ExpressionResult.NO_CHANGE : ExpressionResult.UPDATED, s.setMax(variables, min));
      assertEquals(min, s.getMax(variables));
   }

   @UseDataProvider("data")
   @Test
   public void testSetMinFailed(String leftRange, String rightRange) {
      assertSetMinFailed(parseRange(leftRange), parseRange(rightRange));
   }

   private void assertSetMinFailed(Range leftRange, Range rightRange) {
      for (int i = 1; i < 4; i++) {
         TestUtils environment = new TestUtils(leftRange, rightRange);
         Subtract s = new Subtract(environment.getLeft(), environment.getRight());

         long max = s.getMax(environment.getConstraintStore());
         long newMax = max + i;
         assertEquals(ExpressionResult.FAILED, s.setMin(environment.getConstraintStore(), newMax));
      }
   }

   @UseDataProvider("data")
   @Test
   public void testSetMaxFailed(String leftRange, String rightRange) {
      assertSetMaxFailed(parseRange(leftRange), parseRange(rightRange));
   }

   private void assertSetMaxFailed(Range leftRange, Range rightRange) {
      for (int i = 1; i < 4; i++) {
         TestUtils environment = new TestUtils(leftRange, rightRange);
         Subtract s = new Subtract(environment.getLeft(), environment.getRight());

         long min = s.getMin(environment.getConstraintStore());
         long newMin = min - i;
         assertEquals(ExpressionResult.FAILED, s.setMax(environment.getConstraintStore(), newMin));
      }
   }

   @Test
   @DataProvider({"MAX,MIN", "MAX,-1", "MAX-1,-2", "MAX-1,-3", "MAX-2,-3", "MAX-3,-4",})
   public void testSetMaxOverflow(String leftRange, String rightRange) {
      assertSetMaxOverflow(parseRange(leftRange), parseRange(rightRange));
   }

   private void assertSetMaxOverflow(Range leftRange, Range rightRange) {
      TestUtils environment = new TestUtils(leftRange, rightRange);
      Subtract s = new Subtract(environment.getLeft(), environment.getRight());

      assertEquals(ExpressionResult.FAILED, s.setMax(environment.getConstraintStore(), s.getMin(environment.getConstraintStore())));
   }

   @Test
   @DataProvider({"MIN,MAX", "MIN,1", "MIN+1,2", "MIN+1,3", "MIN+2,3", "MIN+3,4",})
   public void testSetMinOverflow(String leftRange, String rightRange) {
      assertSetMinOverflow(parseRange(leftRange), parseRange(rightRange));
   }

   private void assertSetMinOverflow(Range leftRange, Range rightRange) {
      TestUtils environment = new TestUtils(leftRange, rightRange);
      Subtract s = new Subtract(environment.getLeft(), environment.getRight());

      assertEquals(ExpressionResult.FAILED, s.setMin(environment.getConstraintStore(), s.getMax(environment.getConstraintStore())));
   }

   @Test
   public void testSetNotNoChange() {
      Range leftRange = parseRange("2:4");
      Range rightRange = parseRange("3:5");
      TestUtils environment = new TestUtils(leftRange, rightRange);
      Variable left = environment.getLeft();
      Variable right = environment.getRight();
      ConstraintStore variables = environment.getConstraintStore();
      Subtract s = new Subtract(environment.getLeft(), environment.getRight());
      for (long i = s.getMin(variables) - 1; i <= s.getMax(variables) + 1; i++) {
         assertEquals(ExpressionResult.NO_CHANGE, s.setNot(variables, i));
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
      Subtract s = new Subtract(environment.getLeft(), environment.getRight());
      assertEquals(ExpressionResult.UPDATED, s.setNot(variables, s.getMin(variables)));
      assertEquals(leftRange.min(), left.getMin(variables));
      assertEquals(leftRange.max(), left.getMax(variables));
      assertEquals(rightRange.min(), right.getMin(variables));
      assertEquals(rightRange.max() - 1, right.getMax(variables));
   }

   @Test
   public void testSetNotUpdatedMax() {
      Range leftRange = parseRange("3");
      Range rightRange = parseRange("3:5");
      TestUtils environment = new TestUtils(leftRange, rightRange);
      Variable left = environment.getLeft();
      Variable right = environment.getRight();
      ConstraintStore variables = environment.getConstraintStore();
      Subtract s = new Subtract(environment.getLeft(), environment.getRight());
      assertEquals(ExpressionResult.UPDATED, s.setNot(variables, s.getMax(variables)));
      assertEquals(leftRange.min(), left.getMin(variables));
      assertEquals(leftRange.max(), left.getMax(variables));
      assertEquals(rightRange.min() + 1, right.getMin(variables));
      assertEquals(rightRange.max(), right.getMax(variables));
   }

   @Test
   public void testSetNotFailed() {
      TestUtils environment = new TestUtils(parseRange("3"), parseRange("5"));
      ConstraintStore variables = environment.getConstraintStore();
      Subtract s = new Subtract(environment.getLeft(), environment.getRight());
      assertEquals(ExpressionResult.FAILED, s.setNot(variables, s.getMax(variables)));
   }
}
