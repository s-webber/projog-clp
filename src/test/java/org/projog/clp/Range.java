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

final class Range {
   final long min;
   final long max;

   Range(long min, long max) {
      if (max < min) {
         throw new IllegalArgumentException(max + " < " + min);
      }
      this.min = min;
      this.max = max;
   }

   long min() {
      return min;
   }

   long max() {
      return max;
   }
}
