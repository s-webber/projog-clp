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

import static org.testng.Assert.assertTrue;

import java.util.Arrays;

import org.testng.annotations.Test;

public class SudokuTest {
   @Test
   public void test() {
      // given
      Integer x = null;
      Integer[][] input = new Integer[][] {
                  {3, 7, x, x, x, 6, x, 2, x},
                  {x, 1, x, 9, x, x, x, x, x},
                  {2, x, x, 7, x, x, 4, x, x},
                  {4, x, x, x, x, x, x, 5, 3},
                  {x, 2, 9, x, 1, x, 8, 7, x},
                  {6, 3, x, x, x, x, x, x, 4},
                  {x, x, 2, x, x, 8, x, x, 7},
                  {x, x, x, x, x, 7, x, 4, x},
                  {x, 6, x, 4, x, x, x, 8, 9}};

      // when
      int[][] solution = Sudoku.solve(input);

      // then
      assertArrayEquals(solution[0], new int[] {3, 7, 4, 1, 5, 6, 9, 2, 8});
      assertArrayEquals(solution[1], new int[] {8, 1, 6, 9, 4, 2, 7, 3, 5});
      assertArrayEquals(solution[2], new int[] {2, 9, 5, 7, 8, 3, 4, 6, 1});
      assertArrayEquals(solution[3], new int[] {4, 8, 7, 2, 6, 9, 1, 5, 3});
      assertArrayEquals(solution[4], new int[] {5, 2, 9, 3, 1, 4, 8, 7, 6});
      assertArrayEquals(solution[5], new int[] {6, 3, 1, 8, 7, 5, 2, 9, 4});
      assertArrayEquals(solution[6], new int[] {9, 4, 2, 5, 3, 8, 6, 1, 7});
      assertArrayEquals(solution[7], new int[] {1, 5, 8, 6, 9, 7, 3, 4, 2});
      assertArrayEquals(solution[8], new int[] {7, 6, 3, 4, 2, 1, 5, 8, 9});
   }

   private static void assertArrayEquals(int[] actual, int[] expected) {
      assertTrue(Arrays.equals(actual, expected));
   }
}
