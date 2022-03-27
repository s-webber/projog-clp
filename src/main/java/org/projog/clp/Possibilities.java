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

/** Used to iterate over a range of possible values. */
final class Possibilities {
   private final long min;
   private final long max;
   private final NumberSet bitset;
   private long next;

   Possibilities(long min, long max, NumberSet bitset) {
      if (min > max) {
         throw new IllegalStateException();
      }
      this.min = min;
      this.max = max;
      this.bitset = bitset;
      this.next = min;
   }

   boolean hasNext() {
      return next >= min && next <= max;
   }

   long next() {
      long c = next;
      if (bitset == null) {
         next++;
      } else {
         if (next < min) {
            throw new IllegalStateException();
         }
         next = bitset.nextSetValue(next + 1);
      }
      return c;
   }
}
