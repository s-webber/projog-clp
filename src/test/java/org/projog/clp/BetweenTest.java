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

import java.util.function.Consumer;
import java.util.function.Function;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;

@RunWith(DataProviderRunner.class)
public class BetweenTest {
   @Test
   @DataProvider({"30,40,30,40", "20,42,30,40", "33,38,33,38", "27,39,30,39", "31,42,31,40"})
   public void testEnforceMatched(long inputMin, long inputMax, long outputMin, long outputMax) {
      ClpConstraintStore.Builder builder = new ClpConstraintStore.Builder();
      Variable e = builder.createVariable();
      ConstraintStore variables = builder.build();
      e.setMin(variables, inputMin);
      e.setMax(variables, inputMax);

      Between b = new Between(e, 30, 40);

      assertEquals(ConstraintResult.MATCHED, b.enforce(variables));
      assertEquals(outputMin, e.getMin(variables));
      assertEquals(outputMax, e.getMax(variables));
   }

   @Test
   @DataProvider({"0,29", "41,50"})
   public void testEnforceFailed(long inputMin, long inputMax) {
      ClpConstraintStore.Builder builder = new ClpConstraintStore.Builder();
      Variable e = builder.createVariable();
      ConstraintStore variables = builder.build();
      e.setMin(variables, inputMin);
      e.setMax(variables, inputMax);

      Between b = new Between(e, 30, 40);

      assertEquals(ConstraintResult.FAILED, b.enforce(variables));
   }

   @Test
   @DataProvider({ //
               "30,40,MATCHED",
               "34,36,MATCHED",
               "29,40,UNRESOLVED",
               "30,41,UNRESOLVED",
               "29,41,UNRESOLVED",
               "0,29,FAILED",
               "41,50,FAILED"})
   public void testReify(long inputMin, long inputMax, ConstraintResult expected) {
      ClpConstraintStore.Builder builder = new ClpConstraintStore.Builder();
      Variable e = builder.createVariable();
      ConstraintStore variables = builder.build();
      e.setMin(variables, inputMin);
      e.setMax(variables, inputMax);

      Between b = new Between(e, 30, 40);

      assertEquals(expected, b.reify(variables));
   }

   @Test
   @DataProvider({ //
               "30,40,FAILED",
               "34,36,FAILED",
               "29,40,UNRESOLVED",
               "30,41,UNRESOLVED",
               "29,41,UNRESOLVED",
               "0,29,MATCHED",
               "41,50,MATCHED"})
   public void testPrevent(long inputMin, long inputMax, ConstraintResult expected) {
      ClpConstraintStore.Builder builder = new ClpConstraintStore.Builder();
      Variable e = builder.createVariable();
      ConstraintStore variables = builder.build();
      e.setMin(variables, inputMin);
      e.setMax(variables, inputMax);

      Between b = new Between(e, 30, 40);

      assertEquals(expected, b.prevent(variables));
   }

   @Test
   public void testWalk() {
      // given
      @SuppressWarnings("unchecked")
      Consumer<Expression> consumer = mock(Consumer.class);
      Expression expression = mock(Expression.class);
      Between testObject = new Between(expression, 7, 42);

      // when
      testObject.walk(consumer);

      // then
      verify(expression).walk(consumer);
      verifyNoMoreInteractions(consumer, expression);
   }

   @Test
   public void testReplaceVariables() {
      // given
      @SuppressWarnings("unchecked")
      Function<Variable, Variable> function = mock(Function.class);
      Expression expression = mock(Expression.class);
      Between testObject = new Between(expression, 7, 42);
      when(expression.replaceVariables(function)).thenReturn(new FixedValue(30));

      // when
      Between replacement = testObject.replaceVariables(function);
      assertNotSame(testObject, replacement);
      assertEquals("Between [e=FixedValue [value=30], min=7, max=42]", replacement.toString());

      // then
      verify(expression).replaceVariables(function);
      verifyNoMoreInteractions(function, expression);
   }
}
