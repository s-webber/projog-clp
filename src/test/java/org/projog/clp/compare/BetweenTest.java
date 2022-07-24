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

import org.projog.clp.AbstractConstraintTest;
import org.projog.clp.Expression;
import org.projog.clp.FixedValue;
import org.projog.clp.LeafExpression;
import org.testng.annotations.Test;

public class BetweenTest extends AbstractConstraintTest {
   private static final String IGNORE = "MIN:MAX";

   public BetweenTest() {
      super((x, y) -> new Between(x, 30, 40), false);

      enforce("30:40", IGNORE).matched();
      enforce("20:42", IGNORE).matched("30:40", IGNORE);
      enforce("33:38", IGNORE).matched();
      enforce("27:39", IGNORE).matched("30:39", IGNORE);
      enforce("31:42", IGNORE).matched("31:40", IGNORE);
      enforce("0:29", IGNORE).failed();
      enforce("41:50", IGNORE).failed();

      reify("30:40", IGNORE).matched();
      reify("34:36", IGNORE).matched();
      reify("29:40", IGNORE).unresolved();
      reify("30:41", IGNORE).unresolved();
      reify("29:41", IGNORE).unresolved();
      reify("0:29", IGNORE).failed();
      reify("41:50", IGNORE).failed();

      prevent("30:40", IGNORE).failed();
      prevent("34:36", IGNORE).failed();
      prevent("29:40", IGNORE).unresolved();
      prevent("30:41", IGNORE).unresolved();
      prevent("29:41", IGNORE).unresolved();
      prevent("0:29", IGNORE).matched();
      prevent("41:50", IGNORE).matched();
   }

   @Override
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

   @Override
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
