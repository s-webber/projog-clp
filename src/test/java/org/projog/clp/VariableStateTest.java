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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertThrows;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.function.Function;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class VariableStateTest {
   @DataProvider
   public static Object[] testDataRanges() {
      return new Object[][] {{12, 17}, {-17, -12}, {0, 5}, {-5, 0}, {-3, 2}, {Long.MIN_VALUE, Long.MIN_VALUE + 5}, {Long.MAX_VALUE - 5, Long.MAX_VALUE}};
   }

   @Test
   public void testDefaults() {
      VariableState v = new VariableState();
      assertEquals(Long.MIN_VALUE, v.getMin());
      assertEquals(Long.MAX_VALUE, v.getMax());
      assertEquals(Long.MAX_VALUE, v.count());
      assertFalse(v.isSingleValue());
      assertFalse(v.isCorrupt());
   }

   @Test
   public void testSetMinSetMax() {
      int value = 7;

      VariableState v = new VariableState();
      assertFalse(v.isSingleValue());

      assertSame(VariableStateResult.UPDATED, v.setMin(value));
      assertFalse(v.isSingleValue());

      assertSame(VariableStateResult.UPDATED, v.setMax(value));
      assertTrue(v.isSingleValue());

      assertEquals(value, v.getMin());
      assertEquals(value, v.getMax());
      assertEquals(1, v.count());

      assertSame(VariableStateResult.NO_CHANGE, v.setMin(value));
      assertSame(VariableStateResult.NO_CHANGE, v.setMax(value));
   }

   @Test
   public void testSetValue() {
      int value = 7;

      VariableState v = new VariableState();
      assertFalse(v.isSingleValue());
      assertFalse(v.isCorrupt());

      assertSame(VariableStateResult.UPDATED, v.setValue(value));
      assertTrue(v.isSingleValue());
      assertFalse(v.isCorrupt());

      assertEquals(value, v.getMin());
      assertEquals(value, v.getMax());
      assertEquals(1, v.count());

      assertSame(VariableStateResult.NO_CHANGE, v.setValue(value));
      assertTrue(v.isSingleValue());
      assertFalse(v.isCorrupt());

      assertSame(VariableStateResult.FAILED, v.setValue(value + 1));
      assertTrue(v.isCorrupt());
   }

   @Test
   public void testCount() {
      VariableState v = new VariableState();
      assertEquals(Long.MAX_VALUE, v.count());

      v.setMin(-1);
      assertEquals(Long.MAX_VALUE, v.count());

      v.setMin(0);
      assertEquals(Long.MAX_VALUE, v.count());

      v.setMin(1);
      assertEquals(Long.MAX_VALUE, v.count());

      v.setMin(2);
      assertEquals(Long.MAX_VALUE - 1, v.count());

      v.setMin(3);
      assertEquals(Long.MAX_VALUE - 2, v.count());

      v.setMin(Long.MAX_VALUE - 1);
      assertEquals(2, v.count());

      v.setMin(Long.MAX_VALUE);
      assertEquals(1, v.count());
   }

   @Test(dataProvider = "testDataRanges")
   public void testAnd_no_overlap(long min, long max) {
      assertTrue(max > min + 4);

      VariableState a = new VariableState();
      a.setMin(min);
      a.setMax(min + 2);
      a.setNot(min + 1);

      VariableState b = new VariableState();
      b.setMin(min + 3);
      b.setMax(max);
      b.setNot(min + 4);
      assertNull(VariableState.and(a, b));
      assertNull(VariableState.and(b, a));
   }

   @Test(dataProvider = "testDataRanges")
   public void testAnd_range_within_another_no_matching_values_1(long min, long max) {
      VariableState a = new VariableState();
      a.setMin(min);
      a.setMax(max);
      for (long i = min + 1; i < max; i++) {
         a.setNot(i);
      }

      VariableState b = new VariableState();
      b.setMin(min + 1);
      b.setMax(max - 1);
      assertNull(VariableState.and(a, b));
      assertNull(VariableState.and(b, a));
   }

   @Test(dataProvider = "testDataRanges")
   public void testAnd_range_within_another_no_matching_values_b(long min, long max) {
      VariableState a = new VariableState();
      a.setMin(min);
      a.setMax(max);
      a.setNot(min + 1);
      a.setNot(min + 2);

      VariableState b = new VariableState();
      b.setMin(min + 1);
      b.setMax(min + 2);
      assertNull(VariableState.and(a, b));
      assertNull(VariableState.and(b, a));
   }

   @Test(dataProvider = "testDataRanges")
   public void testAnd_with_single_value_equals_min(long min, long max) {
      VariableState a = new VariableState();
      a.setMin(min);
      a.setMax(min);

      VariableState b = new VariableState();
      b.setMin(min);
      b.setMax(max);

      assertValue(VariableState.and(a, b), min);
      assertValue(VariableState.and(b, a), min);
   }

   @Test(dataProvider = "testDataRanges")
   public void testAnd_with_single_value_equals_max(long min, long max) {
      VariableState a = new VariableState();
      a.setMin(max);
      a.setMax(max);

      VariableState b = new VariableState();
      b.setMin(min);
      b.setMax(max);

      assertValue(VariableState.and(a, b), max);
      assertValue(VariableState.and(b, a), max);
   }

   @Test(dataProvider = "testDataRanges")
   public void testAnd_with_self(long min, long max) {
      VariableState a = new VariableState();
      a.setMin(min);
      a.setMax(max);

      assertSame(a, VariableState.and(a, a));
   }

   @Test(dataProvider = "testDataRanges")
   public void testAnd_with_identical(long min, long max) {
      VariableState a = new VariableState();
      a.setMin(min);
      a.setMax(max);

      VariableState b = new VariableState();
      b.setMin(min);
      b.setMax(max);

      assertSame(a, VariableState.and(a, b));
      assertSame(b, VariableState.and(b, a));
   }

   @Test(dataProvider = "testDataRanges")
   public void testAnd_within_another_exclusive(long min, long max) {
      VariableState a = new VariableState();
      a.setMin(min);
      a.setMax(max);

      VariableState b = new VariableState();
      b.setMin(min + 1);
      b.setMax(max - 1);

      assertSame(b, VariableState.and(a, b));
      assertSame(b, VariableState.and(b, a));
   }

   @Test(dataProvider = "testDataRanges")
   public void testAnd_within_another_start_inclusive(long min, long max) {
      VariableState a = new VariableState();
      a.setMin(min);
      a.setMax(max);

      VariableState b = new VariableState();
      b.setMin(min);
      b.setMax(max - 1);

      assertSame(b, VariableState.and(a, b));
      assertSame(b, VariableState.and(b, a));
   }

   @Test(dataProvider = "testDataRanges")
   public void testAnd_within_another_end_inclusive(long min, long max) {
      VariableState a = new VariableState();
      a.setMin(min + 1);
      a.setMax(max);

      VariableState b = new VariableState();
      b.setMin(min);
      b.setMax(max);

      assertSame(a, VariableState.and(a, b));
      assertSame(a, VariableState.and(b, a));
   }

   @Test(dataProvider = "testDataRanges")
   public void testAnd_overlap(long min, long max) {
      VariableState a = new VariableState();
      a.setMin(min);
      a.setMax(max - 1);

      VariableState b = new VariableState();
      b.setMin(min + 1);
      b.setMax(max);

      assertRange(VariableState.and(a, b), min + 1, max - 1);
      assertRange(VariableState.and(b, a), min + 1, max - 1);
   }

   @Test(dataProvider = "testDataRanges")
   public void testAnd_same_range_and_bitset(long min, long max) {
      VariableState a = new VariableState();
      a.setMin(min);
      a.setMax(max);
      a.setNot(min + 1);

      VariableState b = new VariableState();
      b.setMin(min);
      b.setMax(max);
      b.setNot(min + 1);

      assertSame(a, VariableState.and(a, b));
      assertSame(b, VariableState.and(b, a));
   }

   @Test(dataProvider = "testDataRanges")
   public void testAnd_same_range_and_values_but_created_with_different_min_values(long min, long max) {
      VariableState a = new VariableState();
      a.setMin(min);
      a.setMax(max);
      a.setNot(min);
      a.setNot(min + 2);

      VariableState b = new VariableState();
      b.setMin(min + 1);
      b.setMax(max);
      b.setNot(min + 2);

      // NOTE in this scenario .and could return "b" but creates a new VariableState instead
      assertNotSame(b, VariableState.and(a, b));
      assertNotSame(b, VariableState.and(b, a));
      assertPossibilities(VariableState.and(a, b), min + 1, min + 3, min + 4, min + 5);
      assertPossibilities(VariableState.and(a, b), min + 1, min + 3, min + 4, min + 5);
   }

   @Test
   public void testAnd_same_range_and_values_but_created_with_different_sizes() {
      VariableState a = new VariableState();
      a.setMin(1);
      a.setMax(4);
      a.setNot(2);

      VariableState b = new VariableState();
      b.setMin(1);
      b.setMax(5);
      b.setNot(2);
      b.setNot(5);

      assertSame(a, VariableState.and(a, b));
      assertSame(b, VariableState.and(b, a));
   }

   @Test
   public void testAnd_same_range_different_bitset() {
      VariableState a = new VariableState();
      a.setMin(1);
      a.setMax(4);
      a.setNot(2);

      VariableState b = new VariableState();
      b.setMin(1);
      b.setMax(4);
      b.setNot(3);

      assertPossibilities(VariableState.and(a, b), 1, 4);
      assertPossibilities(VariableState.and(b, a), 1, 4);
   }

   @Test
   public void testAnd_different_range_similar_bitset() {
      VariableState a = new VariableState();
      a.setMin(1);
      a.setMax(4);
      a.setNot(2);

      VariableState b = new VariableState();
      b.setMin(2);
      b.setMax(5);
      b.setNot(3);

      assertValue(VariableState.and(a, b), 4);
      assertValue(VariableState.and(b, a), 4);
   }

   @Test
   public void testAnd_range_overlap_but_no_matching_values_both_states_have_same_count() {
      VariableState a = new VariableState();
      a.setMin(5);
      a.setMax(10);
      a.setNot(6);
      a.setNot(7);
      a.setNot(8);
      a.setNot(9);

      VariableState b = new VariableState();
      b.setMin(7);
      b.setMax(12);
      b.setNot(8);
      b.setNot(9);
      b.setNot(10);
      b.setNot(11);

      assertTrue(a.count() == b.count());
      assertPossibilities(a, 5, 10);
      assertPossibilities(b, 7, 12);
      assertNull(VariableState.and(a, b));
      assertNull(VariableState.and(b, a));
   }

   @Test
   public void testAnd_range_overlap_but_no_matching_values_lower_state_has_more_values() {
      VariableState a = new VariableState();
      a.setMin(5);
      a.setMax(10);
      a.setNot(7);
      a.setNot(8);
      a.setNot(9);

      VariableState b = new VariableState();
      b.setMin(7);
      b.setMax(12);
      b.setNot(8);
      b.setNot(9);
      b.setNot(10);
      b.setNot(11);

      assertTrue(a.count() > b.count());
      assertPossibilities(a, 5, 6, 10);
      assertPossibilities(b, 7, 12);
      assertNull(VariableState.and(a, b));
      assertNull(VariableState.and(b, a));
   }

   @Test(dataProvider = "testDataRanges")
   public void testAnd_range_overlap_but_no_matching_values_lower_state_has_more_values(long min, long max) {
      VariableState a = new VariableState();
      a.setMin(min);
      a.setMax(max - 1);
      a.setNot(max - 2);

      VariableState b = new VariableState();
      b.setMin(max - 2);
      b.setMax(max);
      b.setNot(max - 1);

      assertTrue(a.count() > b.count());
      assertNull(VariableState.and(a, b));
      assertNull(VariableState.and(b, a));
   }

   @Test
   public void testAnd_range_overlap_but_no_matching_values_higher_state_has_more_values() {
      VariableState a = new VariableState();
      a.setMin(5);
      a.setMax(10);
      a.setNot(6);
      a.setNot(7);
      a.setNot(8);
      a.setNot(9);

      VariableState b = new VariableState();
      b.setMin(7);
      b.setMax(12);
      b.setNot(8);
      b.setNot(9);
      b.setNot(10);

      assertTrue(a.count() < b.count());
      assertPossibilities(a, 5, 10);
      assertPossibilities(b, 7, 11, 12);
      assertNull(VariableState.and(a, b));
      assertNull(VariableState.and(b, a));
   }

   @Test(dataProvider = "testDataRanges")
   public void testAnd_range_overlap_but_no_matching_values_higher_state_has_more_values(long min, long max) {
      VariableState a = new VariableState();
      a.setMin(min);
      a.setMax(min + 2);
      a.setNot(min + 1);

      VariableState b = new VariableState();
      b.setMin(min + 1);
      b.setMax(max);
      b.setNot(min + 2);

      assertTrue(a.count() < b.count());
      assertNull(VariableState.and(a, b));
      assertNull(VariableState.and(b, a));
   }

   @Test(dataProvider = "testDataRanges")
   public void testAnd_single_value_lower_than_bitset(long min, long max) {
      VariableState a = new VariableState();
      a.setMin(min);
      a.setMax(min);

      VariableState b = new VariableState();
      b.setMin(min + 1);
      b.setMax(max);
      b.setNot(min + 2);
      assertNull(VariableState.and(a, b));
      assertNull(VariableState.and(b, a));
   }

   @Test(dataProvider = "testDataRanges")
   public void testAnd_single_value_higher_than_bitset(long min, long max) {
      VariableState a = new VariableState();
      a.setMin(max);
      a.setMax(max);

      VariableState b = new VariableState();
      b.setMin(min);
      b.setMax(max - 1);
      b.setNot(max - 2);
      assertNull(VariableState.and(a, b));
      assertNull(VariableState.and(b, a));
   }

   @Test(dataProvider = "testDataRanges")
   public void testAnd_single_value_within_range_of_bitset(long min, long max) {
      VariableState a = new VariableState();
      a.setMin(min);
      a.setMax(max);

      VariableState b = new VariableState();
      b.setMin(min);
      b.setMax(max);
      b.setNot(min + 3);
      assertSame(b, VariableState.and(a, b));
      assertSame(b, VariableState.and(b, a));
   }

   @Test(dataProvider = "testDataRanges")
   public void testAnd_non_bitset_within_bitset_min_same(long min, long max) {
      VariableState a = new VariableState();
      a.setMin(min);
      a.setMax(max - 1);

      VariableState b = new VariableState();
      b.setMin(min);
      b.setMax(max);
      b.setNot(min + 1);

      assertPossibilities(VariableState.and(a, b), min, min + 2, min + 3, min + 4);
      assertPossibilities(VariableState.and(b, a), min, min + 2, min + 3, min + 4);
   }

   @Test(dataProvider = "testDataRanges")
   public void testAnd_non_bitset_within_bitset_max_same(long min, long max) {
      VariableState a = new VariableState();
      a.setMin(min + 1);
      a.setMax(max);

      VariableState b = new VariableState();
      b.setMin(min);
      b.setMax(max);
      b.setNot(max - 1);

      assertPossibilities(VariableState.and(a, b), min + 1, min + 2, min + 3, min + 5);
      assertPossibilities(VariableState.and(b, a), min + 1, min + 2, min + 3, min + 5);
   }

   @Test
   public void testAnd_bitset_example_1() {
      VariableState a = new VariableState();
      a.setMin(-17);
      a.setMax(-12);

      VariableState b = new VariableState();
      b.setMin(-16);
      b.setMax(-13);

      assertRange(VariableState.and(a, b), -16, -13);
      assertRange(VariableState.and(b, a), -16, -13);

      a.setNot(-14);

      assertPossibilities(VariableState.and(a, b), -16, -15, -13);
      assertPossibilities(VariableState.and(b, a), -16, -15, -13);

      b.setNot(-15);

      assertPossibilities(VariableState.and(a, b), -16, -13);
      assertPossibilities(VariableState.and(b, a), -16, -13);

      b.setNot(-16);

      assertValue(VariableState.and(a, b), -13);
      assertValue(VariableState.and(b, a), -13);
   }

   @Test
   public void testAnd_bitset_example_2() {
      VariableState a = new VariableState();
      a.setMin(12);
      a.setMax(17);

      VariableState b = new VariableState();
      b.setMin(13);
      b.setMax(16);

      assertRange(VariableState.and(a, b), 13, 16);
      assertRange(VariableState.and(b, a), 13, 16);

      a.setNot(13);

      assertRange(VariableState.and(a, b), 14, 16);
      assertRange(VariableState.and(b, a), 14, 16);

      b.setNot(15);

      assertPossibilities(VariableState.and(a, b), 14, 16);
      assertPossibilities(VariableState.and(b, a), 14, 16);

      b.setNot(14);

      assertValue(VariableState.and(a, b), 16);
      assertValue(VariableState.and(b, a), 16);
   }

   @Test
   public void testAnd_bitset_example_3() {
      VariableState a = new VariableState();
      a.setMin(12);
      a.setMax(17);

      VariableState b = new VariableState();
      b.setMin(13);
      b.setMax(16);

      assertRange(VariableState.and(a, b), 13, 16);
      assertRange(VariableState.and(b, a), 13, 16);

      a.setNot(16);

      assertRange(VariableState.and(a, b), 13, 15);
      assertRange(VariableState.and(b, a), 13, 15);

      b.setNot(13);

      assertRange(VariableState.and(a, b), 14, 15);
      assertRange(VariableState.and(b, a), 14, 15);

      b.setNot(15);
      assertValue(VariableState.and(a, b), 14);
      assertValue(VariableState.and(b, a), 14);
   }

   @Test(dataProvider = "testDataRanges")
   public void testAnd_bitset_example_4(long base, long max) {
      VariableState a = new VariableState();
      a.setMin(base + 1);
      a.setMax(base + 3);
      a.setNot(base + 2);

      VariableState b = new VariableState();
      b.setMin(base);
      b.setMax(base + 5);
      b.setNot(base + 1);

      assertEquals(b.getMax(), max);
      assertPossibilities(a, base + 1, base + 3);
      assertPossibilities(b, base, base + 2, base + 3, base + 4, base + 5);
      assertValue(VariableState.and(a, b), base + 3);
      assertValue(VariableState.and(b, a), base + 3);
   }

   @Test(dataProvider = "testDataRanges")
   public void testAnd_bitset_example_5(long base, long max) {
      VariableState a = new VariableState();
      a.setMin(base + 1);
      a.setMax(base + 3);
      a.setNot(base + 2);

      VariableState b = new VariableState();
      b.setMin(base);
      b.setMax(base + 5);
      b.setNot(base + 3);

      assertEquals(b.getMax(), max);
      assertPossibilities(a, base + 1, base + 3);
      assertPossibilities(b, base, base + 1, base + 2, base + 4, base + 5);
      assertValue(VariableState.and(a, b), base + 1);
      assertValue(VariableState.and(b, a), base + 1);
   }

   @Test(dataProvider = "testDataRanges")
   public void testAnd_bitset_example_6(long base, long max) {
      VariableState a = new VariableState();
      a.setMin(base + 2);
      a.setMax(base + 4);
      a.setNot(base + 3);

      VariableState b = new VariableState();
      b.setMin(base);
      b.setMax(base + 5);
      b.setNot(base + 2);

      assertEquals(b.getMax(), max);
      assertPossibilities(a, base + 2, base + 4);
      assertPossibilities(b, base, base + 1, base + 3, base + 4, base + 5);
      assertValue(VariableState.and(a, b), base + 4);
      assertValue(VariableState.and(b, a), base + 4);
   }

   @Test(dataProvider = "testDataRanges")
   public void testAnd_bitset_example_7(long base, long max) {
      VariableState a = new VariableState();
      a.setMin(base + 2);
      a.setMax(base + 4);
      a.setNot(base + 3);

      VariableState b = new VariableState();
      b.setMin(base);
      b.setMax(base + 5);
      b.setNot(base + 4);

      assertEquals(b.getMax(), max);
      assertPossibilities(a, base + 2, base + 4);
      assertPossibilities(b, base, base + 1, base + 2, base + 3, base + 5);
      assertValue(VariableState.and(a, b), base + 2);
      assertValue(VariableState.and(b, a), base + 2);
   }

   @Test
   public void testToString() {
      VariableState v = new VariableState();
      assertEquals("-9223372036854775808..9223372036854775807", v.toString());

      v.setMin(-3);
      assertEquals("-3..9223372036854775807", v.toString());

      v.setMax(3);
      assertEquals("-3..3", v.toString());

      v.setNot(-1);
      assertEquals("{-3, -2, 0, 1, 2, 3}", v.toString());

      v.setNot(0);
      assertEquals("{-3, -2, 1, 2, 3}", v.toString());

      v.setNot(-3);
      assertEquals("{-2, 1, 2, 3}", v.toString());

      v.setNot(-2);
      assertEquals("1..3", v.toString());

      v.setNot(2);
      assertEquals("{1, 3}", v.toString());

      v.setMax(1);
      assertEquals("1", v.toString());

      v.setMax(0);
      assertEquals("corrupt", v.toString());
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
   public void testSetValue_inside_range_but_not_set() {
      VariableState v = new VariableState();
      int min = 5;
      int max = 10;
      int value = min + 1;
      v.setMin(min);
      v.setMax(max);
      v.setNot(value);

      assertFailed(v, s -> s.setValue(value));
   }

   @Test
   public void testSetValue_min() {
      VariableState v = new VariableState();
      int min = 5;
      v.setMin(min);
      v.setMax(10);

      assertEquals(VariableStateResult.UPDATED, v.setValue(min));
      assertValue(v, min);
   }

   @Test
   public void testSetValue_max() {
      VariableState v = new VariableState();
      int max = 10;
      v.setMin(5);
      v.setMax(max);

      assertEquals(VariableStateResult.UPDATED, v.setValue(max));
      assertValue(v, max);
   }

   @Test
   public void testSetValue_middle() {
      VariableState v = new VariableState();
      int value = 8;

      assertEquals(VariableStateResult.UPDATED, v.setValue(value));
      assertValue(v, value);

      assertEquals(VariableStateResult.NO_CHANGE, v.setValue(value));
      assertValue(v, value);

      assertFailed(v, s -> s.setValue(value - 1));
      assertFailed(v, s -> s.setValue(value + 1));
   }

   @Test
   public void testSetMax() {
      VariableState v = new VariableState();
      int min = 5;
      v.setMin(min);

      assertEquals(VariableStateResult.UPDATED, v.setMax(10));
      assertRange(v, min, 10);

      assertEquals(VariableStateResult.NO_CHANGE, v.setMax(10));
      assertEquals(VariableStateResult.NO_CHANGE, v.setMax(9999));
      assertRange(v, min, 10);

      assertFailed(v, s -> s.setMax(min - 1));
   }

   @Test
   public void testSetMin() {
      VariableState v = new VariableState();
      int max = 10;
      v.setMax(max);

      assertEquals(VariableStateResult.UPDATED, v.setMin(5));
      assertRange(v, 5, max);

      assertEquals(VariableStateResult.NO_CHANGE, v.setMin(5));
      assertEquals(VariableStateResult.NO_CHANGE, v.setMin(0));
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

      assertEquals(VariableStateResult.NO_CHANGE, v.setNot(4));
      assertEquals(VariableStateResult.NO_CHANGE, v.setNot(11));
      assertRange(v, 5, 10);

      assertEquals(VariableStateResult.UPDATED, v.setNot(5));
      assertRange(v, 6, 10);

      assertEquals(VariableStateResult.UPDATED, v.setNot(10));
      assertRange(v, 6, 9);

      assertEquals(VariableStateResult.NO_CHANGE, v.setNot(5));
      assertEquals(VariableStateResult.NO_CHANGE, v.setNot(10));
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

      assertEquals(VariableStateResult.UPDATED, v.setNot(8));
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

      assertEquals(VariableStateResult.UPDATED, v.setNot(8));
      assertEquals(VariableStateResult.UPDATED, v.setNot(7));
      assertEquals(VariableStateResult.UPDATED, v.setNot(6));
      assertEquals(VariableStateResult.UPDATED, v.setMin(6));

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

      assertEquals(VariableStateResult.UPDATED, v.setNot(7));
      assertEquals(VariableStateResult.UPDATED, v.setNot(8));
      assertEquals(VariableStateResult.UPDATED, v.setNot(9));
      assertEquals(VariableStateResult.UPDATED, v.setMax(9));

      assertRange(v, 5, 6);
   }

   @Test
   public void testSetNot_5() {
      VariableState v = new VariableState();
      v.setMin(5);
      v.setMax(10);

      assertEquals(VariableStateResult.UPDATED, v.setNot(8));
      assertEquals(VariableStateResult.UPDATED, v.setNot(6));
      assertEquals(VariableStateResult.UPDATED, v.setMin(6));

      assertPossibilities(v, 7, 9, 10);
   }

   @Test
   public void testSetNot_6() {
      VariableState v = new VariableState();
      v.setMin(5);
      v.setMax(10);

      assertEquals(VariableStateResult.UPDATED, v.setNot(7));
      assertEquals(VariableStateResult.UPDATED, v.setNot(9));
      assertEquals(VariableStateResult.UPDATED, v.setMax(9));

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

      assertEquals(VariableStateResult.UPDATED, v.setNot(9));
      assertEquals(VariableStateResult.UPDATED, v.setNot(8));
      assertEquals(VariableStateResult.UPDATED, v.setNot(7));
      assertEquals(VariableStateResult.UPDATED, v.setNot(6));
      assertEquals(VariableStateResult.UPDATED, v.setMin(6));

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

      assertEquals(VariableStateResult.UPDATED, v.setNot(6));
      assertEquals(VariableStateResult.UPDATED, v.setNot(7));
      assertEquals(VariableStateResult.UPDATED, v.setNot(8));
      assertEquals(VariableStateResult.UPDATED, v.setNot(9));
      assertEquals(VariableStateResult.UPDATED, v.setMax(9));

      assertValue(v, 5);
   }

   /** Test using setNot on unbound variable */
   @Test
   public void testSetNot_9() {
      VariableState v = new VariableState();

      assertEquals(VariableStateResult.NO_CHANGE, v.setNot(0));
   }

   private void assertValue(VariableState state, long value) {
      assertRange(state, value, value);
   }

   private void assertRange(VariableState state, long min, long max) {
      // test toString
      assertEquals(min == max ? "" + min : min + ".." + max, state.toString());
      // test count
      assertEquals(max - min + 1, state.count());
      // test min/max
      assertEquals(min, state.getMin());
      assertEquals(max, state.getMax());
      // test single value
      assertEquals(state.getMax() == state.getMin(), state.isSingleValue());
      // test not corrupt
      assertFalse(state.isCorrupt());
      // test all possibilities
      Possibilities p = state.getPossibilities();
      for (long i = min; i <= max && i >= min; i++) { // do i>min to avoid overflow
         assertTrue(p.hasNext());
         assertEquals(i, p.next());
      }
      assertFalse(p.hasNext());
   }

   private void assertPossibilities(VariableState state, long... values) {
      // test toString
      if (state.getMax() - state.getMin() + 1 == values.length) {
         assertEquals(values[0] + ".." + values[values.length - 1], state.toString());
      } else {
         assertEquals(Arrays.toString(values).toString().replace('[', '{').replace(']', '}'), state.toString());
      }
      // test count
      assertEquals(values.length, state.count());
      // test min/max
      assertEquals(values[0], state.getMin());
      assertEquals(values[values.length - 1], state.getMax());
      // test all possibilities
      Possibilities p = state.getPossibilities();
      for (long v : values) {
         assertTrue(p.hasNext());
         assertEquals(v, p.next());
      }
      assertFalse(p.hasNext());
   }

   private void assertFailed(VariableState state, Function<VariableState, VariableStateResult> f) {
      VariableState copy = state.copy();

      assertEquals(VariableStateResult.FAILED, f.apply(copy));

      assertEquals("corrupt", copy.toString());
      assertTrue(copy.isCorrupt());
      assertThrows(IllegalStateException.class, () -> copy.copy());
      assertThrows(IllegalStateException.class, () -> copy.count());
      assertThrows(IllegalStateException.class, () -> copy.getMax());
      assertThrows(IllegalStateException.class, () -> copy.getMin());
      assertThrows(IllegalStateException.class, () -> copy.getPossibilities());
      assertThrows(IllegalStateException.class, () -> copy.isSingleValue());
      assertThrows(IllegalStateException.class, () -> copy.setMax(1));
      assertThrows(IllegalStateException.class, () -> copy.setMin(1));
      assertThrows(IllegalStateException.class, () -> copy.setNot(1));
      assertThrows(IllegalStateException.class, () -> copy.setValue(0));
   }
}
