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
package org.projog.clp.bool;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import org.projog.clp.AbstractConstraintTest;
import org.projog.clp.Constraint;
import org.projog.clp.ConstraintResult;
import org.projog.clp.LeafExpression;
import org.testng.annotations.Test;

abstract class AbstractReificationTest extends AbstractConstraintTest {
   private final Map<String, Builder> tests = new HashMap<>();

   AbstractReificationTest(BiFunction<LeafExpression, LeafExpression, Constraint> factory) {
      super(factory, false);
   }

   @Test
   public final void testReificationConfiguration() {
      String[] testCases = new String[] {"1,1", "0,0", "1,0", "0,1", "1,0:1", "0:1,1", "0,0:1", "0:1,0", "0:1,0:1"};
      assertEquals(testCases.length, tests.size());
      for (String testCase : testCases) {
         assertTrue(tests.containsKey(testCase));
      }
   }

   Builder given(String inputLeft, String inputRight) {
      String key = inputLeft + "," + inputRight;
      if (tests.containsKey(key)) {
         throw new IllegalStateException(key);
      }

      Builder b = new Builder(inputLeft, inputRight);
      tests.put(key, b);
      return b;
   }

   class Builder {
      private final String inputLeft;
      private final String inputRight;

      private Builder(String inputLeft, String inputRight) {
         this.inputLeft = inputLeft;
         this.inputRight = inputRight;
      }

      Builder enforce(ConstraintResult enforceResult) {
         return enforce(enforceResult, inputLeft, inputRight);
      }

      Builder enforce(ConstraintResult enforceResult, String enforceValue) {
         return enforce(enforceResult, enforceValue, enforceValue);
      }

      Builder enforce(ConstraintResult enforceResult, String enforceLeftValue, String enforceRightValue) {
         AbstractReificationTest.this.enforce(inputLeft, inputRight).then(enforceResult, enforceLeftValue, enforceRightValue);
         return this;
      }

      Builder prevent(ConstraintResult preventResult) {
         return prevent(preventResult, inputLeft, inputRight);
      }

      Builder prevent(ConstraintResult preventResult, String preventValue) {
         return prevent(preventResult, preventValue, preventValue);
      }

      Builder prevent(ConstraintResult preventResult, String preventLeftValue, String preventRightValue) {
         AbstractReificationTest.this.prevent(inputLeft, inputRight).then(preventResult, preventLeftValue, preventRightValue);
         return this;
      }

      Builder reify(ConstraintResult reifyResult) {
         AbstractReificationTest.this.reify(inputLeft, inputRight).then(reifyResult, inputLeft, inputRight);
         return this;
      }
   }
}
