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

final class MathUtils {
   private MathUtils() {
   }

   static long safeAdd(long x, long y) {
      long r = x + y;
      if (((x ^ r) & (y ^ r)) < 0) {
         return y > 0 ? Long.MAX_VALUE : Long.MIN_VALUE;
      }
      return r;
   }

   static long safeSubtract(long x, long y) {
      long r = x - y;
      if (((x ^ y) & (x ^ r)) < 0) {
         return y < 0 ? Long.MAX_VALUE : Long.MIN_VALUE;
      }
      return r;
   }

   static long safeMultiply(long x, long y) {
      long r = x * y;
      long ax = Math.abs(x);
      long ay = Math.abs(y);
      if ((ax | ay) >>> 31 != 0 && ((y != 0 && r / y != x) || (x == Long.MIN_VALUE && y == -1))) {
         return x < 0 == y < 0 ? Long.MAX_VALUE : Long.MIN_VALUE;
      }
      return r;
   }
}
