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
import static org.junit.Assert.assertNotSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.projog.clp.TestUtils.given;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;

@RunWith(DataProviderRunner.class)
public class EquivalentTest {
   private final BiFunction<Constraint, Constraint, Equivalent> factory;
   protected final TestUtils.Action enforce;
   protected final TestUtils.Action prevent;
   protected final TestUtils.Action reify;

   public EquivalentTest() {
      this.factory = Equivalent::new;
      this.enforce = (v, x, y) -> factory.apply(x, y).enforce(v);
      this.prevent = (v, x, y) -> factory.apply(x, y).prevent(v);
      this.reify = (v, x, y) -> factory.apply(x, y).reify(v);
   }

   @Test
   @DataProvider({"1,1,1", "1,0:1,1", "0:1,1,1", "0,0,0", "0,0:1,0", "0:1,0,0"})
   public void testEnforceMatched(String inputLeft, String inputRight, String expected) {
      given(inputLeft, inputRight).when(enforce).then(ConstraintResult.MATCHED, expected);
   }

   @Test
   @DataProvider({"0:1,0:1"})
   public void testEnforceUnresolved(String inputLeft, String inputRight) {
      given(inputLeft, inputRight).when(enforce).then(ConstraintResult.UNRESOLVED, inputLeft, inputRight);
   }

   @Test
   @DataProvider({"1,0", "0,1"})
   public void testEnforceFailed(String inputLeft, String inputRight) {
      given(inputLeft, inputRight).when(enforce).then(ConstraintResult.FAILED);
   }

   @Test
   @DataProvider({ //
               "1,1,MATCHED",
               "0,0,MATCHED",
               "1,0,FAILED",
               "0,1,FAILED",
               "0:1,0:1,UNRESOLVED",
               "1,0:1,UNRESOLVED",
               "0:1,1,UNRESOLVED",
               "0,0:1,UNRESOLVED",
               "0:1,0,UNRESOLVED"})
   public void testReify(String inputLeft, String inputRight, ConstraintResult expected) {
      given(inputLeft, inputRight).when(reify).then(expected);
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
      Equivalent testObject = factory.apply(left, right);
      when(left.replaceVariables(function)).thenReturn(new FixedValue(42));
      when(right.replaceVariables(function)).thenReturn(new FixedValue(180));

      // when
      Equivalent replacement = testObject.replaceVariables(function);
      assertNotSame(testObject, replacement);
      assertEquals("Equivalent [left=FixedValue [value=42], right=FixedValue [value=180]]", replacement.toString());

      // then
      verify(left).replaceVariables(function);
      verify(right).replaceVariables(function);
      verifyNoMoreInteractions(function, left, right);
   }
}
