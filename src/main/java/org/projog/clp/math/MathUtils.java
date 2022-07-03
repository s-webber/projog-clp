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
package org.projog.clp.math;

/**
 * Contains methods for performing basic numeric operations which handle overflow errors by rounding down to
 * {@link Long.MAX_VALUE} or up to {@link Long.MIN_VALUE}.
 */
public final class MathUtils {
   private MathUtils() {
   }

   /**
    * Returns the sum of the arguments, rounding up or down if the result overflows a long.
    * <p>
    * e.g.:<br>
    * {@code safeAdd(Long.MAX_Value, 1)} returns {@link Long.MAX_VALUE}.<br>
    * {@code safeAdd(Long.MIN_Value, -1)} returns {@link Long.MIN_VALUE}.
    *
    * @see java.lang.Math#addExact(long, long)
    */
   public static long safeAdd(long x, long y) {
      long r = x + y;
      if (((x ^ r) & (y ^ r)) < 0) {
         return y > 0 ? Long.MAX_VALUE : Long.MIN_VALUE;
      }
      return r;
   }

   /**
    * Returns the difference of the arguments, rounding up or down if the result overflows a long.
    * <p>
    * e.g.:<br>
    * {@code safeSubtract(Long.MAX_Value, -1)} returns {@link Long.MAX_VALUE}.<br>
    * {@code safeSubtract(Long.MIN_Value, 1)} returns {@link Long.MIN_VALUE}.
    *
    * @see java.lang.Math#subtractExact(long, long)
    */
   public static long safeSubtract(long x, long y) {
      long r = x - y;
      if (((x ^ y) & (x ^ r)) < 0) {
         return y < 0 ? Long.MAX_VALUE : Long.MIN_VALUE;
      }
      return r;
   }

   /**
    * Returns the product of the arguments, rounding up or down if the result overflows a long.
    * <p>
    * e.g.:<br>
    * {@code safeMultiply(Long.MAX_Value, 1)} returns {@link Long.MAX_VALUE}.<br>
    * {@code safeMultiply(Long.MIN_Value, 1)} returns {@link Long.MIN_VALUE}.
    *
    * @see java.lang.Math#multiplyExact(long, long)
    */
   public static long safeMultiply(long x, long y) {
      long r = x * y;
      long ax = Math.abs(x);
      long ay = Math.abs(y);
      if ((ax | ay) >>> 31 != 0 && ((y != 0 && r / y != x) || (x == Long.MIN_VALUE && y == -1))) {
         return x < 0 == y < 0 ? Long.MAX_VALUE : Long.MIN_VALUE;
      }
      return r;
   }

   /**
    * Returns the absolute value of a {@code long} value.
    * <p>
    * {@code safeAbs(Long.MIN_VALUE)} returns {@link Long.MAX_VALUE} whereas {@code Math.abs(Long.MIN_Value)} returns
    * {@link Long.MIN_VALUE}.
    *
    * @see java.lang.Math#abs(long)
    */
   public static long safeAbs(long v) {
      return v == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(v);
   }

   public static long safeMinus(long v) {
      if (v == Long.MIN_VALUE) {
         return Long.MAX_VALUE;
      }
      return -v;
   }
}
