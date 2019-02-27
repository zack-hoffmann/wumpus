# Wumpus Practice
## *by Zack Hoffmann*
This is a pratice project for personal use. It is a re-implementation of the *Hunt the Wumpus* game concept using Java and many modern - albiet arbitrary - programming practices.
## Step 0 - Project Template (COMPLETE)
Create a new Maven project using Checkstyle, Spotbugs, and the Java Best Practices ruleset for PMD.  Also create a core runnable class and a simple test for it.
## Step 1 - Generate the Game World (COMPLETE)
Faithful to the original Hunt the Wumpus game, the early map layout will be rooms joined as corners of a dodecahedron.  As there will be no way to traverse these rooms, the program will just print out their linked references and then exit.

**Amended:** This game will use a randomized map instead, since that is a reasonable variation of early versions that has better long-term gameplay.

## Step 2 - Basic Gameplay
The core game includes using deduction from environmental clues to find and kill the wumpus by shooting it with an arrow without running in to it.  Environmental hazards include fatal pit traps and "super bats" which can move the player to a random location in the wumpus lair.  As an added challenge, the player should be limited to three arrows.