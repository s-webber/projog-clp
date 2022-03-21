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
import static org.projog.clp.TestDataParser.parseRange;

final class Bdd {
   static Bdd given(String left, String right) {
      return new Bdd(parseRange(left), parseRange(right));
   }

   private final Variable left;
   private final Variable right;
   private final Variables v;

   Bdd(Range leftRange, Range rightRange) {
      ClpEnvironment.Builder b = new ClpEnvironment.Builder();
      this.left = b.createVariable("x");
      this.right = b.createVariable("y");
      this.v = b.build();
      left.setMin(v, leftRange.min());
      left.setMax(v, leftRange.max());
      right.setMin(v, rightRange.min());
      right.setMax(v, rightRange.max());
   }

   Variable getLeft() {
      return left;
   }

   Variable getRight() {
      return right;
   }

   Variables getVariables() {
      return v;
   }

   When when(Action a) {
      Object result = a.action(v, left, right);
      return new When(result);
   }

   @FunctionalInterface
   interface Action {
      Object action(Variables v, Variable x, Variable y);
   }

   class When {
      private final Object result;

      When(Object result) {
         this.result = result;
      }

      void then(Object expectedResult) {
         assertEquals(expectedResult, this.result);
      }

      void then(Object expectedResult, String expected) {
         then(expectedResult, expected, expected);
      }

      void then(Object expectedResult, String expectedLeft, String expectedRight) {
         then(expectedResult);
         assertRange(left, parseRange(expectedLeft));
         assertRange(right, parseRange(expectedRight));
      }

      private void assertRange(Variable variable, Range range) {
         assertEquals(range.min(), variable.getMin(v));
         assertEquals(range.max(), variable.getMax(v));
      }
   }
}
