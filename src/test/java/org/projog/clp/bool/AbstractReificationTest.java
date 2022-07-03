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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertSame;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import org.projog.clp.Constraint;
import org.projog.clp.ConstraintResult;
import org.projog.clp.Expression;
import org.projog.clp.FixedValue;
import org.projog.clp.Variable;
import org.projog.clp.test.TestUtils;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

abstract class AbstractReificationTest {
   private final Map<String, Builder> tests = new HashMap<>();
   private final BiFunction<Constraint, Constraint, Constraint> factory;
   final org.projog.clp.test.TestUtils.Action enforce;
   final org.projog.clp.test.TestUtils.Action prevent;
   final org.projog.clp.test.TestUtils.Action reify;
   private boolean running;

   AbstractReificationTest(BiFunction<Constraint, Constraint, Constraint> factory) {
      this.factory = factory;
      this.enforce = (v, x, y) -> factory.apply(x, y).enforce(v);
      this.prevent = (v, x, y) -> factory.apply(x, y).prevent(v);
      this.reify = (v, x, y) -> factory.apply(x, y).reify(v);
   }

   @BeforeTest
   public void before() {
      this.running = true;
   }

   @DataProvider
   public Object[] data() {
      return new Object[] {"1,1", "0,0", "1,0", "0,1", "1,0:1", "0:1,1", "0,0:1", "0:1,0", "0:1,0:1"};
   }

   @Test
   public final void testConfiguration() {
      assertEquals(data().length, tests.size());
   }

   @Test(dataProvider = "data")
   public final void testEnforce(String key) {
      Builder b = getBuilder(key);

      TestUtils.When when = TestUtils.given(b.inputLeft, b.inputRight).when(enforce);
      if (b.enforceResult == ConstraintResult.FAILED) {
         when.then(b.enforceResult);
      } else {
         when.then(b.enforceResult, b.enforceLeftValue, b.enforceRightValue);
      }
   }

   @Test(dataProvider = "data")
   public final void testPrevent(String key) {
      Builder b = getBuilder(key);

      TestUtils.When when = TestUtils.given(b.inputLeft, b.inputRight).when(prevent);
      if (b.preventResult == ConstraintResult.FAILED) {
         when.then(b.preventResult);
      } else {
         when.then(b.preventResult, b.preventLeftValue, b.preventRightValue);
      }
   }

   @Test(dataProvider = "data")
   public final void testReify(String key) {
      Builder b = getBuilder(key);

      TestUtils.given(b.inputLeft, b.inputRight).when(reify).then(b.reifyResult);
   }

   @Test
   public final void testWalk() {
      // given
      @SuppressWarnings("unchecked")
      Consumer<Expression> consumer = mock(Consumer.class);
      Constraint left = mock(Constraint.class);
      Constraint right = mock(Constraint.class);
      Constraint testObject = factory.apply(left, right);

      // when
      testObject.walk(consumer);

      // then
      verify(left).walk(consumer);
      verify(right).walk(consumer);
      verifyNoMoreInteractions(consumer, left, right);
   }

   @Test
   public final void testReplaceVariables() {
      // given
      @SuppressWarnings("unchecked")
      Function<Variable, Variable> function = mock(Function.class);
      Constraint left = mock(Constraint.class);
      Constraint right = mock(Constraint.class);
      Constraint testObject = factory.apply(left, right);
      when(left.replaceVariables(function)).thenReturn(new FixedValue(42));
      when(right.replaceVariables(function)).thenReturn(new FixedValue(180));

      // when
      Constraint replacement = testObject.replaceVariables(function);
      assertSame(testObject.getClass(), replacement.getClass());
      assertNotSame(testObject, replacement);
      String name = testObject.getClass().getName();
      assertEquals(name.substring(name.lastIndexOf('.') + 1) + " [left=FixedValue [value=42], right=FixedValue [value=180]]", replacement.toString());

      // then
      verify(left).replaceVariables(function);
      verify(right).replaceVariables(function);
      verifyNoMoreInteractions(function, left, right);
   }

   private Builder getBuilder(String key) {
      Builder b = tests.get(key);
      if (b == null) {
         throw new IllegalStateException(key);
      }
      return b;
   }

   Builder given(String inputLeft, String inputRight) {
      assertFalse(running);

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
