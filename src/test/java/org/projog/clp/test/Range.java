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
package org.projog.clp.test;

public final class Range {
   private final long min;
   private final long max;

   public Range(long min, long max) {
      if (max < min) {
         throw new IllegalArgumentException(max + " < " + min);
      }
      this.min = min;
      this.max = max;
   }

   public long min() {
      return min;
   }

   public long max() {
      return max;
   }

   @Override
   public boolean equals(Object o) {
      if (o instanceof Range) {
         Range r = (Range) o;
         return min == r.min && max == r.max;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Long.hashCode(min) * Long.hashCode(max);
   }

   @Override
   public String toString() {
      return "Range [min=" + min + ", max=" + max + "]";
   }
}
