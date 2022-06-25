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
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

public class NumberSetTest {
   @Test
   public void testTooManyBits() {
      try {
         new NumberSet(0, Integer.MAX_VALUE);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("2147483647-0+1=2147483648>2147483647", e.getMessage());
      }
   }

   @Test
   public void testStartEqualsEnd() {
      try {
         new NumberSet(7, 7);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("7>=7", e.getMessage());
      }
   }

   @Test
   public void testStartAfterEnd() {
      try {
         new NumberSet(8, 7);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("8>=7", e.getMessage());
      }
   }

   @Ignore
   @Test
   public void testMaxRange() {
      NumberSet s = new NumberSet(1, Integer.MAX_VALUE);
      assertEquals(Integer.MAX_VALUE, s.cardinality());

      assertTrue(s.get(Integer.MAX_VALUE));
      s.clear(Integer.MAX_VALUE);
      assertFalse(s.get(Integer.MAX_VALUE));
      assertEquals(Integer.MAX_VALUE - 1, s.cardinality());

      assertTrue(s.get(1));
      s.clear(1);
      assertFalse(s.get(1));
      assertEquals(Integer.MAX_VALUE - 2, s.cardinality());
   }

   @Test
   public void testMinRange() {
      NumberSet s = new NumberSet(6, 7);
      assertEquals(2, s.cardinality());

      assertTrue(s.get(6));
      s.clear(6);
      assertFalse(s.get(6));
      assertEquals(1, s.cardinality());

      assertTrue(s.get(7));
      s.clear(7);
      assertFalse(s.get(7));
      assertEquals(0, s.cardinality());
   }

   @Test
   public void testClearValue() {
      NumberSet s = new NumberSet(7, 9);
      assertTrue(s.get(7));
      assertTrue(s.get(8));
      assertTrue(s.get(9));

      s.clear(8);
      assertTrue(s.get(7));
      assertFalse(s.get(8));
      assertTrue(s.get(9));

      s.clear(9);
      assertTrue(s.get(7));
      assertFalse(s.get(8));
      assertFalse(s.get(9));

      s.clear(7);
      assertFalse(s.get(7));
      assertFalse(s.get(8));
      assertFalse(s.get(9));

      // clearing already cleared value has no affect
      s.clear(7);
      s.clear(8);
      s.clear(9);
      assertFalse(s.get(7));
      assertFalse(s.get(8));
      assertFalse(s.get(9));

      // OK to clear a larger value than in set
      s.clear(10);

      try {
         s.clear(6);
         fail();
      } catch (IndexOutOfBoundsException e) {
      }
   }

   @Test
   public void testClearRange() {
      NumberSet s = new NumberSet(6, 12);
      assertTrue(s.get(6));
      assertTrue(s.get(7));
      assertTrue(s.get(8));
      assertTrue(s.get(9));
      assertTrue(s.get(10));
      assertTrue(s.get(11));
      assertTrue(s.get(12));

      s.clear(6, 6); // toValue is inclusive, so one value cleared
      assertFalse(s.get(6));
      assertTrue(s.get(7));
      assertTrue(s.get(8));
      assertTrue(s.get(9));
      assertTrue(s.get(10));
      assertTrue(s.get(11));
      assertTrue(s.get(12));

      s.clear(9, 11); // toValue is inclusive, so 3 values cleared
      assertFalse(s.get(6));
      assertTrue(s.get(7));
      assertTrue(s.get(8));
      assertFalse(s.get(9));
      assertFalse(s.get(10));
      assertFalse(s.get(11));
      assertTrue(s.get(12));

      // clearing already cleared values (9, 10 and 11) leaves them cleared
      s.clear(9, 12);
      assertFalse(s.get(6));
      assertTrue(s.get(7));
      assertTrue(s.get(8));
      assertFalse(s.get(9));
      assertFalse(s.get(10));
      assertFalse(s.get(11));
      assertFalse(s.get(12));

      // OK to clear a larger value than in set
      s.clear(6, 13);
      assertFalse(s.get(6));
      assertFalse(s.get(7));
      assertFalse(s.get(8));
      assertFalse(s.get(9));
      assertFalse(s.get(10));
      assertFalse(s.get(11));
      assertFalse(s.get(12));

      try {
         s.clear(5, 12);
         fail();
      } catch (IndexOutOfBoundsException e) {
      }
   }

   @Test
   public void testCopy() {
      NumberSet original = new NumberSet(7, 10);
      original.clear(8);
      assertTrue(original.get(7));
      assertFalse(original.get(8));
      assertTrue(original.get(9));
      assertTrue(original.get(10));

      NumberSet copy = original.copy();

      // assert copy starts with same values as original
      assertTrue(copy.get(7));
      assertFalse(copy.get(8));
      assertTrue(copy.get(9));
      assertTrue(copy.get(9));

      original.clear(7);
      copy.clear(9);
      original.clear(10);

      // assert changes in original are not reflected in copy, and changes in copy are not reflected in original
      assertFalse(original.get(7));
      assertFalse(original.get(8));
      assertTrue(original.get(9));
      assertFalse(original.get(10));
      assertTrue(copy.get(7));
      assertFalse(copy.get(8));
      assertFalse(copy.get(9));
      assertTrue(copy.get(10));
   }

   @Test
   public void testNextSetValue() {
      NumberSet s = new NumberSet(7, 12);

      try {
         s.nextSetValue(6);
         fail();
      } catch (IndexOutOfBoundsException e) {
      }

      assertEquals(7, s.nextSetValue(7));
      assertEquals(8, s.nextSetValue(8));
      assertEquals(9, s.nextSetValue(9));
      assertEquals(10, s.nextSetValue(10));
      assertEquals(11, s.nextSetValue(11));
      assertEquals(12, s.nextSetValue(12));
      assertEquals(6, s.nextSetValue(13)); // 6 = start value - 1

      s.clear(8);
      assertEquals(7, s.nextSetValue(7));
      assertEquals(9, s.nextSetValue(8));
      assertEquals(9, s.nextSetValue(9));
      assertEquals(10, s.nextSetValue(10));
      assertEquals(11, s.nextSetValue(11));
      assertEquals(12, s.nextSetValue(12));
      assertEquals(6, s.nextSetValue(13));

      s.clear(9);
      assertEquals(7, s.nextSetValue(7));
      assertEquals(10, s.nextSetValue(8));
      assertEquals(10, s.nextSetValue(9));
      assertEquals(10, s.nextSetValue(10));
      assertEquals(11, s.nextSetValue(11));
      assertEquals(12, s.nextSetValue(12));
      assertEquals(6, s.nextSetValue(13));

      s.clear(12);
      assertEquals(7, s.nextSetValue(7));
      assertEquals(10, s.nextSetValue(8));
      assertEquals(10, s.nextSetValue(9));
      assertEquals(10, s.nextSetValue(10));
      assertEquals(11, s.nextSetValue(11));
      assertEquals(6, s.nextSetValue(12));
      assertEquals(6, s.nextSetValue(13));

      s.clear(11);
      assertEquals(7, s.nextSetValue(7));
      assertEquals(10, s.nextSetValue(8));
      assertEquals(10, s.nextSetValue(9));
      assertEquals(10, s.nextSetValue(10));
      assertEquals(6, s.nextSetValue(11));
      assertEquals(6, s.nextSetValue(12));
      assertEquals(6, s.nextSetValue(13));

      s.clear(7);
      assertEquals(10, s.nextSetValue(7));
      assertEquals(10, s.nextSetValue(8));
      assertEquals(10, s.nextSetValue(9));
      assertEquals(10, s.nextSetValue(10));
      assertEquals(6, s.nextSetValue(11));
      assertEquals(6, s.nextSetValue(12));
      assertEquals(6, s.nextSetValue(13));

      s.clear(10);
      assertEquals(6, s.nextSetValue(7));
      assertEquals(6, s.nextSetValue(8));
      assertEquals(6, s.nextSetValue(9));
      assertEquals(6, s.nextSetValue(10));
      assertEquals(6, s.nextSetValue(11));
      assertEquals(6, s.nextSetValue(12));
      assertEquals(6, s.nextSetValue(13));
   }

   @Test
   public void testPreviousSetValue() {
      NumberSet s = new NumberSet(7, 12);

      try {
         s.previousSetValue(5);
         fail();
      } catch (IndexOutOfBoundsException e) {
      }

      assertEquals(6, s.previousSetValue(6)); // 6 = start value - 1
      assertEquals(7, s.previousSetValue(7));
      assertEquals(8, s.previousSetValue(8));
      assertEquals(9, s.previousSetValue(9));
      assertEquals(10, s.previousSetValue(10));
      assertEquals(11, s.previousSetValue(11));
      assertEquals(12, s.previousSetValue(12));
      assertEquals(12, s.previousSetValue(13));
      assertEquals(12, s.previousSetValue(999));

      s.clear(8);
      assertEquals(6, s.previousSetValue(6));
      assertEquals(7, s.previousSetValue(7));
      assertEquals(7, s.previousSetValue(8));
      assertEquals(9, s.previousSetValue(9));
      assertEquals(10, s.previousSetValue(10));
      assertEquals(11, s.previousSetValue(11));
      assertEquals(12, s.previousSetValue(12));

      s.clear(9);
      assertEquals(6, s.previousSetValue(6));
      assertEquals(7, s.previousSetValue(7));
      assertEquals(7, s.previousSetValue(8));
      assertEquals(7, s.previousSetValue(9));
      assertEquals(10, s.previousSetValue(10));
      assertEquals(11, s.previousSetValue(11));
      assertEquals(12, s.previousSetValue(12));

      s.clear(12);
      assertEquals(6, s.previousSetValue(6));
      assertEquals(7, s.previousSetValue(7));
      assertEquals(7, s.previousSetValue(8));
      assertEquals(7, s.previousSetValue(9));
      assertEquals(10, s.previousSetValue(10));
      assertEquals(11, s.previousSetValue(11));
      assertEquals(11, s.previousSetValue(12));

      s.clear(11);
      assertEquals(6, s.previousSetValue(6));
      assertEquals(7, s.previousSetValue(7));
      assertEquals(7, s.previousSetValue(8));
      assertEquals(7, s.previousSetValue(9));
      assertEquals(10, s.previousSetValue(10));
      assertEquals(10, s.previousSetValue(11));
      assertEquals(10, s.previousSetValue(12));

      s.clear(7);
      assertEquals(6, s.previousSetValue(6));
      assertEquals(6, s.previousSetValue(7));
      assertEquals(6, s.previousSetValue(8));
      assertEquals(6, s.previousSetValue(9));
      assertEquals(10, s.previousSetValue(10));
      assertEquals(10, s.previousSetValue(11));
      assertEquals(10, s.previousSetValue(12));

      s.clear(10);
      assertEquals(6, s.previousSetValue(6));
      assertEquals(6, s.previousSetValue(7));
      assertEquals(6, s.previousSetValue(8));
      assertEquals(6, s.previousSetValue(9));
      assertEquals(6, s.previousSetValue(10));
      assertEquals(6, s.previousSetValue(11));
      assertEquals(6, s.previousSetValue(12));
   }

   @Test
   public void testMinValues() {
      NumberSet s = new NumberSet(Long.MIN_VALUE, Long.MIN_VALUE + 2);
      assertTrue(s.get(Long.MIN_VALUE));
      assertTrue(s.get(Long.MIN_VALUE + 1));
      assertTrue(s.get(Long.MIN_VALUE + 2));

      assertEquals(Long.MIN_VALUE, s.nextSetValue(Long.MIN_VALUE));
      assertEquals(Long.MIN_VALUE, s.previousSetValue(Long.MIN_VALUE));

      s.clear(Long.MIN_VALUE);

      assertEquals(Long.MIN_VALUE + 1, s.nextSetValue(Long.MIN_VALUE));
      assertEquals(Long.MAX_VALUE, s.previousSetValue(Long.MIN_VALUE));

      assertFalse(s.get(Long.MIN_VALUE));
      assertTrue(s.get(Long.MIN_VALUE + 1));
      assertTrue(s.get(Long.MIN_VALUE + 2));
   }

   @Test
   public void testMinValues_clear() {
      NumberSet s = new NumberSet(Long.MIN_VALUE, Long.MIN_VALUE + 2);
      assertTrue(s.get(Long.MIN_VALUE));
      assertTrue(s.get(Long.MIN_VALUE + 1));
      assertTrue(s.get(Long.MIN_VALUE + 2));

      s.clear(Long.MIN_VALUE, Long.MIN_VALUE + 2);

      assertFalse(s.get(Long.MIN_VALUE));
      assertFalse(s.get(Long.MIN_VALUE + 1));
      assertFalse(s.get(Long.MIN_VALUE + 2));

      assertEquals(Long.MAX_VALUE, s.nextSetValue(Long.MIN_VALUE));
      assertEquals(Long.MAX_VALUE, s.previousSetValue(Long.MIN_VALUE));
      try {
         s.nextSetValue(Long.MAX_VALUE);
         fail();
      } catch (IndexOutOfBoundsException e) {
         // expected
      }
      assertEquals(Long.MAX_VALUE, s.previousSetValue(Long.MAX_VALUE));
      assertEquals(Long.MAX_VALUE, s.nextSetValue(0));
      assertEquals(Long.MAX_VALUE, s.previousSetValue(0));
   }

   @Test
   public void testMaxValues() {
      NumberSet s = new NumberSet(Long.MAX_VALUE - 2, Long.MAX_VALUE);
      assertTrue(s.get(Long.MAX_VALUE));
      assertTrue(s.get(Long.MAX_VALUE - 1));
      assertTrue(s.get(Long.MAX_VALUE - 2));

      assertEquals(Long.MAX_VALUE, s.nextSetValue(Long.MAX_VALUE));
      assertEquals(Long.MAX_VALUE, s.previousSetValue(Long.MAX_VALUE));

      s.clear(Long.MAX_VALUE);

      assertEquals(Long.MAX_VALUE - 3, s.nextSetValue(Long.MAX_VALUE));
      assertEquals(Long.MAX_VALUE - 1, s.previousSetValue(Long.MAX_VALUE));

      assertFalse(s.get(Long.MAX_VALUE));
      assertTrue(s.get(Long.MAX_VALUE - 1));
      assertTrue(s.get(Long.MAX_VALUE - 2));
   }

   @Test
   public void testMaxValues_clear() {
      NumberSet s = new NumberSet(Long.MAX_VALUE - 2, Long.MAX_VALUE);
      assertTrue(s.get(Long.MAX_VALUE));
      assertTrue(s.get(Long.MAX_VALUE - 1));
      assertTrue(s.get(Long.MAX_VALUE - 2));

      s.clear(Long.MAX_VALUE - 2, Long.MAX_VALUE);
      assertFalse(s.get(Long.MAX_VALUE));
      assertFalse(s.get(Long.MAX_VALUE - 1));
      assertFalse(s.get(Long.MAX_VALUE - 2));

      assertEquals(Long.MAX_VALUE - 3, s.nextSetValue(Long.MAX_VALUE));
      assertEquals(Long.MAX_VALUE - 3, s.previousSetValue(Long.MAX_VALUE));
      assertEquals(Long.MAX_VALUE - 3, s.nextSetValue(Long.MIN_VALUE));
      assertEquals(Long.MAX_VALUE - 3, s.previousSetValue(Long.MIN_VALUE));
      assertEquals(Long.MAX_VALUE - 3, s.nextSetValue(0));
      assertEquals(Long.MAX_VALUE - 3, s.previousSetValue(0));
   }

   @Test
   public void testGet() {
      NumberSet s = new NumberSet(7, 9);
      try {
         s.get(6);
         fail();
      } catch (IndexOutOfBoundsException e) {
      }
      assertTrue(s.get(7));
      assertTrue(s.get(8));
      assertTrue(s.get(9));
      assertFalse(s.get(10));

      s.clear(7, 9);
      assertFalse(s.get(7));
      assertFalse(s.get(8));
      assertFalse(s.get(9));
   }

   @Test
   public void testCardinality() {
      NumberSet s = new NumberSet(7, 9);
      assertEquals(3, s.cardinality());
      s.clear(7);
      assertEquals(2, s.cardinality());
      s.clear(8);
      assertEquals(1, s.cardinality());
      s.clear(9);
      assertEquals(0, s.cardinality());
   }

   @Test
   public void testEquals() {
      NumberSet b1 = new NumberSet(7, 9);
      assertTrue(b1.equals(b1));

      NumberSet b2 = new NumberSet(7, 9);
      assertTrue(b1.equals(b2));
      assertEquals(b1.hashCode(), b2.hashCode());

      b1.clear(8);
      assertTrue(b1.equals(b1));
      assertFalse(b1.equals(b2));
      assertFalse(b2.equals(b1));
      assertNotEquals(b1.hashCode(), b2.hashCode());

      assertFalse(b1.equals(new NumberSet(7, 10)));
      assertFalse(b1.equals(new NumberSet(6, 8)));
      assertFalse(b1.equals(new NumberSet(8, 10)));
   }

   @Test
   public void testToString() {
      NumberSet s = new NumberSet(-3, 10);
      assertEquals("{-3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10}", s.toString());

      s.clear(-3);
      assertEquals("{-2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10}", s.toString());

      s.clear(-1, 7);
      assertEquals("{-2, 8, 9, 10}", s.toString());

      s.clear(8, 11);
      assertEquals("{-2}", s.toString());

      s.clear(-2);
      assertEquals("{}", s.toString());
   }
}
