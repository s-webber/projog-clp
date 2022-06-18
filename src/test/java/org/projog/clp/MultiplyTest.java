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

@RunWith(DataProviderRunner.class)
public class MultiplyTest extends AbstractExpressionTest {
   public MultiplyTest() {
      super(Multiply::new);
   }

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
      TestUtils environment = new TestUtils(leftRange, rightRange);
      Multiply m = new Multiply(environment.getLeft(), environment.getRight());

      assertEquals(expectedRange.min(), m.getMin(environment.getConstraintStore()));
      assertEquals(expectedRange.max(), m.getMax(environment.getConstraintStore()));
   }

   @Test
   @DataProvider({
               // when min is zero
               "-1:0,1:2,0,0,1:2",
               "0:1,1:2,0,0:1,1:2",
               "-1:1,1:2,0,0:1,1:2",
               // when both args are +ve
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
               "1:7,2:3,21,7,3",
               // when both args are -ve
               "-1,-2,2,-1,-2",
               "-7,-3,20,-7,-3",
               "-7,-3,21,-7,-3",
               "-7:-1,-3:-2,14,-7:-5,-3:-2",
               "-7:-1,-3:-2,15,-7:-5,-3",
               "-7:-1,-3:-2,16,-7:-6,-3",
               "-7:-1,-3:-2,17,-7:-6,-3",
               "-7:-1,-3:-2,18,-7:-6,-3",
               "-7:-1,-3:-2,19,-7,-3",
               "-7:-1,-3:-2,20,-7,-3",
               "-7:-1,-3:-2,21,-7,-3",})
   public void testSetMin(String inputLeftRange, String inputRightRange, String min, String outputLeftRange, String outputRightRange) {
      assertSetMin(parseRange(inputLeftRange), parseRange(inputRightRange), Long.parseLong(min), parseRange(outputLeftRange), parseRange(outputRightRange));
      assertSetMin(parseRange(inputRightRange), parseRange(inputLeftRange), Long.parseLong(min), parseRange(outputRightRange), parseRange(outputLeftRange));
   }

   private void assertSetMin(Range inputLeftRange, Range inputRightRange, long min, Range outputLeftRange, Range outputRightRange) {
      TestUtils environment = new TestUtils(inputLeftRange, inputRightRange);
      Variable left = environment.getLeft();
      Variable right = environment.getRight();
      Multiply m = new Multiply(left, right);

      assertNotEquals(ExpressionResult.FAILED, m.setMin(environment.getConstraintStore(), min));
      assertEquals(outputLeftRange.min(), left.getMin(environment.getConstraintStore()));
      assertEquals(outputLeftRange.max(), left.getMax(environment.getConstraintStore()));
      assertEquals(outputRightRange.min(), right.getMin(environment.getConstraintStore()));
      assertEquals(outputRightRange.max(), right.getMax(environment.getConstraintStore()));
   }

   @Test
   @DataProvider({"7,3,22", "7,3,23", "1:7,2:3,22", "-1,2,0", "-1,2,1"})
   public void testSetMinFailed(String inputLeftRange, String inputRightRange, String min) {
      assertSetMinFailed(parseRange(inputLeftRange), parseRange(inputRightRange), Long.parseLong(min));
      assertSetMinFailed(parseRange(inputRightRange), parseRange(inputLeftRange), Long.parseLong(min));
   }

   private void assertSetMinFailed(Range inputLeftRange, Range inputRightRange, long min) {
      TestUtils environment = new TestUtils(inputLeftRange, inputRightRange);
      Variable left = environment.getLeft();
      Variable right = environment.getRight();
      Multiply m = new Multiply(left, right);

      assertEquals(ExpressionResult.FAILED, m.setMin(environment.getConstraintStore(), min));
   }

   @Test
   @DataProvider({
               // when max is zero
               "-1:0,-2:-1,0,0,-2:-1",
               "-1:0,-2:1,0,-1:0,-2:1",
               "-1:1,1:2,0,-1:0,1:2",
               //+ve/+ve
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
               "4:7,6:15,105,4:7,6:15",
               // -ve/-ve
               "-1,-2,2,-1,-2",
               "-4,-6,24,-4,-6",
               "-4,-6,25,-4,-6",
               "-7,-15,105,-7,-15",
               "-7:-4,-15:-6,24,-4,-6",
               "-7:-4,-15:-6,25,-4,-6",
               "-7:-4,-15:-6,26,-4,-6",
               "-7:-4,-15:-6,27,-4,-6",
               "-7:-4,-15:-6,28,-4,-7:-6",
               "-7:-4,-15:-6,29,-4,-7:-6",
               "-7:-4,-15:-6,30,-5:-4,-7:-6",
               "-7:-4,-15:-6,31,-5:-4,-7:-6",
               "-7:-4,-15:-6,32,-5:-4,-8:-6",
               "-7:-4,-15:-6,105,-7:-4,-15:-6",})
   public void testSetMax(String inputLeftRange, String inputRightRange, String min, String outputLeftRange, String outputRightRange) {
      assertSetMax(parseRange(inputLeftRange), parseRange(inputRightRange), Long.parseLong(min), parseRange(outputLeftRange), parseRange(outputRightRange));
      assertSetMax(parseRange(inputRightRange), parseRange(inputLeftRange), Long.parseLong(min), parseRange(outputRightRange), parseRange(outputLeftRange));
   }

   private void assertSetMax(Range inputLeftRange, Range inputRightRange, long min, Range outputLeftRange, Range outputRightRange) {
      TestUtils environment = new TestUtils(inputLeftRange, inputRightRange);
      Variable left = environment.getLeft();
      Variable right = environment.getRight();
      Multiply m = new Multiply(left, right);

      assertNotEquals(ExpressionResult.FAILED, m.setMax(environment.getConstraintStore(), min));
      assertEquals(outputLeftRange.min(), left.getMin(environment.getConstraintStore()));
      assertEquals(outputLeftRange.max(), left.getMax(environment.getConstraintStore()));
      assertEquals(outputRightRange.min(), right.getMin(environment.getConstraintStore()));
      assertEquals(outputRightRange.max(), right.getMax(environment.getConstraintStore()));
   }

   @Test
   @DataProvider({"4,6,22", "4,6,23", "4:7,6:15,23", "1,2,0", "-1,-2,0", "1,2,-1", "-1,-2,-1"})
   public void testSetMaxFailed(String inputLeftRange, String inputRightRange, String min) {
      assertSetMaxFailed(parseRange(inputLeftRange), parseRange(inputRightRange), Long.parseLong(min));
      assertSetMaxFailed(parseRange(inputRightRange), parseRange(inputLeftRange), Long.parseLong(min));
   }

   private void assertSetMaxFailed(Range inputLeftRange, Range inputRightRange, long min) {
      TestUtils environment = new TestUtils(inputLeftRange, inputRightRange);
      Variable left = environment.getLeft();
      Variable right = environment.getRight();
      Multiply m = new Multiply(left, right);

      assertEquals(ExpressionResult.FAILED, m.setMax(environment.getConstraintStore(), min));
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
         assertEquals(ExpressionResult.NO_CHANGE, m.setNot(variables, i));
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
      assertEquals(ExpressionResult.UPDATED, m.setNot(variables, m.getMin(variables)));
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
      assertEquals(ExpressionResult.UPDATED, m.setNot(variables, m.getMax(variables)));
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
      assertEquals(ExpressionResult.FAILED, m.setNot(variables, m.getMax(variables)));
   }
}
