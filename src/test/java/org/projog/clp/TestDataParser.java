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

final class TestDataParser {
   private TestDataParser() {
   }

   static Range parseRange(String input) {
      int pos = input.indexOf(':');
      if (pos == -1) {
         long value = parseLong(input);
         return new Range(value, value);
      }
      long min = parseLong(input.substring(0, pos));
      long max = parseLong(input.substring(pos + 1));
      return new Range(min, max);
   }

   static long parseLong(String input) {
      input = input.replace("MAX-5", "" + (Long.MAX_VALUE - 5));
      input = input.replace("MIN+5", "" + (Long.MIN_VALUE + 5));
      input = input.replace("MAX-4", "" + (Long.MAX_VALUE - 4));
      input = input.replace("MIN+4", "" + (Long.MIN_VALUE + 4));
      input = input.replace("MAX-3", "" + (Long.MAX_VALUE - 3));
      input = input.replace("MIN+3", "" + (Long.MIN_VALUE + 3));
      input = input.replace("MAX-2", "" + (Long.MAX_VALUE - 2));
      input = input.replace("MIN+2", "" + (Long.MIN_VALUE + 2));
      input = input.replace("MAX-1", "" + (Long.MAX_VALUE - 1));
      input = input.replace("MIN+1", "" + (Long.MIN_VALUE + 1));
      input = input.replace("MAX", "" + Long.MAX_VALUE);
      input = input.replace("MIN", "" + Long.MIN_VALUE);
      return Long.parseLong(input);
   }
}
