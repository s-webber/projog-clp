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

import static org.projog.clp.MathUtils.safeAdd;
import static org.projog.clp.MathUtils.safeMultiply;
import static org.projog.clp.MathUtils.safeSubtract;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/** The product of two {@code Expression}s. */
public final class Multiply implements Expression {
   private final Expression left;
   private final Expression right;

   public Multiply(Expression left, Expression right) {
      this.left = Objects.requireNonNull(left);
      this.right = Objects.requireNonNull(right);
   }

   @Override
   public long getMin(ReadConstraintStore s) {
      long leftMin = left.getMin(s);
      long leftMax = left.getMax(s);
      long rightMin = right.getMin(s);
      long rightMax = right.getMax(s);
      long r = safeMultiply(leftMin, rightMin);
      long r1 = safeMultiply(leftMin, rightMax);
      if (r1 < r) {
         r = r1;
      }
      r1 = safeMultiply(leftMax, rightMin);
      if (r1 < r) {
         r = r1;
      }
      r1 = safeMultiply(leftMax, rightMax);
      if (r1 < r) {
         r = r1;
      }
      return r;
   }

   @Override
   public long getMax(ReadConstraintStore s) {
      long leftMin = left.getMin(s);
      long leftMax = left.getMax(s);
      long rightMin = right.getMin(s);
      long rightMax = right.getMax(s);
      long r = safeMultiply(leftMin, rightMin);
      long r1 = safeMultiply(leftMin, rightMax);
      if (r1 > r) {
         r = r1;
      }
      r1 = safeMultiply(leftMax, rightMin);
      if (r1 > r) {
         r = r1;
      }
      r1 = safeMultiply(leftMax, rightMax);
      if (r1 > r) {
         r = r1;
      }
      return r;
   }

   @Override
   public ExpressionResult setMin(ConstraintStore s, long min) {
      long leftMin = left.getMin(s);
      long leftMax = left.getMax(s);
      long rightMin = right.getMin(s);
      long rightMax = right.getMax(s);

      if (min > 0) {
         // For result to be >0 both args must be of the same sign (+ve/-ve).
         if (leftMin > -1 && rightMax < 1) { // if not the same sign
            return ExpressionResult.FAILED;
         }
         if (rightMin > -1 && leftMax < 1) { // if not the same sign
            return ExpressionResult.FAILED;
         }
         // neither arg can be zero
         if (leftMin == 0) {
            ExpressionResult r = left.setMin(s, 1);
            if (r != ExpressionResult.NO_CHANGE) {
               return r;
            }
         }
         if (leftMax == 0) {
            ExpressionResult r = left.setMax(s, -1);
            if (r != ExpressionResult.NO_CHANGE) {
               return r;
            }
         }
         if (rightMin == 0) {
            ExpressionResult r = right.setMin(s, 1);
            if (r != ExpressionResult.NO_CHANGE) {
               return r;
            }
         }
         if (rightMax == 0) {
            ExpressionResult r = right.setMax(s, -1);
            if (r != ExpressionResult.NO_CHANGE) {
               return r;
            }
         }
         // must be same sign
         if (leftMin > 0 && rightMin < 1) {
            ExpressionResult r = right.setMin(s, 1);
            if (r != ExpressionResult.NO_CHANGE) {
               return r;
            }
         }
         if (rightMin > 0 && leftMin < 1) {
            ExpressionResult r = left.setMin(s, 1);
            if (r != ExpressionResult.NO_CHANGE) {
               return r;
            }
         }
         if (leftMax < 0 && rightMax > -1) {
            ExpressionResult r = right.setMax(s, -1);
            if (r != ExpressionResult.NO_CHANGE) {
               return r;
            }
         }
         if (rightMax < 0 && leftMax > -1) {
            ExpressionResult r = left.setMax(s, -1);
            if (r != ExpressionResult.NO_CHANGE) {
               return r;
            }
         }
      }

      if (min == 0) {
         if (leftMin > 0) {
            ExpressionResult r = right.setMin(s, 0);
            if (r != ExpressionResult.NO_CHANGE) {
               return r;
            }
         }
         if (rightMin > 0) {
            ExpressionResult r = left.setMin(s, 0);
            if (r != ExpressionResult.NO_CHANGE) {
               return r;
            }
         }
         if (leftMax < 0) {
            ExpressionResult r = right.setMax(s, 0);
            if (r != ExpressionResult.NO_CHANGE) {
               return r;
            }
         }
         if (rightMax < 0) {
            ExpressionResult r = left.setMax(s, 0);
            if (r != ExpressionResult.NO_CHANGE) {
               return r;
            }
         }
      }

      if (leftMax < 0 && rightMax < 0) {
         ExpressionResult r1 = setMax(s, left, min, leftMax, rightMin, rightMax, Multiply::divideWhole);
         if (r1 == ExpressionResult.FAILED) {
            return ExpressionResult.FAILED;
         }

         ExpressionResult r2 = setMax(s, right, min, rightMax, leftMin, leftMax, Multiply::divideWhole);
         if (r2 == ExpressionResult.FAILED) {
            return ExpressionResult.FAILED;
         }

         return r1 == ExpressionResult.UPDATED || r2 == ExpressionResult.UPDATED ? ExpressionResult.UPDATED : ExpressionResult.NO_CHANGE;
      } else if (leftMin > 0 && rightMin > 0) {
         ExpressionResult r1 = setMin(s, left, min, leftMin, rightMin, rightMax, Multiply::divideWhole);
         if (r1 == ExpressionResult.FAILED) {
            return ExpressionResult.FAILED;
         }

         ExpressionResult r2 = setMin(s, right, min, rightMin, leftMin, leftMax, Multiply::divideWhole);
         if (r2 == ExpressionResult.FAILED) {
            return ExpressionResult.FAILED;
         }

         return r1 == ExpressionResult.UPDATED || r2 == ExpressionResult.UPDATED ? ExpressionResult.UPDATED : ExpressionResult.NO_CHANGE;
      } else {
         return ExpressionResult.NO_CHANGE;
      }
   }

   @Override
   public ExpressionResult setMax(ConstraintStore s, long max) {
      long leftMin = left.getMin(s);
      long leftMax = left.getMax(s);
      long rightMin = right.getMin(s);
      long rightMax = right.getMax(s);

      if (max < 0) {
         // For result to be <0 args must be of different sign (+ve/-ve).
         if (leftMin > -1 && rightMin > -1) { // if not the same sign
            return ExpressionResult.FAILED;
         }
         if (leftMax < 1 && rightMax < 1) { // if not the same sign
            return ExpressionResult.FAILED;
         }
         // neither arg can be zero
         if (leftMin == 0) {
            ExpressionResult r = left.setMin(s, 1);
            if (r != ExpressionResult.NO_CHANGE) {
               return r;
            }
         }
         if (leftMax == 0) {
            ExpressionResult r = left.setMax(s, -1);
            if (r != ExpressionResult.NO_CHANGE) {
               return r;
            }
         }
         if (rightMin == 0) {
            ExpressionResult r = right.setMin(s, 1);
            if (r != ExpressionResult.NO_CHANGE) {
               return r;
            }
         }
         if (rightMax == 0) {
            ExpressionResult r = right.setMax(s, -1);
            if (r != ExpressionResult.NO_CHANGE) {
               return r;
            }
         }
         // cannot be same sign
         if (leftMin > 0 && rightMax > -1) {
            ExpressionResult r = right.setMax(s, -1);
            if (r != ExpressionResult.NO_CHANGE) {
               return r;
            }
         }
         if (rightMin > 0 && leftMax > -1) {
            ExpressionResult r = left.setMax(s, -1);
            if (r != ExpressionResult.NO_CHANGE) {
               return r;
            }
         }
         if (leftMax < 0 && rightMin < 1) {
            ExpressionResult r = right.setMin(s, 1);
            if (r != ExpressionResult.NO_CHANGE) {
               return r;
            }
         }
         if (rightMax < 0 && leftMin < 1) {
            ExpressionResult r = left.setMin(s, 1);
            if (r != ExpressionResult.NO_CHANGE) {
               return r;
            }
         }
      }

      if (max == 0) {
         if (leftMin > 0) {
            ExpressionResult r = right.setMax(s, 0);
            if (r != ExpressionResult.NO_CHANGE) {
               return r;
            }
         }
         if (rightMin > 0) {
            ExpressionResult r = left.setMax(s, 0);
            if (r != ExpressionResult.NO_CHANGE) {
               return r;
            }
         }
         if (leftMax < 0) {
            ExpressionResult r = right.setMin(s, 0);
            if (r != ExpressionResult.NO_CHANGE) {
               return r;
            }
         }
         if (rightMax < 0) {
            ExpressionResult r = left.setMin(s, 0);
            if (r != ExpressionResult.NO_CHANGE) {
               return r;
            }
         }
      }

      if (leftMax < 0 && rightMax < 0) {
         ExpressionResult r1 = setMin(s, left, max, leftMin, rightMin, rightMax, Multiply::divideRoundDown);
         if (r1 == ExpressionResult.FAILED) {
            return ExpressionResult.FAILED;
         }

         ExpressionResult r2 = setMin(s, right, max, rightMin, leftMin, leftMax, Multiply::divideRoundDown);
         if (r2 == ExpressionResult.FAILED) {
            return ExpressionResult.FAILED;
         }

         return r1 == ExpressionResult.UPDATED || r2 == ExpressionResult.UPDATED ? ExpressionResult.UPDATED : ExpressionResult.NO_CHANGE;
      } else if (leftMin > 0 && rightMin > 0) {
         ExpressionResult r1 = setMax(s, left, max, leftMax, rightMin, rightMax, Multiply::divideRoundDown);
         if (r1 == ExpressionResult.FAILED) {
            return ExpressionResult.FAILED;
         }

         ExpressionResult r2 = setMax(s, right, max, rightMax, leftMin, leftMax, Multiply::divideRoundDown);
         if (r2 == ExpressionResult.FAILED) {
            return ExpressionResult.FAILED;
         }

         return r1 == ExpressionResult.UPDATED || r2 == ExpressionResult.UPDATED ? ExpressionResult.UPDATED : ExpressionResult.NO_CHANGE;
      } else {
         return ExpressionResult.NO_CHANGE;
      }
   }

   private static ExpressionResult setMax(ConstraintStore s, Expression left, long max, long leftMax, long rightMin, long rightMax, BiFunction<Long, Long, Long> f) {
      long newMaxLeft = Math.max(f.apply(max, rightMin), f.apply(max, rightMax));
      return newMaxLeft < leftMax ? left.setMax(s, newMaxLeft) : ExpressionResult.NO_CHANGE;
   }

   private static ExpressionResult setMin(ConstraintStore s, Expression left, long min, long leftMin, long rightMin, long rightMax, BiFunction<Long, Long, Long> f) {
      long newMinLeft = Math.min(f.apply(min, rightMin), f.apply(min, rightMax));
      return newMinLeft > leftMin ? left.setMin(s, newMinLeft) : ExpressionResult.NO_CHANGE;
   }

   private static long divideRoundDown(long dividend, long divisor) {
      return dividend / divisor;
   }

   private static long divideWhole(long dividend, long divisor) {
      long result = dividend / divisor;
      if (divisor * result == dividend) {
         return result;
      } else if (divisor < 0) {
         return result - 1;
      } else {
         return result + 1;
      }
   }

   @Override
   public ExpressionResult setNot(ConstraintStore s, long not) {
      if (getMax(s) == not) {
         return setMax(s, safeSubtract(not, 1));
      } else if (getMin(s) == not) {
         return setMin(s, safeAdd(not, 1));
      } else {
         return ExpressionResult.NO_CHANGE;
      }
   }

   @Override
   public void walk(Consumer<Expression> r) {
      r.accept(this);
      left.walk(r);
      right.walk(r);
   }

   @Override
   public Multiply replaceVariables(Function<Variable, Variable> r) {
      return new Multiply(left.replaceVariables(r), right.replaceVariables(r));
   }

   @Override
   public String toString() {
      return "Multiply [left=" + left + ", right=" + right + "]";
   }
}
