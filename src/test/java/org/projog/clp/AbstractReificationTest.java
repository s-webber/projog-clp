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

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import org.junit.Test;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

abstract class AbstractReificationTest extends AbstractConstraintTest {
   private Map<String, Builder> tests = new HashMap<>();

   AbstractReificationTest(BiFunction<Constraint, Constraint, Constraint> factory) {
      super((e1, e2) -> factory.apply((Constraint) e1, (Constraint) e2));
   }

   @DataProvider
   public static Object[] data() {
      return new Object[] {"1,1", "0,0", "1,0", "0,1", "1,0:1", "0:1,1", "0,0:1", "0:1,0", "0:1,0:1"};
   }

   @UseDataProvider("data")
   @Test
   public final void testEnforce(String key) {
      Builder b = getBuilder(key);

      TestUtils.When when = TestUtils.given(b.inputLeft, b.inputRight).when(enforce);
      if (b.enforceResult == ConstraintResult.FAILED) {
         when.then(b.enforceResult);
      } else {
         when.then(b.enforceResult, b.enforceLeftValue, b.enforceRightValue);
      }
   }

   @UseDataProvider("data")
   @Test
   public final void testPrevent(String key) {
      Builder b = getBuilder(key);

      TestUtils.When when = TestUtils.given(b.inputLeft, b.inputRight).when(prevent);
      if (b.preventResult == ConstraintResult.FAILED) {
         when.then(b.preventResult);
      } else {
         when.then(b.preventResult, b.preventLeftValue, b.preventRightValue);
      }
   }

   @UseDataProvider("data")
   @Test
   public final void testReify(String key) {
      Builder b = getBuilder(key);

      TestUtils.given(b.inputLeft, b.inputRight).when(reify).then(b.reifyResult);
   }

   private Builder getBuilder(String key) {
      Builder b = tests.get(key);
      if (b == null) {
         throw new IllegalStateException(key);
      }
      return b;
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

   static class Builder {
      private final String inputLeft;
      private final String inputRight;
      private ConstraintResult enforceResult;
      private String enforceLeftValue;
      private String enforceRightValue;
      private ConstraintResult preventResult;
      private String preventLeftValue;
      private String preventRightValue;
      private ConstraintResult reifyResult;

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
         this.enforceResult = enforceResult;
         this.enforceLeftValue = enforceLeftValue;
         this.enforceRightValue = enforceRightValue;
         return this;
      }

      Builder prevent(ConstraintResult preventResult) {
         return prevent(preventResult, inputLeft, inputRight);
      }

      Builder prevent(ConstraintResult preventResult, String preventValue) {
         return prevent(preventResult, preventValue, preventValue);
      }

      Builder prevent(ConstraintResult preventResult, String preventLeftValue, String preventRightValue) {
         this.preventResult = preventResult;
         this.preventLeftValue = preventLeftValue;
         this.preventRightValue = preventRightValue;
         return this;
      }

      Builder reify(ConstraintResult reifyResult) {
         this.reifyResult = reifyResult;
         return this;
      }
   }
}
