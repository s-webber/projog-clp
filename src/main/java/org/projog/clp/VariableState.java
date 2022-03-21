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

public final class VariableState {
   private static final MyBitSet CORRUPT = new MyBitSet(0, 1);

   private long min;
   private long max;
   private MyBitSet bitset;

   public VariableState() {
      min = Long.MIN_VALUE;
      max = Long.MAX_VALUE;
   }

   private VariableState(VariableState original) {
      this.min = original.min;
      this.max = original.max;
      if (original.bitset != null) {
         this.bitset = original.bitset.copy();
      }
   }

   public long getMin() {
      validate();
      return min;
   }

   public long getMax() {
      validate();
      return max;
   }

   public ExpressionResult setValue(long value) {
      validate();
      if (value == min && value == max) {
         return ExpressionResult.NO_CHANGE;
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
      return ExpressionResult.UPDATED;
   }

   public ExpressionResult setMin(long min) {
      validate();
      if (min <= this.min) {
         return ExpressionResult.NO_CHANGE;
      }
      if (min > this.max) {
         return fail();
      }

      if (bitset != null) {
         bitset.clear(this.min, min);
         min = bitset.nextSetBit(min);
         if (bitset.cardinality() == 0) {
            return fail();
         }
      }
      this.min = min;
      if (min == max) {
         bitset = null;
      }
      return ExpressionResult.UPDATED;
   }

   public ExpressionResult setMax(long max) {
      validate();
      if (max >= this.max) {
         return ExpressionResult.NO_CHANGE;
      }
      if (max < this.min) {
         return fail();
      }

      if (bitset != null) {
         bitset.clear(max + 1, this.max + 1);
         max = bitset.previousSetBit(max);
         if (bitset.cardinality() == 0) {
            return fail();
         }
      }
      this.max = max;
      if (min == max) {
         bitset = null;
      }
      return ExpressionResult.UPDATED;
   }

   public ExpressionResult setNot(long not) {
      validate();
      if (not < min || not > max) {
         return ExpressionResult.NO_CHANGE;
      }
      // TODO if not ==max or ==min and bitset==null then alter min/max rather than create new bitset
      if (bitset == null) {
         bitset = new MyBitSet(min, max);
         bitset.set(min, max + 1);
      }
      if (bitset.get(not)) {
         if (bitset.cardinality() == 1) {
            return fail();
         }

         bitset.clear(not);
         if (min == not) {
            min = bitset.nextSetBit(min + 1);
         }
         if (max == not) {
            max = bitset.previousSetBit(max - 1);
         }
         if (min == max) {
            bitset = null;
         }
         return ExpressionResult.UPDATED;
      } else {
         return ExpressionResult.NO_CHANGE;
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
         return MathUtils.safeSubtract(max, min);
      }
   }

   public Possibilities getPossibilities() {
      validate();
      return new Possibilities(min, max, bitset);
   }

   private ExpressionResult fail() {
      bitset = CORRUPT;
      return ExpressionResult.FAILED;
   }

   private void validate() {
      if (bitset == CORRUPT) {
         throw new IllegalStateException();
      }
   }

   public boolean isSingleValue() { // TODO use in this class instead of min == max
      validate();
      return min == max;
   }

   @Override
   public String toString() {
      return isSingleValue() ? Long.toString(min) : (min + "-" + max + " " + bitset);
   }
}
