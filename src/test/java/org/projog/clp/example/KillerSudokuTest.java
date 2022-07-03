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

import static org.projog.clp.example.ExpressionUtils.add;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.projog.clp.Variable;
import org.testng.annotations.Test;

/**
 * Example of solving a killer sudoku puzzle.
 * <p>
 * See: https://en.wikipedia.org/wiki/Killer_sudoku
 */
public class KillerSudokuTest {
   @Test
   public void test() {
      Sudoku sudoku = new Sudoku();

      // define groups of cells and the required total sum of the values in those cells
      Group aa = new Group(10);
      Group ab = new Group(16);
      Group ac = new Group(15);
      Group ad = new Group(3);
      Group ae = new Group(6);
      Group af = new Group(15);
      Group ag = new Group(16);
      Group ah = new Group(4);
      Group ai = new Group(3);
      Group aj = new Group(8);
      Group ak = new Group(3);
      Group al = new Group(12);
      Group am = new Group(4);
      Group an = new Group(8);
      Group ao = new Group(9);
      Group ap = new Group(15);
      Group aq = new Group(17);
      Group ar = new Group(5);
      Group as = new Group(11);
      Group at = new Group(13);
      Group au = new Group(6);
      Group av = new Group(3);
      Group aw = new Group(13);
      Group ax = new Group(7);
      Group ay = new Group(9);
      Group az = new Group(2);
      Group ba = new Group(7);
      Group bb = new Group(9);
      Group bc = new Group(7);
      Group bd = new Group(9);
      Group be = new Group(12);
      Group bf = new Group(8);
      Group bg = new Group(5);
      Group bh = new Group(9);
      Group bi = new Group(8);
      Group bj = new Group(12);
      Group bk = new Group(7);
      Group bl = new Group(6);
      Group bm = new Group(17);
      Group bn = new Group(18);
      Group bo = new Group(3);
      Group bp = new Group(17);
      Group bq = new Group(6);
      Group br = new Group(5);
      Group bs = new Group(7);

      Group[][] grid = {
                  {aa, ab, ac, ac, ad, ae, ae, af, af},
                  {aa, ab, ab, ag, ah, ai, aj, aj, ak},
                  {al, am, am, ag, an, ai, ao, ao, ap},
                  {al, aq, aq, ar, ar, as, as, at, ap},
                  {al, au, av, aw, ax, ay, az, at, at},
                  {ba, bb, bb, aw, bc, bc, bd, be, be},
                  {ba, bf, bf, bg, bh, bi, bj, bj, be},
                  {bk, bl, bl, bg, bm, bm, bm, bn, bo},
                  {bp, bp, bq, bq, br, bs, bn, bn, bo}};

      Map<Group, List<Variable>> groups = new HashMap<>();
      for (int x = 0; x < Sudoku.MAX_VALUE; x++) {
         for (int y = 0; y < Sudoku.MAX_VALUE; y++) {
            groups.computeIfAbsent(grid[x][y], g -> new ArrayList<>()).add(sudoku.get(x, y));
         }
      }

      // add constraint for each group
      for (Map.Entry<Group, List<Variable>> e : groups.entrySet()) {
         sudoku.enforce(e.getKey().total).equalTo(add(e.getValue()));
      }

      // find and check solution
      int[][] solution = sudoku.findSolution();
      assertArrayEquals(solution[0], new int[] {2, 4, 9, 6, 3, 5, 1, 8, 7});
      assertArrayEquals(solution[1], new int[] {8, 5, 7, 9, 4, 1, 6, 2, 3});
      assertArrayEquals(solution[2], new int[] {6, 3, 1, 7, 8, 2, 4, 5, 9});
      assertArrayEquals(solution[3], new int[] {5, 9, 8, 3, 2, 4, 7, 1, 6});
      assertArrayEquals(solution[4], new int[] {1, 6, 3, 5, 7, 9, 2, 4, 8});
      assertArrayEquals(solution[5], new int[] {4, 7, 2, 8, 1, 6, 9, 3, 5});
      assertArrayEquals(solution[6], new int[] {3, 2, 6, 1, 9, 8, 5, 7, 4});
      assertArrayEquals(solution[7], new int[] {7, 1, 5, 4, 6, 3, 8, 9, 2});
      assertArrayEquals(solution[8], new int[] {9, 8, 4, 2, 5, 7, 3, 6, 1});
   }

   private static final class Group {
      final int total;

      Group(int total) {
         this.total = total;
      }
   }

   private static void assertArrayEquals(int[] actual, int[] expected) {
      assertTrue(Arrays.equals(actual, expected));
   }
}
