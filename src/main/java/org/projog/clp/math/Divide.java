/*
 * Copyright 2023 S. Webber
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

import static org.projog.clp.math.MathUtils.safeAdd;
import static org.projog.clp.math.MathUtils.safeDivide;
import static org.projog.clp.math.MathUtils.safeMultiply;
import static org.projog.clp.math.MathUtils.safeSubtract;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import org.projog.clp.ConstraintStore;
import org.projog.clp.Expression;
import org.projog.clp.ExpressionResult;
import org.projog.clp.LeafExpression;
import org.projog.clp.ReadConstraintStore;

/** The result of dividing one {@code Expression} by another. */
public final class Divide implements Expression {
   /** The dividend. What is being divided. */
   private final Expression left;
   /** The divisor. What the divided is being divided by. */
   private final Expression right;

   public Divide(Expression left, Expression right) {
      this.left = Objects.requireNonNull(left);
      this.right = Objects.requireNonNull(right);
   }

   @Override
   public long getMin(ReadConstraintStore s) {
      long leftMin = left.getMin(s);
      long leftMax = left.getMax(s);
      long rightMin = right.getMin(s);
      long rightMax = right.getMax(s);

      if (rightMin == 0 || rightMax == 0) {
         return Long.MIN_VALUE;
      }

      long r = safeDivide(leftMin, rightMin);
      long r1 = safeDivide(leftMin, rightMax);
      if (r1 < r) {
         r = r1;
      }
      r1 = safeDivide(leftMax, rightMin);
      if (r1 < r) {
         r = r1;
      }
      r1 = safeDivide(leftMax, rightMax);
      if (r1 < r) {
         r = r1;
      }
      if (rightMin < 0 && rightMax > 0) {
         r1 = safeDivide(leftMin, 1);
         if (r1 < r) {
            r = r1;
         }
         r1 = safeDivide(leftMin, -1);
         if (r1 < r) {
            r = r1;
         }
         r1 = safeDivide(leftMax, -1);
         if (r1 < r) {
            r = r1;
         }
      }

      return r;
   }

   @Override
   public long getMax(ReadConstraintStore s) {
      long leftMin = left.getMin(s);
      long leftMax = left.getMax(s);
      long rightMin = right.getMin(s);
      long rightMax = right.getMax(s);

      if (rightMin == 0 || rightMax == 0) {
         return Long.MAX_VALUE;
      }

      long r = safeDivide(leftMin, rightMin);
      long r1 = safeDivide(leftMin, rightMax);
      if (r1 > r) {
         r = r1;
      }
      r1 = safeDivide(leftMax, rightMin);
      if (r1 > r) {
         r = r1;
      }
      r1 = safeDivide(leftMax, rightMax);
      if (r1 > r) {
         r = r1;
      }
      if (rightMin < 0 && rightMax > 0) {
         r1 = safeDivide(leftMin, 1);
         if (r1 > r) {
            r = r1;
         }
         r1 = safeDivide(leftMax, 1);
         if (r1 > r) {
            r = r1;
         }
         r1 = safeDivide(leftMin, -1);
         if (r1 > r) {
            r = r1;
         }
      }

      return r;
   }

   @Override
   public ExpressionResult setMin(ConstraintStore s, long min) {
      if (right.setNot(s, 0) == ExpressionResult.INVALID) {
         return ExpressionResult.INVALID;
      }

      boolean leftNegative = left.getMax(s) < 0;
      boolean leftPositive = left.getMin(s) > 0;
      boolean rightNegative = right.getMax(s) < 0;
      boolean rightPositive = right.getMin(s) > 0;
      if (min == 0 && leftNegative && rightPositive) {
         if (left.setMin(s, -(right.getMax(s) - 1)) == ExpressionResult.INVALID) {
            return ExpressionResult.INVALID;
         }
         if (right.setMin(s, -(left.getMax(s) - 1)) == ExpressionResult.INVALID) {
            return ExpressionResult.INVALID;
         }
      } else if (min == 0 && leftPositive && rightNegative) {
         if (left.setMax(s, -(right.getMin(s) + 1)) == ExpressionResult.INVALID) {
            return ExpressionResult.INVALID;
         }
         if (right.setMax(s, -(left.getMin(s) + 1)) == ExpressionResult.INVALID) {
            return ExpressionResult.INVALID;
         }
      } else if (leftPositive && rightPositive && min > 0) {
         if (left.setMin(s, min * right.getMin(s)) == ExpressionResult.INVALID) {
            return ExpressionResult.INVALID;
         }
         if (right.setMax(s, left.getMax(s) / min) == ExpressionResult.INVALID) {
            return ExpressionResult.INVALID;
         }
      } else if (leftNegative && rightNegative && min > 0) {
         if (right.setMin(s, -calcRightMin(-left.getMin(s), min)) == ExpressionResult.INVALID) {
            return ExpressionResult.INVALID;
         }

         long r3 = min * right.getMax(s);
         if (left.setMax(s, r3) == ExpressionResult.INVALID) {
            return ExpressionResult.INVALID;
         }
      } else if (leftNegative && rightPositive) {
         if (min > 0) {
            return ExpressionResult.INVALID;
         }

         long r = safeSubtract(safeMultiply(min, right.getMax(s)), safeSubtract(right.getMax(s), 1));
         if (left.setMin(s, r) == ExpressionResult.INVALID) {
            return ExpressionResult.INVALID;
         }

         if (left.getMax(s) / right.getMin(s) < min) {
            long x = right.getMin(s) + 1;
            if (right.setMin(s, x) == ExpressionResult.INVALID) {
               return ExpressionResult.INVALID;
            }
         }
      } else if (leftPositive && rightNegative) {
         if (min > 0) {
            return ExpressionResult.INVALID;
         }

         if (right.setMax(s, -calcRightMax(left.getMin(s), -min)) == ExpressionResult.INVALID) {
            return ExpressionResult.INVALID;
         }

         long r = (min * right.getMin(s)) + (-right.getMin(s) - 1);
         if (left.setMax(s, r) == ExpressionResult.INVALID) {
            return ExpressionResult.INVALID;
         }
      } else if (leftPositive && min > 0) {
         if (right.setMin(s, 1) == ExpressionResult.INVALID) {
            return ExpressionResult.INVALID;
         }
      } else if (leftNegative && min > 0) {
         if (right.setMax(s, -1) == ExpressionResult.INVALID) {
            return ExpressionResult.INVALID;
         }
      } else if (rightPositive && min > 0) {
         if (left.setMin(s, 1) == ExpressionResult.INVALID) {
            return ExpressionResult.INVALID;
         }
      } else if (rightNegative && min > 0) {
         if (left.setMax(s, -1) == ExpressionResult.INVALID) {
            return ExpressionResult.INVALID;
         }
      }

      return ExpressionResult.VALID;
   }

   @Override
   public ExpressionResult setMax(ConstraintStore s, long max) {
      if (right.setNot(s, 0) == ExpressionResult.INVALID) {
         return ExpressionResult.INVALID;
      }

      boolean leftNegative = left.getMax(s) < 0;
      boolean leftPositive = left.getMin(s) > 0;
      boolean rightNegative = right.getMax(s) < 0;
      boolean rightPositive = right.getMin(s) > 0;

      if (max == 0 && leftNegative && rightNegative) {
         if (left.setMin(s, right.getMin(s) + 1) == ExpressionResult.INVALID) {
            return ExpressionResult.INVALID;
         }
         if (right.setMax(s, left.getMax(s) - 1) == ExpressionResult.INVALID) {
            return ExpressionResult.INVALID;
         }
      } else if (max == 0 && leftPositive && rightPositive) {
         if (left.setMax(s, right.getMax(s) - 1) == ExpressionResult.INVALID) {
            return ExpressionResult.INVALID;
         }
         if (right.setMin(s, left.getMin(s) + 1) == ExpressionResult.INVALID) {
            return ExpressionResult.INVALID;
         }
      } else if (leftPositive && rightPositive) {
         if (max < 0) {
            return ExpressionResult.INVALID;
         }

         if (left.setMax(s, calcLeftMax(right.getMax(s), max)) == ExpressionResult.INVALID) {
            return ExpressionResult.INVALID;
         }
         if (right.setMin(s, calcRightMax(left.getMin(s), max)) == ExpressionResult.INVALID) {
            return ExpressionResult.INVALID;
         }
      } else if (leftNegative && rightNegative) {
         if (max < 0) {
            return ExpressionResult.INVALID;
         }

         if (left.setMin(s, -calcLeftMax(-right.getMin(s), max)) == ExpressionResult.INVALID) {
            return ExpressionResult.INVALID;
         }
         if (right.setMax(s, -calcRightMax(-left.getMax(s), max)) == ExpressionResult.INVALID) {
            return ExpressionResult.INVALID;
         }
      } else if (leftNegative && rightPositive && max < 0) {
         if (right.setMax(s, calcRightMin(-left.getMin(s), -max)) == ExpressionResult.INVALID) {
            return ExpressionResult.INVALID;
         }

         long r = (max * right.getMin(s));
         if (left.setMax(s, r) == ExpressionResult.INVALID) {
            return ExpressionResult.INVALID;
         }
      } else if (leftPositive && rightNegative && max < 0) {
         if (left.setMin(s, right.getMax(s) * max) == ExpressionResult.INVALID) {
            return ExpressionResult.INVALID;
         }

         if (left.getMax(s) / right.getMin(s) > max) {
            long x = (left.getMax(s) / max);
            if (right.setMin(s, x) == ExpressionResult.INVALID) {
               return ExpressionResult.INVALID;
            }
         }
      } else if (leftPositive && max < 0) {
         if (right.setMax(s, -1) == ExpressionResult.INVALID) {
            return ExpressionResult.INVALID;
         }
      } else if (leftNegative && max < 0) {
         if (right.setMin(s, 1) == ExpressionResult.INVALID) {
            return ExpressionResult.INVALID;
         }
      } else if (rightPositive && max < 0) {
         if (left.setMax(s, -1) == ExpressionResult.INVALID) {
            return ExpressionResult.INVALID;
         }
      } else if (rightNegative && max < 0) {
         if (left.setMin(s, 1) == ExpressionResult.INVALID) {
            return ExpressionResult.INVALID;
         }
      }

      return ExpressionResult.VALID;
   }

   private static long calcLeftMax(long right, long target) {
      return safeAdd(safeMultiply(right, target), safeSubtract(right, 1));
   }

   private static long calcRightMin(long left, long target) {
      return safeDivide(left, target);
   }

   private static long calcRightMax(long left, long target) {
      return safeAdd(safeDivide(left, safeAdd(target, 1)), 1);
   }

   @Override
   public void walk(Consumer<Expression> r) {
      r.accept(this);
      left.walk(r);
      right.walk(r);
   }

   @Override
   public Divide replace(Function<LeafExpression, LeafExpression> r) {
      return new Divide(left.replace(r), right.replace(r));
   }

   @Override
   public String toString() {
      return "Divide [left=" + left + ", right=" + right + "]";
   }
}