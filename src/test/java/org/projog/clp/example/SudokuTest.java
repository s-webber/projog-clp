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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.projog.clp.BruteForceSearch;
import org.projog.clp.ClpEnvironment;
import org.projog.clp.Variable;

public class SudokuTest {
   @Test
   public void test() {
      ClpEnvironment.Builder v = new ClpEnvironment.Builder();
      Variable[][] grid = new Variable[9][9];
      for (int x = 0; x < 9; x++) {
         for (int y = 0; y < 9; y++) {
            grid[x][y] = v.createVariable(x + "_" + y);
            v.enforce(grid[x][y]).between(1, 9);
         }
      }

      for (int x = 0; x < 9; x++) {
         Variable xxx[] = new Variable[9];
         Variable yyy[] = new Variable[9];
         for (int y = 0; y < 9; y++) {
            xxx[y] = grid[x][y];
            yyy[y] = grid[y][x];
         }
         v.enforce(xxx).distinct();
         v.enforce(yyy).distinct();
      }
      for (int x = 0; x < 3; x++) {
         for (int y = 0; y < 3; y++) {
            List<Variable> aaa = new ArrayList<>();
            for (int q = 0; q < 3; q++) {
               for (int z = 0; z < 3; z++) {
                  aaa.add(grid[(x * 3) + q][(y * 3) + z]);
               }
            }
            v.enforce(aaa.toArray(new Variable[0])).distinct();
         }
      }

      v.enforce(8).equalTo(grid[0][0]);
      v.enforce(2).equalTo(grid[0][1]);
      v.enforce(5).equalTo(grid[0][6]);
      v.enforce(7).equalTo(grid[1][0]);
      v.enforce(4).equalTo(grid[1][4]);
      v.enforce(3).equalTo(grid[1][6]);
      v.enforce(3).equalTo(grid[2][3]);
      v.enforce(6).equalTo(grid[2][4]);
      v.enforce(4).equalTo(grid[3][0]);
      v.enforce(8).equalTo(grid[3][1]);
      v.enforce(5).equalTo(grid[4][3]);
      v.enforce(9).equalTo(grid[4][7]);
      v.enforce(1).equalTo(grid[5][3]);
      v.enforce(2).equalTo(grid[5][4]);
      v.enforce(8).equalTo(grid[5][7]);
      v.enforce(7).equalTo(grid[6][8]);
      v.enforce(6).equalTo(grid[7][2]);
      v.enforce(8).equalTo(grid[7][4]);
      v.enforce(9).equalTo(grid[7][5]);
      v.enforce(1).equalTo(grid[8][2]);
      v.enforce(5).equalTo(grid[8][8]);

      ClpEnvironment build = v.build();
      build.resolve(); // TODO move .
      BruteForceSearch f = new BruteForceSearch(build);
      ClpEnvironment q = f.next();

      assertEquals(8, q.getValue(grid[0][0]));
      assertEquals(2, q.getValue(grid[0][1]));
      assertEquals(3, q.getValue(grid[0][2]));
      assertEquals(9, q.getValue(grid[0][3]));
      assertEquals(1, q.getValue(grid[0][4]));
      assertEquals(7, q.getValue(grid[0][5]));
      assertEquals(5, q.getValue(grid[0][6]));
      assertEquals(4, q.getValue(grid[0][7]));
      assertEquals(6, q.getValue(grid[0][8]));
      assertEquals(7, q.getValue(grid[1][0]));
      assertEquals(6, q.getValue(grid[1][1]));
      assertEquals(5, q.getValue(grid[1][2]));
      assertEquals(8, q.getValue(grid[1][3]));
      assertEquals(4, q.getValue(grid[1][4]));
      assertEquals(2, q.getValue(grid[1][5]));
      assertEquals(3, q.getValue(grid[1][6]));
      assertEquals(1, q.getValue(grid[1][7]));
      assertEquals(9, q.getValue(grid[1][8]));
      assertEquals(1, q.getValue(grid[2][0]));
      assertEquals(9, q.getValue(grid[2][1]));
      assertEquals(4, q.getValue(grid[2][2]));
      assertEquals(3, q.getValue(grid[2][3]));
      assertEquals(6, q.getValue(grid[2][4]));
      assertEquals(5, q.getValue(grid[2][5]));
      assertEquals(2, q.getValue(grid[2][6]));
      assertEquals(7, q.getValue(grid[2][7]));
      assertEquals(8, q.getValue(grid[2][8]));
      assertEquals(4, q.getValue(grid[3][0]));
      assertEquals(8, q.getValue(grid[3][1]));
      assertEquals(7, q.getValue(grid[3][2]));
      assertEquals(6, q.getValue(grid[3][3]));
      assertEquals(9, q.getValue(grid[3][4]));
      assertEquals(3, q.getValue(grid[3][5]));
      assertEquals(1, q.getValue(grid[3][6]));
      assertEquals(5, q.getValue(grid[3][7]));
      assertEquals(2, q.getValue(grid[3][8]));
      assertEquals(3, q.getValue(grid[4][0]));
      assertEquals(1, q.getValue(grid[4][1]));
      assertEquals(2, q.getValue(grid[4][2]));
      assertEquals(5, q.getValue(grid[4][3]));
      assertEquals(7, q.getValue(grid[4][4]));
      assertEquals(8, q.getValue(grid[4][5]));
      assertEquals(6, q.getValue(grid[4][6]));
      assertEquals(9, q.getValue(grid[4][7]));
      assertEquals(4, q.getValue(grid[4][8]));
      assertEquals(6, q.getValue(grid[5][0]));
      assertEquals(5, q.getValue(grid[5][1]));
      assertEquals(9, q.getValue(grid[5][2]));
      assertEquals(1, q.getValue(grid[5][3]));
      assertEquals(2, q.getValue(grid[5][4]));
      assertEquals(4, q.getValue(grid[5][5]));
      assertEquals(7, q.getValue(grid[5][6]));
      assertEquals(8, q.getValue(grid[5][7]));
      assertEquals(3, q.getValue(grid[5][8]));
      assertEquals(2, q.getValue(grid[6][0]));
      assertEquals(3, q.getValue(grid[6][1]));
      assertEquals(8, q.getValue(grid[6][2]));
      assertEquals(4, q.getValue(grid[6][3]));
      assertEquals(5, q.getValue(grid[6][4]));
      assertEquals(1, q.getValue(grid[6][5]));
      assertEquals(9, q.getValue(grid[6][6]));
      assertEquals(6, q.getValue(grid[6][7]));
      assertEquals(7, q.getValue(grid[6][8]));
      assertEquals(5, q.getValue(grid[7][0]));
      assertEquals(7, q.getValue(grid[7][1]));
      assertEquals(6, q.getValue(grid[7][2]));
      assertEquals(2, q.getValue(grid[7][3]));
      assertEquals(8, q.getValue(grid[7][4]));
      assertEquals(9, q.getValue(grid[7][5]));
      assertEquals(4, q.getValue(grid[7][6]));
      assertEquals(3, q.getValue(grid[7][7]));
      assertEquals(1, q.getValue(grid[7][8]));
      assertEquals(9, q.getValue(grid[8][0]));
      assertEquals(4, q.getValue(grid[8][1]));
      assertEquals(1, q.getValue(grid[8][2]));
      assertEquals(7, q.getValue(grid[8][3]));
      assertEquals(3, q.getValue(grid[8][4]));
      assertEquals(6, q.getValue(grid[8][5]));
      assertEquals(8, q.getValue(grid[8][6]));
      assertEquals(2, q.getValue(grid[8][7]));
      assertEquals(5, q.getValue(grid[8][8]));

      assertNull(f.next());
   }
}
