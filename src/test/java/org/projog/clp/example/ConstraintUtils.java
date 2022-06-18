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
package org.projog.clp.example;

import org.projog.clp.EqualTo;
import org.projog.clp.Expression;
import org.projog.clp.FixedValue;

class ConstraintUtils {
   private ConstraintUtils() {
   }

   static Builder is(long v) {
      return is(new FixedValue(v));
   }

   static Builder is(Expression e) {
      return new Builder(e);
   }

   static class Builder {
      private final Expression left;

      private Builder(Expression e) {
         this.left = e;
      }

      EqualTo equalTo(Expression right) {
         return new EqualTo(left, right);
      }
   }
}
