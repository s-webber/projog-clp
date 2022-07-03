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
import static org.projog.clp.example.ExpressionUtils.diff;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import org.projog.clp.BruteForceSearch;
import org.projog.clp.ClpConstraintStore;
import org.projog.clp.Variable;
import org.testng.annotations.Test;

/**
 * Example of solving the zebra puzzle.
 * <p>
 * See: https://en.wikipedia.org/wiki/Zebra_Puzzle
 */
public class ZebraPuzzleTest {
   @Test
   public void test() {
      ClpConstraintStore.Builder builder = new ClpConstraintStore.Builder();

      Variable english = builder.createVariable();
      Variable spainiard = builder.createVariable();
      Variable ukranian = builder.createVariable();
      Variable norwegian = builder.createVariable();
      Variable japanese = builder.createVariable();

      Variable red = builder.createVariable();
      Variable green = builder.createVariable();
      Variable ivory = builder.createVariable();
      Variable yellow = builder.createVariable();
      Variable blue = builder.createVariable();

      Variable dog = builder.createVariable();
      Variable snails = builder.createVariable();
      Variable fox = builder.createVariable();
      Variable horse = builder.createVariable();
      Variable zebra = builder.createVariable();

      Variable coffee = builder.createVariable();
      Variable tea = builder.createVariable();
      Variable milk = builder.createVariable();
      Variable juice = builder.createVariable();
      Variable water = builder.createVariable();

      Variable oldGold = builder.createVariable();
      Variable kools = builder.createVariable();
      Variable chesterfields = builder.createVariable();
      Variable luckyStrike = builder.createVariable();
      Variable parliaments = builder.createVariable();

      builder.enforce(english, spainiard, ukranian, norwegian, japanese).between(1, 5).distinct();
      builder.enforce(red, green, ivory, yellow, blue).between(1, 5).distinct();
      builder.enforce(dog, snails, fox, horse, zebra).between(1, 5).distinct();
      builder.enforce(coffee, tea, milk, juice, water).between(1, 5).distinct();
      builder.enforce(oldGold, kools, chesterfields, luckyStrike, parliaments).between(1, 5).distinct();

      // The Englishman lives in the red house.
      builder.enforce(english).equalTo(red);
      // The Spaniard owns the dog.
      builder.enforce(spainiard).equalTo(dog);
      // Coffee is drunk in the green house.
      builder.enforce(coffee).equalTo(green);
      // The Ukrainian drinks tea.
      builder.enforce(ukranian).equalTo(tea);
      // The green house is immediately to the right of the ivory house.
      builder.enforce(green).equalTo(add(1, ivory));
      // The Old Gold smoker owns snails.
      builder.enforce(oldGold).equalTo(snails);
      // Kools are smoked in the yellow house.
      builder.enforce(kools).equalTo(yellow);
      // Milk is drunk in the middle house.
      builder.enforce(3).equalTo(milk);
      // The Norwegian lives in the first house.
      builder.enforce(1).equalTo(norwegian); // TODO change to be "1 or 5" and check for alternative solution
      // The man who smokes Chesterfields lives in the house next to the man with the fox.
      builder.enforce(1).equalTo(diff(chesterfields, fox));
      // Kools are smoked in the house next to the house where the horse is kept.
      builder.enforce(1).equalTo(diff(kools, horse));
      // The Lucky Strike smoker drinks orange juice.
      builder.enforce(luckyStrike).equalTo(juice);
      // The Japanese smokes Parliaments.
      builder.enforce(japanese).equalTo(parliaments);
      // The Norwegian lives next to the blue house.
      builder.enforce(1).equalTo(diff(norwegian, blue));

      ClpConstraintStore environment = builder.build();
      assertTrue(environment.resolve());

      BruteForceSearch bruteForceSearch = new BruteForceSearch(environment);
      ClpConstraintStore solution = bruteForceSearch.next();

      assertEquals(1, solution.getValue(norwegian));
      assertEquals(2, solution.getValue(ukranian));
      assertEquals(3, solution.getValue(english));
      assertEquals(4, solution.getValue(spainiard));
      assertEquals(5, solution.getValue(japanese));

      assertEquals(1, solution.getValue(yellow));
      assertEquals(2, solution.getValue(blue));
      assertEquals(3, solution.getValue(red));
      assertEquals(4, solution.getValue(ivory));
      assertEquals(5, solution.getValue(green));

      assertEquals(1, solution.getValue(fox));
      assertEquals(2, solution.getValue(horse));
      assertEquals(3, solution.getValue(snails));
      assertEquals(4, solution.getValue(dog));
      assertEquals(5, solution.getValue(zebra));

      assertEquals(1, solution.getValue(water));
      assertEquals(2, solution.getValue(tea));
      assertEquals(3, solution.getValue(milk));
      assertEquals(4, solution.getValue(juice));
      assertEquals(5, solution.getValue(coffee));

      assertEquals(1, solution.getValue(kools));
      assertEquals(2, solution.getValue(chesterfields));
      assertEquals(3, solution.getValue(oldGold));
      assertEquals(4, solution.getValue(luckyStrike));
      assertEquals(5, solution.getValue(parliaments));

      assertNull(bruteForceSearch.next());
   }
}
