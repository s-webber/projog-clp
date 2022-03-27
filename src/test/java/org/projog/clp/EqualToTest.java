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

import java.util.function.Consumer;
import java.util.function.Function;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;

@RunWith(DataProviderRunner.class)
public class EqualToTest {
   private static final TestUtils.Action ENFORCE = (v, x, y) -> new EqualTo(x, y).enforce(v);

   @Test
   @DataProvider({"1,1,1", "8,7:9,8", "-8:14,14:42,14"})
   public void testEnforceMatched(String inputLeft, String inputRight, Long expected) {
      assertEnforceMatched(inputLeft, inputRight, expected);
      assertEnforceMatched(inputRight, inputLeft, expected);
   }

   private void assertEnforceMatched(String inputLeft, String inputRight, Long expected) {
      given(inputLeft, inputRight).when(ENFORCE).then(ConstraintResult.MATCHED, expected.toString());
   }

   @Test
   @DataProvider({"0:9,0:9,0:9", "-8:14,12:42,12:14"})
   public void testEnforceUnresolved(String inputLeft, String inputRight, String expected) {
      assertEnforceUnresolved(inputLeft, inputRight, expected);
      assertEnforceUnresolved(inputRight, inputLeft, expected);
   }

   private void assertEnforceUnresolved(String inputLeft, String inputRight, String expected) {
      given(inputLeft, inputRight).when(ENFORCE).then(ConstraintResult.UNRESOLVED, expected);
   }

   @Test
   @DataProvider({"-1,0:9", "10,0:9", "-9:-1,1:9", "12:14,15:22"})
   public void testEnforceFailed(String inputLeft, String inputRight) {
      assertEnforceFailed(inputLeft, inputRight);
      assertEnforceFailed(inputRight, inputLeft);
   }

   private void assertEnforceFailed(String inputLeft, String inputRight) {
      given(inputLeft, inputRight).when(ENFORCE).then(ConstraintResult.FAILED);
   }

   @Test
   public void testWalk() {
      // given
      @SuppressWarnings("unchecked")
      Consumer<Expression> consumer = mock(Consumer.class);
      Expression left = mock(Expression.class);
      Expression right = mock(Expression.class);
      EqualTo testObject = new EqualTo(left, right);

      // when
      testObject.walk(consumer);

      // then
      verify(left).walk(consumer);
      verify(right).walk(consumer);
      verifyNoMoreInteractions(consumer, left, right);
   }

   @Test
   public void testReplace() {
      // given
      @SuppressWarnings("unchecked")
      Function<Expression, Expression> function = mock(Function.class);
      Expression left = mock(Expression.class);
      Expression right = mock(Expression.class);
      EqualTo testObject = new EqualTo(left, right);
      when(left.replace(function)).thenReturn(new FixedValue(42));
      when(right.replace(function)).thenReturn(new FixedValue(180));

      // when
      EqualTo replacement = testObject.replace(function);
      assertNotSame(testObject, replacement);
      assertEquals("EqualTo [left=FixedValue [value=42], right=FixedValue [value=180]]", replacement.toString());

      // then
      verify(left).replace(function);
      verify(right).replace(function);
      verifyNoMoreInteractions(function, left, right);
   }
}
