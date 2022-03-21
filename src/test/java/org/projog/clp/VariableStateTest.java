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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.function.Function;

import org.junit.Test;

public class VariableStateTest {
   @Test
   public void testDefaults() {
      VariableState v = new VariableState();
      assertEquals(Long.MIN_VALUE, v.getMin());
      assertEquals(Long.MAX_VALUE, v.getMax());
   }

   @Test
   public void testCopy_no_bitset() {
      VariableState original = new VariableState();
      original.setMin(5);
      original.setMax(10);

      VariableState copy = original.copy();
      assertNotEquals(copy, original);

      assertRange(original, 5, 10);
      assertRange(copy, 5, 10);

      copy.setMin(6);
      copy.setMax(8);
      assertRange(original, 5, 10);
      assertRange(copy, 6, 8);

      copy.setNot(7);
      assertRange(original, 5, 10);
      assertPossibilities(copy, 6, 8);

      original.setNot(8);
      assertPossibilities(original, 5, 6, 7, 9, 10);
      assertPossibilities(copy, 6, 8);
   }

   @Test
   public void testCopy_with_bitset() {
      VariableState original = new VariableState();
      original.setMin(5);
      original.setMax(10);
      original.setNot(8);

      VariableState copy = original.copy();
      assertNotEquals(copy, original);

      assertPossibilities(original, 5, 6, 7, 9, 10);
      assertPossibilities(copy, 5, 6, 7, 9, 10);

      copy.setMin(7);
      original.setMax(9);
      assertPossibilities(original, 5, 6, 7, 9);
      assertPossibilities(copy, 7, 9, 10);

      copy.setNot(7);
      assertPossibilities(original, 5, 6, 7, 9);
      assertPossibilities(copy, 9, 10);

      original.setNot(9);
      assertPossibilities(original, 5, 6, 7);
      assertPossibilities(copy, 9, 10);
   }

   @Test
   public void testSetValue_outside_range() {
      VariableState v = new VariableState();
      int min = 5;
      int max = 10;
      v.setMin(min);
      v.setMax(max);

      assertFailed(v, s -> s.setValue(min - 1));
      assertFailed(v, s -> s.setValue(max + 1));
   }

   @Test
   public void testSetValue_min() {
      VariableState v = new VariableState();
      int min = 5;
      v.setMin(min);
      v.setMax(10);

      assertEquals(ExpressionResult.UPDATED, v.setValue(min));
      assertValue(v, min);
   }

   @Test
   public void testSetValue_max() {
      VariableState v = new VariableState();
      int max = 10;
      v.setMin(5);
      v.setMax(max);

      assertEquals(ExpressionResult.UPDATED, v.setValue(max));
      assertValue(v, max);
   }

   @Test
   public void testSetValue_middle() {
      VariableState v = new VariableState();
      int value = 8;

      assertEquals(ExpressionResult.UPDATED, v.setValue(value));
      assertValue(v, value);

      assertEquals(ExpressionResult.NO_CHANGE, v.setValue(value));
      assertValue(v, value);

      assertFailed(v, s -> s.setValue(value - 1));
      assertFailed(v, s -> s.setValue(value + 1));
   }

   @Test
   public void testSetMax() {
      VariableState v = new VariableState();
      int min = 5;
      v.setMin(min);

      assertEquals(ExpressionResult.UPDATED, v.setMax(10));
      assertRange(v, min, 10);

      assertEquals(ExpressionResult.NO_CHANGE, v.setMax(10));
      assertEquals(ExpressionResult.NO_CHANGE, v.setMax(9999));
      assertRange(v, min, 10);

      assertFailed(v, s -> s.setMax(min - 1));
   }

   @Test
   public void testSetMin() {
      VariableState v = new VariableState();
      int max = 10;
      v.setMax(max);

      assertEquals(ExpressionResult.UPDATED, v.setMin(5));
      assertRange(v, 5, max);

      assertEquals(ExpressionResult.NO_CHANGE, v.setMin(5));
      assertEquals(ExpressionResult.NO_CHANGE, v.setMin(0));
      assertEquals(5, v.getMin());
      assertRange(v, 5, max);

      assertFailed(v, s -> s.setMin(max + 1));
   }

   /**
    * Test using setNot to alter the min/max values.
    */
   @Test
   public void testSetNot_min_max() {
      VariableState v = new VariableState();
      v.setMin(5);
      v.setMax(10);

      assertEquals(ExpressionResult.NO_CHANGE, v.setNot(4));
      assertEquals(ExpressionResult.NO_CHANGE, v.setNot(11));
      assertRange(v, 5, 10);

      assertEquals(ExpressionResult.UPDATED, v.setNot(5));
      assertRange(v, 6, 10);

      assertEquals(ExpressionResult.UPDATED, v.setNot(10));
      assertRange(v, 6, 9);

      assertEquals(ExpressionResult.NO_CHANGE, v.setNot(5));
      assertEquals(ExpressionResult.NO_CHANGE, v.setNot(10));
      assertEquals(6, v.getMin());
      assertEquals(9, v.getMax());
   }

   /**
    * Test using setNot to alter a value within the min/max range of values.
    */
   @Test
   public void testSetNot_1() {
      VariableState v = new VariableState();
      v.setMin(5);
      v.setMax(10);

      assertEquals(ExpressionResult.UPDATED, v.setNot(8));
      assertPossibilities(v, 5, 6, 7, 9, 10);
   }

   /**
    * Test using setNot failed
    */
   @Test
   public void testSetNot_2() {
      VariableState v = new VariableState();
      int value = 7;
      v.setValue(value);

      assertFailed(v, s -> s.setNot(value));
   }

   /**
    * Test using setMin when given value has been "not-ed".
    */
   @Test
   public void testSetNot_3() {
      VariableState v = new VariableState();
      v.setMin(5);
      v.setMax(10);

      assertEquals(ExpressionResult.UPDATED, v.setNot(8));
      assertEquals(ExpressionResult.UPDATED, v.setNot(7));
      assertEquals(ExpressionResult.UPDATED, v.setNot(6));
      assertEquals(ExpressionResult.UPDATED, v.setMin(6));

      assertRange(v, 9, 10);
   }

   /**
    * Test using setMax when given value has been "not-ed".
    */
   @Test
   public void testSetNot_4() {
      VariableState v = new VariableState();
      v.setMin(5);
      v.setMax(10);

      assertEquals(ExpressionResult.UPDATED, v.setNot(7));
      assertEquals(ExpressionResult.UPDATED, v.setNot(8));
      assertEquals(ExpressionResult.UPDATED, v.setNot(9));
      assertEquals(ExpressionResult.UPDATED, v.setMax(9));

      assertRange(v, 5, 6);
   }

   @Test
   public void testSetNot_5() {
      VariableState v = new VariableState();
      v.setMin(5);
      v.setMax(10);

      assertEquals(ExpressionResult.UPDATED, v.setNot(8));
      //      assertEquals(Result.UPDATED, v.setNot(7));
      assertEquals(ExpressionResult.UPDATED, v.setNot(6));
      assertEquals(ExpressionResult.UPDATED, v.setMin(6));

      assertPossibilities(v, 7, 9, 10);
   }

   @Test
   public void testSetNot_6() {
      VariableState v = new VariableState();
      v.setMin(5);
      v.setMax(10);

      assertEquals(ExpressionResult.UPDATED, v.setNot(7));
      //      assertEquals(Result.UPDATED, v.setNot(8));
      assertEquals(ExpressionResult.UPDATED, v.setNot(9));
      assertEquals(ExpressionResult.UPDATED, v.setMax(9));

      assertPossibilities(v, 5, 6, 8);
   }

   /**
    * Test using setMin when given value has been "not-ed" - causing single answer.
    */
   @Test
   public void testSetNot_7() {
      VariableState v = new VariableState();
      v.setMin(5);
      v.setMax(10);

      assertEquals(ExpressionResult.UPDATED, v.setNot(9));
      assertEquals(ExpressionResult.UPDATED, v.setNot(8));
      assertEquals(ExpressionResult.UPDATED, v.setNot(7));
      assertEquals(ExpressionResult.UPDATED, v.setNot(6));
      assertEquals(ExpressionResult.UPDATED, v.setMin(6));

      assertValue(v, 10);
   }

   /**
    * Test using setMax when given value has been "not-ed" - causing single answer.
    */
   @Test
   public void testSetNot_8() {
      VariableState v = new VariableState();
      v.setMin(5);
      v.setMax(10);

      assertEquals(ExpressionResult.UPDATED, v.setNot(6));
      assertEquals(ExpressionResult.UPDATED, v.setNot(7));
      assertEquals(ExpressionResult.UPDATED, v.setNot(8));
      assertEquals(ExpressionResult.UPDATED, v.setNot(9));
      assertEquals(ExpressionResult.UPDATED, v.setMax(9));

      assertValue(v, 5);
   }

   private void assertValue(VariableState state, int value) {
      assertRange(state, value, value);
   }

   private void assertRange(VariableState state, int min, int max) {
      assertEquals(min, state.getMin());
      assertEquals(max, state.getMax());
      Possibilities p = state.getPossibilities();
      for (int i = min; i <= max; i++) {
         assertTrue(p.hasNext());
         assertEquals(i, p.next());
      }
      assertFalse(p.hasNext());
   }

   private void assertPossibilities(VariableState state, int... values) {
      assertEquals(values[0], state.getMin());
      assertEquals(values[values.length - 1], state.getMax());
      Possibilities p = state.getPossibilities();
      for (int v : values) {
         assertTrue(p.hasNext());
         assertEquals(v, p.next());
      }
      assertFalse(p.hasNext());
   }

   private void assertFailed(VariableState state, Function<VariableState, ExpressionResult> f) {
      VariableState copy = state.copy();

      assertEquals(ExpressionResult.FAILED, f.apply(copy));

      // TODO check all public methods of VariableState
      assertThrows(IllegalStateException.class, () -> copy.copy());
      assertThrows(IllegalStateException.class, () -> copy.getMax());
      assertThrows(IllegalStateException.class, () -> copy.getMin());
      assertThrows(IllegalStateException.class, () -> copy.getPossibilities());
      assertThrows(IllegalStateException.class, () -> copy.setMax(1));
      assertThrows(IllegalStateException.class, () -> copy.setMin(1));
      assertThrows(IllegalStateException.class, () -> copy.setNot(1));
      assertThrows(IllegalStateException.class, () -> copy.setValue(0));
   }
}
