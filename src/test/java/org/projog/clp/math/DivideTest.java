/*
 * Copyright 2023 S. Webber
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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import org.projog.clp.BruteForceSearch;
import org.projog.clp.ClpConstraintStore;
import org.projog.clp.Constraint;
import org.projog.clp.FixedValue;
import org.projog.clp.Variable;
import org.projog.clp.compare.EqualTo;
import org.projog.clp.compare.LessThanOrEqualTo;
import org.testng.annotations.Test;

public class DivideTest extends AbstractTwoArgsExpressionTest {
   public DivideTest() {
      super(Divide::new, false);

      // test with minimum and maximum values
      divide("MAX", "MIN").returns("0");
      divide("MIN", "MAX").returns("-1");
      divide("MAX", "MAX").returns("1");
      divide("MIN", "MIN").returns("1");
      divide("1", "MIN").returns("0");
      divide("1", "MAX").returns("0");
      divide("-1", "MIN").returns("0");
      divide("-1", "MAX").returns("0");
      divide("MIN", "1").returns("MIN");
      divide("MAX", "1").returns("MAX");
      divide("MIN", "-1").returns("MAX");
      divide("MAX", "-1").returns("MIN+1");
      divide("MIN:MAX", "MIN:MAX").returns("MIN:MAX");
      divide("29", "MIN:MAX").returns("-29:29");
      divide("29", "-10000:10000").returns("-29:29");

      // dividend == 0
      divide("0", "MIN:MAX").returns("0");
      divide("0", "1").returns("0");
      divide("0", "-1").returns("0");

      // divisor == 0
      divide("0", "0").returns("MIN:MAX");
      divide("42", "0").returns("MIN:MAX");
      divide("-42", "0").returns("MIN:MAX");

      divide("35", "1:50").returns("0:35");
      divide("-35", "-50:-1").returns("0:35");
      divide("-35", "1:50").returns("-35:0");
      divide("35", "-50:-1").returns("-35:0");

      divide("-5", "-5").returns("1");
      divide("-5", "-5:-2").returns("1:2");
      divide("-5:-1", "2:3").returns("-2:0");
      divide("-5:-1", "1:2").returns("-5:0");
      divide("-5:-4", "1:5").returns("-5:0");
      divide("2:3", "-2:3").returns("-3:3");
      divide("1:2", "-1:2").returns("-2:2");
      divide("1", "-1:2").returns("-1:1");
      divide("1:2", "-1:3").returns("-2:2");
      divide("-5:-4", "1").returns("-5:-4");
      divide("-5:-4", "3:5").returns("-1:0");
      divide("2:3", "-5:3").returns("-3:3");
      divide("-5:5", "-5:2").returns("-5:5");
      divide("-5:4", "-5:2").returns("-5:5");
      divide("-5:2", "-5:1").returns("-5:5");
      divide("1", "-5:2").returns("-1:1");
      divide("1:2", "-5:3").returns("-2:2");
      divide("-5:5", "-5:3").returns("-5:5");
      divide("-5:2", "-5:2").returns("-5:5");
      divide("-5", "-5:1").returns("-5:5");
      divide("-5:5", "-1:2").returns("-5:5");
      divide("1:4", "-2:-1").returns("-4:0");
      divide("-5:2", "-1:3").returns("-5:5");
      divide("-4:5", "-4:5").returns("-5:5");
      divide("-2:4", "-2:2").returns("-4:4");
      divide("1:2", "-5:-1").returns("-2:0");
      divide("-5", "-5:2").returns("-5:5");
      divide("-4:5", "-5:2").returns("-5:5");
      divide("-4:5", "-5:1").returns("-5:5");
      divide("-5:-4", "-5").returns("0:1");
      divide("-5:1", "-5:-1").returns("-1:5");
      divide("-4:5", "-5:5").returns("-5:5");
      divide("2:3", "-3:1").returns("-3:3");
      divide("-1:2", "-2:2").returns("-2:2");
      divide("-4:1", "-5:-1").returns("-1:4");
      divide("-4", "-5:5").returns("-4:4");
      divide("-3:4", "-5:4").returns("-4:4");
      divide("1", "-5:1").returns("-1:1");
      divide("1:2", "-5:1").returns("-2:2");
      divide("0:1", "-5:1").returns("-1:1");

      given("27", "MIN:MAX").setMin(9).then("27", "1:MAX");
      given("27", "MIN:MAX").setMax(9).unchanged();
      given("27", "MIN:MAX").setMin(-9).unchanged();
      given("27", "MIN:MAX").setMax(-9).then("27", "MIN:-1");

      given("27", "1:MAX").setMin(9).then("27", "1:3");
      given("27", "1:MAX").setMax(9).then("27", "3:MAX");
      given("27", "1:MAX").setMin(-9).unchanged();
      given("27", "1:MAX").setMax(-9).failed();

      given("-27", "MIN:MAX").setMin(9).then("-27", "MIN:-1");
      given("-27", "MIN:MAX").setMax(9).unchanged();
      given("-27", "MIN:MAX").setMin(-9).unchanged();
      given("-27", "MIN:MAX").setMax(-9).then("-27", "1:MAX");

      given("-27", "1:MAX").setMin(9).failed();
      given("-27", "1:MAX").setMax(9).unchanged();
      given("-27", "1:MAX").setMin(-9).then("-27", "2:MAX");
      given("-27", "1:MAX").setMax(-9).then("-27", "1:3");

      given("35", "1:50").setMin(9).then("35", "1:3");
      given("35", "1:50").setMax(9).then("35", "4:50");

      given("-35", "-50:-1").setMin(9).then("-35", "-3:-1");
      given("-35", "-50:-1").setMax(9).then("-35", "-50:-4");

      given("-35", "1:50").setMin(-9).then("-35", "2:50");
      given("-35", "1:50").setMax(-9).then("-35", "1:3");

      given("35", "-50:-1").setMin(-9).then("35", "-50:-4");
      given("35", "-50:-1").setMax(-9).then("35", "-3:-1");

      given("1:2", "1").setMin(2).then("2", "1");
      given("1", "1:2").setMin(1).then("1", "1");
      given("-10", "-10:-5").setMin(2).then("-10", "-5");
      given("-10:-9", "-10").setMin(1).then("-10", "-10");
      given("-10:-9", "1").setMin(1).failed();
      given("-10:-9", "1").setMin(-9).then("-9", "1");
      given("-10", "1:2").setMin(-9).then("-10", "2");
      given("1", "-10:-9").setMin(1).failed();
      given("2", "-10:-1").setMin(-1).then("2", "-10:-2");
      given("1:2", "-1").setMin(-1).then("1", "-1");

      given("9:10", "1").setMax(-1).failed();
      given("1:2", "1").setMax(1).then("1", "1");
      given("2", "1:2").setMax(1).then("2", "2");
      given("-10:-9", "-1").setMax(-1).failed();
      given("-10:-9", "-5").setMax(1).then("-9", "-5");
      given("-10", "-10:-5").setMax(1).then("-10", "-10:-6");
      given("-10", "1:2").setMax(-10).then("-10", "1");
      given("-10:-9", "1").setMax(-10).then("-10", "1");
      given("1:2", "-10:-2").setMax(-1).then("2", "-2");
      given("1", "-10:-1").setMax(-1).then("1", "-1");

      // set max to zero
      given("1:2", "1:2").setMax(0).then("1", "2");
      given("-2:-1", "-2:-1").setMax(0).then("-1", "-2");
      given("1:2", "-2:-1").setMax(0).unchanged();
      given("-2:1", "1:2").setMax(0).unchanged();

      // set min to zero
      given("1:2", "1:2").setMin(0).unchanged();
      given("-2:-1", "-2:-1").setMin(0).unchanged();
      given("1:2", "-2:-1").setMin(0).then("1", "-2");
      given("-2:-1", "1:2").setMin(0).then("-1", "2");

      given("-7:0", "1:2").setMin(0).unchanged();
      given("-7:0", "-2:-1").setMin(0).unchanged();
      given("1:2", "-7:7").setMin(0).unchanged();
      given("-2:-1", "-7:7").setMin(0).unchanged();
      given("1:2", "-7:7").setMin(1).then("1:2", "1:7");
      given("-2:-1", "-7:7").setMin(1).then("-2:-1", "-7:-1");
      given("-3:2", "1:7").setMin(1).then("1:2", "1:7");
      given("-3:2", "-7:-1").setMin(1).then("-3:-1", "-7:-1");

      given("-7:0", "-12:-2").setMax(0).unchanged();
      given("0:7", "-12:-2").setMax(0).unchanged();
      given("-2:-1", "-7:7").setMax(0).unchanged();
      given("1:2", "-7:7").setMax(0).unchanged();
      given("-2:-1", "-7:7").setMax(-1).then("-2:-1", "1:7");
      given("1:2", "-7:7").setMax(-1).then("1:2", "-7:-1");
      given("-6:8", "-7:-1").setMax(-1).then("1:8", "-7:-1");
      given("-6:8", "1:7").setMax(-1).then("-6:-1", "1:7");

      // setNot
      given("27", "9").setNot(2).unchanged();
      given("27", "9").setNot(3).failed();
      given("27", "9").setNot(4).unchanged();
   }

   private GetterTest divide(String left, String right) {
      return expression(left, right);
   }

   @Test
   public void testBruteForce() {
      long MIN = -7;
      long MAX = 7;
      for (long minLeft = MIN; minLeft <= MAX; minLeft++) {
         for (long maxLeft = minLeft; maxLeft <= MAX; maxLeft++) {
            for (long minRight = MIN; minRight <= MAX; minRight++) {
               for (long maxRight = minRight; maxRight <= MAX; maxRight++) {
                  for (long min = MIN - 1; min <= MAX + 1; min++) {
                     assertSetters(minLeft, maxLeft, minRight, maxRight, min);
                  }
               }
            }
         }
      }
   }

   private static void assertSetters(long minLeft, long maxLeft, long minRight, long maxRight, long target) {
      if (minRight == 0 || maxRight == 0) {
         return;
      }

      assertSetter(minLeft, maxLeft, minRight, maxRight, target, Method.GREATER_THAN_OR_EQUAL);
      assertSetter(minLeft, maxLeft, minRight, maxRight, target, Method.LESS_THAN_OR_EQUAL);
      assertSetter(minLeft, maxLeft, minRight, maxRight, target, Method.EQUAL);
   }

   private static void assertSetter(final long minLeft, final long maxLeft, final long minRight, final long maxRight, final long target, final Method methodToTest) {
      // perform exhaustive search to find maximum and minimum values for both the left (i.e. dividend) and right (i.e. divisor) arguments
      // that can satisfy the "greater than or equal" or "less than or equal" constraint for the given target (i.e. quotient)
      long actualMaxLeft = Long.MIN_VALUE;
      long actualMinLeft = Long.MAX_VALUE;
      long actualMaxRight = Long.MIN_VALUE;
      long actualMinRight = Long.MAX_VALUE;
      for (long left = minLeft; left <= maxLeft; left++) {
         for (long right = minRight; right <= maxRight; right++) {
            if (right != 0 && methodToTest.isMatch(left / right, target)) {
               actualMaxLeft = Math.max(actualMaxLeft, left);
               actualMinLeft = Math.min(actualMinLeft, left);
               actualMaxRight = Math.max(actualMaxRight, right);
               actualMinRight = Math.min(actualMinRight, right);
            }
         }
      }

      // build a ClpConstraintStore using the given min/max and values
      ClpConstraintStore.Builder builder = new ClpConstraintStore.Builder();
      Variable x = builder.createVariable();
      Variable y = builder.createVariable();
      builder.enforce(x).between(minLeft, maxLeft);
      builder.enforce(y).between(minRight, maxRight);
      builder.addConstraint(methodToTest.createConstraint(new Divide(x, y), new FixedValue(target)));
      ClpConstraintStore s = builder.build();

      if (Long.MIN_VALUE == actualMaxLeft) {
         // if exhaustive search could not find a solution then confirm BruteForceSearch throws an exception
         try {
            new BruteForceSearch(s).next();
            throw new Error();
         } catch (Exception e) {
            // expected
            assertEquals("Variables not sufficiently bound. Too many possibilities.", e.getMessage());
         }
      } else if (s.resolve()) {
         // if resolved using ClpConstraintStore then confirm resulting min/max values match result of exhaustive search
         if (x.getMin(s) > actualMinLeft || x.getMax(s) < actualMaxLeft || y.getMin(s) > actualMinRight || y.getMax(s) < actualMaxRight) {
            fail("Result of resolving using ClpConstraintStore is too restrictive given input: "
                 + minLeft
                 + ":"
                 + maxLeft
                 + " "
                 + minRight
                 + ":"
                 + maxRight
                 + " "
                 + target
                 + " expected: "
                 + actualMinLeft
                 + ":"
                 + actualMaxLeft
                 + " "
                 + actualMinRight
                 + ":"
                 + actualMaxRight
                 + " but got: "
                 + x.getMin(s)
                 + ":"
                 + x.getMax(s)
                 + " "
                 + y.getMin(s)
                 + ":"
                 + y.getMax(s)
                 + " "
                 + methodToTest);
         }
         if (minLeft != 0 && // not yet optimising results where minimum value for first argument is zero
             (minLeft < 0 == maxLeft < 0) && // not yet optimising results where possible values include both positive and negative
             (minRight < 0 == maxRight < 0) && // not yet optimising results where possible values include both positive and negative
             (x.getMin(s) != actualMinLeft || x.getMax(s) != actualMaxLeft || y.getMin(s) != actualMinRight || y.getMax(s) != actualMaxRight)) {
            fail("Result of resolving using ClpConstraintStore is not as restrictive as it could be given input: "
                 + minLeft
                 + ":"
                 + maxLeft
                 + " "
                 + minRight
                 + ":"
                 + maxRight
                 + " "
                 + target
                 + " expected: "
                 + actualMinLeft
                 + ":"
                 + actualMaxLeft
                 + " "
                 + actualMinRight
                 + ":"
                 + actualMaxRight
                 + " got: "
                 + x.getMin(s)
                 + ":"
                 + x.getMax(s)
                 + " "
                 + y.getMin(s)
                 + ":"
                 + y.getMax(s)
                 + " "
                 + methodToTest);
         }
      } else {
         // if cannot resolve using ClpConstraintStore but exhaustive search proved it can be then throw an exception
         fail("could not resolve using ClpConstraintStore given "
              + minLeft
              + ":"
              + maxLeft
              + " "
              + minRight
              + ":"
              + maxRight
              + " "
              + target
              + " expected: "
              + actualMinLeft
              + ":"
              + actualMinLeft
              + " "
              + actualMinRight
              + ":"
              + actualMaxRight
              + " "
              + methodToTest);
      }
   }

   enum Method {
      LESS_THAN_OR_EQUAL {
         @Override
         boolean isMatch(long result, long target) {
            return result <= target;
         }

         @Override
         Constraint createConstraint(Divide division, FixedValue target) {
            return new LessThanOrEqualTo(division, target);
         }
      },
      GREATER_THAN_OR_EQUAL {
         @Override
         boolean isMatch(long result, long target) {
            return result >= target;
         }

         @Override
         Constraint createConstraint(Divide division, FixedValue target) {
            return new LessThanOrEqualTo(target, division);
         }
      },
      EQUAL {
         @Override
         boolean isMatch(long result, long target) {
            return result == target;
         }

         @Override
         Constraint createConstraint(Divide division, FixedValue target) {
            return new EqualTo(target, division);
         }
      };

      abstract boolean isMatch(long result, long target);

      abstract Constraint createConstraint(Divide division, FixedValue target);
   }
}
