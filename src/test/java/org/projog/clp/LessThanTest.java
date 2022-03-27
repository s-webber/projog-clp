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
public class LessThanTest {
   private static final TestUtils.Action ENFORCE = (v, x, y) -> new LessThan(x, y).enforce(v);

   @Test
   @DataProvider({"3,4,3,4", "2,4,2,4", "-4,4,-4,4", "2:5,3,2,3", "2:5,4,2:3,4", "2:5,5,2:4,5", "2,2:5,2,3:5", "3,2:5,3,4:5", "4,2:5,4,5"})
   public void testEnforceMatched(String inputLeft, String inputRight, String expectedLeft, String expectedRight) {
      given(inputLeft, inputRight).when(ENFORCE).then(ConstraintResult.MATCHED, expectedLeft, expectedRight);
   }

   @Test
   @DataProvider({"0:9,0:9,0:8,1:9", "0:10,-1:9,0:8,1:9", "-8:14,12:42,-8:14,12:42"})
   public void testEnforceUnresolved(String inputLeft, String inputRight, String expectedLeft, String expectedRight) {
      given(inputLeft, inputRight).when(ENFORCE).then(ConstraintResult.UNRESOLVED, expectedLeft, expectedRight);
   }

   @Test
   @DataProvider({"8,7", "7,7", "9,7", "9,-7", "8:11,4:7", "8:11,4:6"})
   public void testEnforceFailed(String inputLeft, String inputRight) {
      given(inputLeft, inputRight).when(ENFORCE).then(ConstraintResult.FAILED);
   }

   @Test
   public void testWalk() {
      // given
      @SuppressWarnings("unchecked")
      Consumer<Expression> consumer = mock(Consumer.class);
      Expression left = mock(Expression.class);
      Expression right = mock(Expression.class);
      LessThan testObject = new LessThan(left, right);

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
      LessThan testObject = new LessThan(left, right);
      when(left.replace(function)).thenReturn(new FixedValue(42));
      when(right.replace(function)).thenReturn(new FixedValue(180));

      // when
      LessThan replacement = testObject.replace(function);
      assertNotSame(testObject, replacement);
      assertEquals("LessThan [left=FixedValue [value=42], right=FixedValue [value=180]]", replacement.toString());

      // then
      verify(left).replace(function);
      verify(right).replace(function);
      verifyNoMoreInteractions(function, left, right);
   }
}
