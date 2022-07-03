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
package org.projog.clp.test;

import static org.projog.clp.test.RangeParser.parseRange;
import static org.testng.Assert.assertEquals;

import java.util.Objects;

import org.projog.clp.ClpConstraintStore;
import org.projog.clp.ConstraintStore;
import org.projog.clp.Variable;

public final class TestUtils {
   public static TestUtils given(String left, String right) {
      return new TestUtils(parseRange(left), parseRange(right));
   }

   public static TestUtils given(Range left, Range right) {
      return new TestUtils(left, right);
   }

   private final Variable left;
   private final Variable right;
   private final ConstraintStore v;

   private TestUtils(Range leftRange, Range rightRange) {
      ClpConstraintStore.Builder b = new ClpConstraintStore.Builder();
      this.left = b.createVariable();
      this.right = b.createVariable();
      this.v = b.build();
      left.setMin(v, leftRange.min());
      left.setMax(v, leftRange.max());
      right.setMin(v, rightRange.min());
      right.setMax(v, rightRange.max());
   }

   public Variable getLeft() {
      return left;
   }

   public Variable getRight() {
      return right;
   }

   public ConstraintStore getConstraintStore() {
      return v;
   }

   public When when(Action a) {
      Object result = a.action(v, left, right);
      return new When(result);
   }

   public class When {
      private final Object result;

      private When(Object result) {
         this.result = Objects.requireNonNull(result);
      }

      public void then(Object expectedResult) {
         assertEquals(expectedResult, this.result);
      }

      public void then(Object expectedResult, String expected) {
         then(expectedResult, expected, expected);
      }

      public void then(Object expectedResult, String expectedLeft, String expectedRight) {
         then(expectedResult);
         assertRange(left, parseRange(expectedLeft));
         assertRange(right, parseRange(expectedRight));
      }

      public void then(Object expectedResult, Range expectedLeft, Range expectedRight) {
         then(expectedResult);
         assertRange(left, expectedLeft);
         assertRange(right, expectedRight);
      }

      private void assertRange(Variable variable, Range range) {
         assertEquals(range.min(), variable.getMin(v));
         assertEquals(range.max(), variable.getMax(v));
      }

      @Deprecated
      @Override
      public boolean equals(Object o) {
         throw new UnsupportedOperationException();
      }
   }

   @FunctionalInterface
   public interface Action {
      Object action(ConstraintStore v, Variable x, Variable y);
   }
}
