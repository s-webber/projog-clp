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

import java.util.function.Consumer;
import java.util.function.Function;

import org.projog.clp.AbstractConstraintTest;
import org.projog.clp.Constraint;
import org.projog.clp.Expression;
import org.projog.clp.FixedValue;
import org.projog.clp.LeafExpression;
import org.testng.annotations.Test;

public class NotTest extends AbstractConstraintTest {
   private static final String IGNORE = "MIN:MAX";

   public NotTest() {
      super((x, y) -> new Not(x), false);

      enforce("0", IGNORE).matched();
      enforce("1", IGNORE).failed();
      enforce("0:1", IGNORE).matched("0", IGNORE);

      prevent("0", IGNORE).failed();
      prevent("1", IGNORE).matched();
      prevent("0:1", IGNORE).matched("1", IGNORE);

      reify("0", IGNORE).matched();
      reify("1", IGNORE).failed();
      reify("0:1", IGNORE).unresolved();
   }

   @Override
   @Test
   public void testWalk() {
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

   @Override
   @Test
   public void testReplace() {
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
