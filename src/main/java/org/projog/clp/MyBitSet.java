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

import java.util.BitSet;

final class MyBitSet {
   final long start;
   final BitSet bitset;

   MyBitSet copy() {
      return new MyBitSet(start, (BitSet) bitset.clone());
   }

   private MyBitSet(long start, BitSet bitset) {
      this.start = start;
      // TODO check for overflow
      this.bitset = bitset;
   }

   MyBitSet(long start, long end) {
      this.start = start;
      // TODO check for overflow
      this.bitset = new BitSet((int) (end - start));
   }

   boolean get(long index) {
      return bitset.get((int) (index - start));
   }

   void clear(long fromIndex, long toIndex) {
      bitset.clear((int) (fromIndex - start), (int) (toIndex - start));
   }

   long nextSetBit(long index) {
      return bitset.nextSetBit((int) (index - start)) + start;
   }

   int cardinality() {
      return bitset.cardinality();
   }

   void set(long fromIndex, long toIndex) {
      bitset.set((int) (fromIndex - start), (int) (toIndex - start));
   }

   void clear(long not) {
      bitset.clear((int) (not - start));
   }

   long previousSetBit(long index) {
      return bitset.previousSetBit((int) (index - start)) + start;
   }
}
