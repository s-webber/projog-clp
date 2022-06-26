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

/** Represents the possible values for a variable. */
public final class VariableState {
   /**
    * Used to indicate a {@code VariableState} has become corrupt.
    * <p>
    * If {@code bitset} is assigned to {@code CORRUPT} then that indicates the {@code VariableState} has become corrupt
    * and should no longer be used.
    * </p>
    * <p>
    * For example, a {@code VariableState} would become corrupt if it had a minimum value of 7 and an attempt was made
    * to set its maximum value to 6.
    */
   private static final NumberSet CORRUPT = new NumberSet(0, 1);

   /** The minimum possible value for this {@code VariableState}. */
   private long min;
   /** The maximum possible value for this {@code VariableState}. */
   private long max;
   /**
    * The set of all possible values for this {@code VariableState}.
    * <p>
    * If {@code null} then indicates that all numbers in the range {@code min} to {@code max} (inclusive) are possible
    * values for this {@code VariableState}.
    * </p>
    * <p>
    * If assigned to {@link #CORRUPT} then indicates that this {@code VariableState} has become corrupt and should no
    * longer be used.
    */
   private NumberSet bitset;

   /** Creates a {@code VariableState} with the full range of possible values. */
   public VariableState() {
      this.min = Long.MIN_VALUE;
      this.max = Long.MAX_VALUE;
   }

   private VariableState(long min, long max, NumberSet bitset) {
      this.min = min;
      this.max = max;
      this.bitset = bitset;
   }

   private VariableState(VariableState original) {
      this.min = original.min;
      this.max = original.max;
      if (original.bitset != null) {
         this.bitset = original.bitset.copy();
      }
   }

   /**
    * Return the result of performing a logical "and" on the two given {@code VariableState}s.
    *
    * @return a state representing a logical "and" of {@code a} and {@code b}, or {@code null} if no values shared
    * between them
    */
   public static VariableState and(VariableState a, VariableState b) {
      if (a == b) {
         return a;
      }

      long newMin = min(a, b);
      long newMax = max(a, b);

      if (newMin > newMax) {
         return null;
      } else if ((a.bitset == null && b.bitset == null) || newMin == newMax) {
         if (a.min == newMin && a.max == newMax) {
            return a;
         } else if (b.min == newMin && b.max == newMax) {
            return b;
         } else {
            return new VariableState(newMin, newMax, null);
         }
      } else if (a.bitset == null) {
         return squashVariableState(b, newMin, newMax);
      } else if (b.bitset == null) {
         return squashVariableState(a, newMin, newMax);
      } else if (a.bitset.equals(b.bitset)) {
         return a;
      } else {
         NumberSet newBitSet;
         NumberSet bitSetToMerge;
         if (a.bitset.cardinality() > b.bitset.cardinality()) {
            newBitSet = squashBitSet(b, newMin, newMax);
            bitSetToMerge = a.bitset;
         } else {
            newBitSet = squashBitSet(a, newMin, newMax);
            bitSetToMerge = b.bitset;
         }
         for (long i = newMin; i <= newMax; i++) {
            i = newBitSet.nextSetValue(i);
            if (i < newMin) {
               break;
            }
            if (!bitSetToMerge.get(i)) {
               newBitSet.clear(i);
            }
         }
         return new VariableState(newMin, newMax, newBitSet);
      }
   }

   private static long min(VariableState a, VariableState b) {
      if (a.min == b.min) {
         return a.min;
      }

      long newMin = Math.max(a.min, b.min);
      if (a.bitset != null && b.bitset != null) {
         long n1;
         long n2;
         do {
            n1 = a.bitset.nextSetValue(newMin);
            if (n1 < a.min || n1 < b.min || n1 > a.max) {
               return Long.MAX_VALUE;
            }
            newMin = Math.max(n1, newMin);
            n2 = b.bitset.nextSetValue(newMin);
            if (n2 < a.min || n2 < b.min || n1 > b.max) {
               return Long.MAX_VALUE;
            }
            newMin = Math.max(n2, newMin);
         } while (n1 != n2);
      } else if (a.bitset != null) {
         long n = a.bitset.nextSetValue(newMin);
         if (n < newMin) {
            return Long.MAX_VALUE;
         } else if (n > newMin) {
            newMin = n;
         }
      } else if (b.bitset != null) {
         long n = b.bitset.nextSetValue(newMin);
         if (n < newMin) {
            return Long.MAX_VALUE;
         } else if (n > newMin) {
            newMin = n;
         }
      }
      return newMin;
   }

   private static long max(VariableState a, VariableState b) {
      if (a.max == b.max) {
         return a.max;
      }

      long newMax = Math.min(a.max, b.max);
      if (a.bitset != null && b.bitset != null) {
         long n1;
         long n2;
         do {
            n1 = a.bitset.previousSetValue(newMax);
            if (n1 < a.min || n1 < b.min) {
               return Long.MIN_VALUE;
            }
            newMax = Math.min(n1, newMax);
            n2 = b.bitset.previousSetValue(newMax);
            if (n2 < a.min || n2 < b.min) {
               return Long.MIN_VALUE;
            }
            newMax = Math.min(n2, newMax);
         } while (n1 != n2);
      } else if (a.bitset != null) {
         long n = a.bitset.previousSetValue(newMax);
         if (n < a.min) {
            return Long.MIN_VALUE;
         } else if (n < newMax) {
            newMax = n;
         }
      } else if (b.bitset != null) {
         long n = b.bitset.previousSetValue(newMax);
         if (n < b.min) {
            return Long.MIN_VALUE;
         } else if (n < newMax) {
            newMax = n;
         }
      }
      return newMax;
   }

   private static VariableState squashVariableState(VariableState s, long newMin, long newMax) {
      if (newMin == s.min && newMax == s.max) {
         return s;
      }

      NumberSet newBitSet = squashBitSet(s, newMin, newMax);
      return new VariableState(newMin, newMax, newBitSet);
   }

   private static NumberSet squashBitSet(VariableState s, long newMin, long newMax) {
      NumberSet newBitSet = s.bitset.copy();
      if (s.min < newMin) {
         newBitSet.clear(s.min, newMin - 1);
      }
      if (s.max > newMax) {
         newBitSet.clear(newMax + 1, s.max);
      }
      return newBitSet;
   }

   public long getMin() {
      validate();
      return min;
   }

   public long getMax() {
      validate();
      return max;
   }

   public VariableStateResult setValue(long value) {
      validate();
      if (value == min && value == max) {
         return VariableStateResult.NO_CHANGE;
      }
      if (value < min || value > max) {
         return fail();
      }

      if (bitset != null) {
         if (!bitset.get(value)) {
            return fail();
         }
         bitset = null;
      }
      this.min = value;
      this.max = value;
      return VariableStateResult.UPDATED;
   }

   public VariableStateResult setMin(long min) {
      validate();
      if (min <= this.min) {
         return VariableStateResult.NO_CHANGE;
      }
      if (min > this.max) {
         return fail();
      }

      if (bitset != null) {
         bitset.clear(this.min, min - 1); // .clear(int,int) is *inclusive*
         min = bitset.nextSetValue(min);
         if (bitset.cardinality() == 0) {
            throw new IllegalStateException(); // should never happen
         }
      }
      this.min = min;
      if (min == max) {
         bitset = null;
      }
      return VariableStateResult.UPDATED;
   }

   public VariableStateResult setMax(long max) {
      validate();
      if (max >= this.max) {
         return VariableStateResult.NO_CHANGE;
      }
      if (max < this.min) {
         return fail();
      }

      if (bitset != null) {
         bitset.clear(max + 1, this.max); // .clear(int,int) is *inclusive*
         max = bitset.previousSetValue(max);
         if (bitset.cardinality() == 0) {
            throw new IllegalStateException(); // should never happen
         }
      }
      this.max = max;
      if (min == max) {
         bitset = null;
      }
      return VariableStateResult.UPDATED;
   }

   public VariableStateResult setNot(long not) {
      validate();
      if (not < min || not > max) {
         return VariableStateResult.NO_CHANGE;
      }
      if (min == not && max == not) {
         return fail();
      }
      if (bitset == null) {
         if (MathUtils.safeSubtract(max, min) >= Integer.MAX_VALUE) {
            return VariableStateResult.NO_CHANGE;
         }
         bitset = new NumberSet(min, max);
      }
      if (bitset.get(not)) {
         if (bitset.cardinality() == 1) {
            throw new IllegalStateException(); // should never happen
         }

         bitset.clear(not);
         if (min == not) {
            min = bitset.nextSetValue(min + 1);
         }
         if (max == not) {
            max = bitset.previousSetValue(max - 1);
         }
         if (min == max) {
            bitset = null;
         }
         return VariableStateResult.UPDATED;
      } else {
         return VariableStateResult.NO_CHANGE;
      }
   }

   public VariableState copy() {
      validate();
      return new VariableState(this);
   }

   public long count() {
      validate();
      if (bitset != null) {
         return bitset.cardinality();
      } else {
         return MathUtils.safeAdd(MathUtils.safeSubtract(max, min), 1);
      }
   }

   public Possibilities getPossibilities() {
      validate();
      return new Possibilities(min, max, bitset);
   }

   private VariableStateResult fail() {
      bitset = CORRUPT;
      return VariableStateResult.FAILED;
   }

   private void validate() {
      if (bitset == CORRUPT) {
         throw new IllegalStateException();
      }
   }

   public boolean isSingleValue() {
      validate();
      return min == max;
   }

   @Override
   public String toString() {
      if (bitset == CORRUPT) {
         return "corrupt";
      } else if (isSingleValue()) {
         return Long.toString(min);
      } else if (bitset == null || bitset.cardinality() == max - min + 1) {
         return min + ".." + max;
      } else {
         return bitset.toString();
      }
   }
}
