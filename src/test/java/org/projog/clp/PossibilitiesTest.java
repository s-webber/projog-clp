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
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PossibilitiesTest {
   @Test
   public void test() {
      int min = 3;
      int max = 10;
      Possibilities p = new Possibilities(min, max, null);
      for (int i = min; i <= max; i++) {
         assertTrue(p.hasNext());
         assertEquals(i, p.next());
      }
      assertFalse(p.hasNext());
   }

   @Test
   public void testBitSet() {
      MyBitSet b = new MyBitSet(3, 10);
      int min = 3;
      int max = 10;
      b.set(min, min + 1);
      b.set(5,6);
      b.set(6,7);
      b.set(8,9);
      b.set(max, max + 1);

      Possibilities p = new Possibilities(min, max, b);
      assertTrue(p.hasNext());
      assertEquals(3, p.next());
      assertTrue(p.hasNext());
      assertEquals(5, p.next());
      assertTrue(p.hasNext());
      assertEquals(6, p.next());
      assertTrue(p.hasNext());
      assertEquals(8, p.next());
      assertTrue(p.hasNext());
      assertEquals(10, p.next());
      assertFalse(p.hasNext());
   }

   @Test
   public void testBitSetNegative() {
      MyBitSet b = new MyBitSet(-5, 3);
      int min = -5;
      int max = 3;
      b.set(-4, -3);
      b.set(-2, max + 1);

      Possibilities p = new Possibilities(min, max, b);
      assertTrue(p.hasNext());
      assertEquals(-5, p.next());
      assertTrue(p.hasNext());
      assertEquals(-4, p.next());
      assertTrue(p.hasNext());
      assertEquals(-2, p.next());
      assertTrue(p.hasNext());
      assertEquals(-1, p.next());
      assertTrue(p.hasNext());
      assertEquals(0, p.next());
      assertTrue(p.hasNext());
      assertEquals(1, p.next());
      assertTrue(p.hasNext());
      assertEquals(2, p.next());
      assertTrue(p.hasNext());
      assertEquals(3, p.next());
      assertFalse(p.hasNext());
   }
}
