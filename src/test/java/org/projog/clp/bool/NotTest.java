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
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

import java.util.function.Consumer;
import java.util.function.Function;

import org.projog.clp.ClpConstraintStore;
import org.projog.clp.Constraint;
import org.projog.clp.ConstraintResult;
import org.projog.clp.ConstraintStore;
import org.projog.clp.Expression;
import org.projog.clp.FixedValue;
import org.projog.clp.LeafExpression;
import org.projog.clp.Variable;
import org.projog.clp.test.Range;
import org.projog.clp.test.TestData;
import org.projog.clp.test.TestDataProvider;
import org.testng.annotations.Test;

public class NotTest {
   @Test(dataProvider = "process", dataProviderClass = TestDataProvider.class)
   @TestData({"0,MATCHED", "1,FAILED", "0:1,MATCHED"})
   public void testEnforce(Range input, ConstraintResult result) {
      ClpConstraintStore.Builder b = new ClpConstraintStore.Builder();
      Variable variable = b.createVariable();
      ConstraintStore store = b.build();
      Not not = new Not(variable);
      variable.setMin(store, input.min());
      variable.setMax(store, input.max());

      assertSame(result, not.enforce(store));

      if (result == ConstraintResult.MATCHED) {
         assertEquals(0, variable.getMin(store));
         assertEquals(0, variable.getMax(store));
      }
   }

   @Test(dataProvider = "process", dataProviderClass = TestDataProvider.class)
   @TestData({"0,FAILED", "1,MATCHED", "0:1,MATCHED"})
   public void testPrevent(Range input, ConstraintResult result) {
      ClpConstraintStore.Builder b = new ClpConstraintStore.Builder();
      Variable variable = b.createVariable();
      ConstraintStore store = b.build();
      Not not = new Not(variable);
      variable.setMin(store, input.min());
      variable.setMax(store, input.max());

      assertSame(result, not.prevent(store));

      if (result == ConstraintResult.MATCHED) {
         assertEquals(1, variable.getMin(store));
         assertEquals(1, variable.getMax(store));
      }
   }

   @Test(dataProvider = "process", dataProviderClass = TestDataProvider.class)
   @TestData({"0,MATCHED", "1,FAILED", "0:1,UNRESOLVED"})
   public void testReify(Range input, ConstraintResult result) {
      ClpConstraintStore.Builder b = new ClpConstraintStore.Builder();
      Variable variable = b.createVariable();
      ConstraintStore store = b.build();
      Not not = new Not(variable);
      variable.setMin(store, input.min());
      variable.setMax(store, input.max());

      assertSame(result, not.reify(store));
   }

   @Test
   public final void testWalk() {
      // given
      @SuppressWarnings("unchecked")
      Consumer<Expression> consumer = mock(Consumer.class);
      Constraint input = mock(Constraint.class);
      Not not = new Not(input);

      // when
      not.walk(consumer);

      // then
      verify(input).walk(consumer);
      verifyNoMoreInteractions(consumer, input);
   }

   @Test
   public final void testReplace() {
      // given
      @SuppressWarnings("unchecked")
      Function<LeafExpression, LeafExpression> function = mock(Function.class);
      Constraint input = mock(Constraint.class);
      FixedValue output = new FixedValue(42);
      Not not = new Not(input);
      org.mockito.Mockito.when(input.replace(function)).thenReturn(output);

      // when
      Not replacement = not.replace(function);
      assertEquals("Not [constraint=FixedValue [value=42]]", replacement.toString());

      // then
      verify(input).replace(function);
      verifyNoMoreInteractions(function, input);
   }
}
