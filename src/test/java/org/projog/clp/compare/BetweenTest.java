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
package org.projog.clp.compare;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotSame;

import java.util.function.Consumer;
import java.util.function.Function;

import org.projog.clp.ClpConstraintStore;
import org.projog.clp.ConstraintResult;
import org.projog.clp.ConstraintStore;
import org.projog.clp.Expression;
import org.projog.clp.FixedValue;
import org.projog.clp.LeafExpression;
import org.projog.clp.Variable;
import org.projog.clp.test.TestData;
import org.projog.clp.test.TestDataProvider;
import org.testng.annotations.Test;

public class BetweenTest {
   @Test(dataProvider = "process", dataProviderClass = TestDataProvider.class)
   @TestData({"30,40,30,40", "20,42,30,40", "33,38,33,38", "27,39,30,39", "31,42,31,40"})
   public void testEnforceMatched(Long inputMin, Long inputMax, Long outputMin, Long outputMax) {
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

   @Test(dataProvider = "process", dataProviderClass = TestDataProvider.class)
   @TestData({"0,29", "41,50"})
   public void testEnforceFailed(Long inputMin, Long inputMax) {
      ClpConstraintStore.Builder builder = new ClpConstraintStore.Builder();
      Variable e = builder.createVariable();
      ConstraintStore variables = builder.build();
      e.setMin(variables, inputMin);
      e.setMax(variables, inputMax);

      Between b = new Between(e, 30, 40);

      assertEquals(ConstraintResult.FAILED, b.enforce(variables));
   }

   @Test(dataProvider = "process", dataProviderClass = TestDataProvider.class)
   @TestData({ //
               "30,40,MATCHED",
               "34,36,MATCHED",
               "29,40,UNRESOLVED",
               "30,41,UNRESOLVED",
               "29,41,UNRESOLVED",
               "0,29,FAILED",
               "41,50,FAILED"})
   public void testReify(Long inputMin, Long inputMax, ConstraintResult expected) {
      ClpConstraintStore.Builder builder = new ClpConstraintStore.Builder();
      Variable e = builder.createVariable();
      ConstraintStore variables = builder.build();
      e.setMin(variables, inputMin);
      e.setMax(variables, inputMax);

      Between b = new Between(e, 30, 40);

      assertEquals(expected, b.reify(variables));
   }

   @Test(dataProvider = "process", dataProviderClass = TestDataProvider.class)
   @TestData({ //
               "30,40,FAILED",
               "34,36,FAILED",
               "29,40,UNRESOLVED",
               "30,41,UNRESOLVED",
               "29,41,UNRESOLVED",
               "0,29,MATCHED",
               "41,50,MATCHED"})
   public void testPrevent(Long inputMin, Long inputMax, ConstraintResult expected) {
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
   public void testReplace() {
      // given
      @SuppressWarnings("unchecked")
      Function<LeafExpression, LeafExpression> function = mock(Function.class);
      Expression expression = mock(Expression.class);
      Between testObject = new Between(expression, 7, 42);
      when(expression.replace(function)).thenReturn(new FixedValue(30));

      // when
      Between replacement = testObject.replace(function);
      assertNotSame(testObject, replacement);
      assertEquals("Between [e=FixedValue [value=30], min=7, max=42]", replacement.toString());

      // then
      verify(expression).replace(function);
      verifyNoMoreInteractions(function, expression);
   }
}
