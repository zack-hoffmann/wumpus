# Wumpus Practice
## *by Zack Hoffmann*
This is a pratice project for personal use. It is a re-implementation of the *Hunt the Wumpus* game concept using Java and many modern - albiet arbitrary - programming practices.
## Step 0 - Project Template (COMPLETE)
Create a new Maven project using Checkstyle, Spotbugs, and the Java Best Practices ruleset for PMD.  Also create a core runnable class and a simple test for it.
## Step 1 - Generate the Game World
Faithful to the original Hunt the Wumpus game, the early map layout will be rooms joined as corners of a dodecahedron.  As there will be no way to traverse these rooms, the program will just print out their linked references and then exit.