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

/** A set of {@code long} primitives. */
final class NumberSet {
   /** The value that the first bit of the bitset represents. */
   private final long start;
   private final BitSet bitset;

   /**
    * Constructs a new {@code NumberSet} containing all values within the specified range (inclusive).
    *
    * @param start the minimum value to be stored (inclusive)
    * @param end the maximum value to be stored (inclusive)
    */
   NumberSet(long start, long end) {
      this.start = start;
      if (start >= end) {
         throw new IllegalArgumentException(start + ">=" + end);
      }
      long size = end - start + 1;
      if (size > Integer.MAX_VALUE) {
         throw new IllegalArgumentException(end + "-" + start + "+1=" + size + ">" + Integer.MAX_VALUE);
      }
      this.bitset = new BitSet((int) size);
      this.bitset.set(0, (int) size);
   }

   private NumberSet(long start, BitSet bitset) {
      this.start = start;
      this.bitset = bitset;
   }

   /** Returns a new copy of this {@code NumberSet}. */
   NumberSet copy() {
      return new NumberSet(start, (BitSet) bitset.clone());
   }

   /** Returns {@code true} if the given value is set, else {@code false}. */
   boolean get(long value) {
      return bitset.get((int) (value - start));
   }

   /**
    * Unsets all values between the given {@code fromValue} (inclusive) and {@code toValue} (inclusive).
    * <p>
    * NOTE: Unlike java.util.BitSet.clear(int, int) both values are *inclusive*.
    */
   void clear(long fromValue, long toValue) {
      bitset.clear((int) (fromValue - start), (int) (toValue - start + 1));
   }

   /** Unsets (i.e. remove from this {@code NumberSet}) the given value. */
   void clear(long not) {
      bitset.clear((int) (not - start));
   }

   /**
    * Returns the value that occurs on or after the specified {@code value}.
    *
    * @param value fromIndex the index to start checking from (inclusive)
    * @return the value that occurs on or after the specified value, or {@code start - 1} is no such value
    */
   long nextSetValue(long value) {
      return bitset.nextSetBit((int) (value - start)) + start;
   }

   /**
    * Returns the value that occurs on or before the specified {@code value}.
    *
    * @param value fromIndex the index to start checking from (inclusive)
    * @return the value that occurs on or after the specified value, or {@code start - 1} is no such value
    */
   long previousSetValue(long value) {
      return bitset.previousSetBit((int) (value - start)) + start;
   }

   /** Returns the count of values set in this {@code NumberSet}. */
   int cardinality() {
      return bitset.cardinality();
   }

   /** Returns all values contained in this set, in order, separated by commas and surrounded by braces. */
   @Override
   public String toString() {
      // based on version in java.util.BitSet, but appends "start + i" rather than "i"
      StringBuilder b = new StringBuilder();
      b.append('{');

      int i = bitset.nextSetBit(0);
      if (i != -1) {
         b.append(start + i);
         while (true) {
            if (++i < 0) {
               break;
            }
            if ((i = bitset.nextSetBit(i)) < 0) {
               break;
            }
            int endOfRun = bitset.nextClearBit(i);
            do {
               b.append(", ").append(start + i);
            } while (++i != endOfRun);
         }
      }

      b.append('}');
      return b.toString();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof NumberSet)) {
         return false;
      } else {
         NumberSet other = (NumberSet) o;
         return start == other.start && bitset.equals(other.bitset);
      }
   }

   @Override
   public int hashCode() {
      return bitset.hashCode();
   }
}
