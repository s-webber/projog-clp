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

import java.util.ArrayList;
import java.util.List;

import org.projog.clp.BruteForceSearch;
import org.projog.clp.ClpConstraintStore;
import org.projog.clp.ClpConstraintStore.Enforce;
import org.projog.clp.Variable;

final class Sudoku {
   static final int MAX_VALUE = 9;

   private final ClpConstraintStore.Builder builder;
   private final Variable[][] grid;

   Sudoku() {
      this.builder = new ClpConstraintStore.Builder();
      this.grid = new Variable[MAX_VALUE][MAX_VALUE];

      // create variable for each cell of grid
      for (int x = 0; x < MAX_VALUE; x++) {
         for (int y = 0; y < MAX_VALUE; y++) {
            grid[x][y] = builder.createVariable();
            builder.enforce(grid[x][y]).between(1, MAX_VALUE);
         }
      }

      // add "all different" constraints on each column and row
      for (int x = 0; x < MAX_VALUE; x++) {
         Variable rows[] = new Variable[MAX_VALUE];
         Variable columns[] = new Variable[MAX_VALUE];
         for (int y = 0; y < MAX_VALUE; y++) {
            rows[y] = grid[x][y];
            columns[y] = grid[y][x];
         }
         builder.enforce(rows).distinct();
         builder.enforce(columns).distinct();
      }

      // add "all different" constraints on the nine 3x3 inner squares
      for (int x = 0; x < 3; x++) {
         for (int y = 0; y < 3; y++) {
            List<Variable> innerSquare = new ArrayList<>();
            for (int q = 0; q < 3; q++) {
               for (int z = 0; z < 3; z++) {
                  innerSquare.add(grid[(x * 3) + q][(y * 3) + z]);
               }
            }
            builder.enforce(innerSquare).distinct();
         }
      }
   }

   void set(int x, int y, int value) {
      builder.enforce(value).equalTo(grid[x][y]);
   }

   Variable get(int x, int y) {
      return grid[x][y];
   }

   Enforce enforce(long i) {
      return builder.enforce(i);
   }

   int[][] findSolution() {
      // search for solution
      ClpConstraintStore environment = builder.build();
      environment.resolve(); // TODO remove need to call resolve here?
      BruteForceSearch search = new BruteForceSearch(environment);
      ClpConstraintStore solution = search.next();

      // ensure exactly one solution
      if (solution == null) {
         throw new IllegalStateException("no solution found");
      }
      if (search.next() != null) {
         throw new IllegalStateException("more than one solution found");
      }

      // convert solution to int[][]
      int[][] output = new int[MAX_VALUE][MAX_VALUE];
      for (int x = 0; x < MAX_VALUE; x++) {
         for (int y = 0; y < MAX_VALUE; y++) {
            output[x][y] = (int) solution.getValue(grid[x][y]);
         }
      }

      return output;
   }

   static int[][] solve(Integer[][] input) {
      // populate sudoku grid with initial values
      Sudoku sudoku = new Sudoku();
      for (int x = 0; x < MAX_VALUE; x++) {
         for (int y = 0; y < MAX_VALUE; y++) {
            if (input[x][y] != null) {
               sudoku.set(x, y, input[x][y]);
            }
         }
      }

      return sudoku.findSolution();
   }
}
