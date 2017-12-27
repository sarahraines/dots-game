=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=
CIS 120 Game Project README
PennKey: saraines
=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=

===================
=: Core Concepts :=
===================

- List the four core concepts, the features they implement, and why each feature
  is an appropriate use of the concept. Incorporate the feedback you got after
  submitting your proposal.

  1. Collections

I used a LinkedList to model a line of dots in the Path class. It helped me figure out whether or not dots could be
connected. Although I originally imagined using a Set, it would not have worked, as sometimes dots are added to a path
twice. This allows the path to intersect itself, and Sets remove repeat instances. I needed to know when the path
intersected itself, as when it did so, I could not add any more dots to the path. I also needed to be able to call up
the third to last dot in the path, as when it was the same as the last dot, I removed the last two dots (because the
path had doubled back on itself).

  2. 2D Arrays

I used 2D arrays to model the game board. There were 6x6 dots in the grid. Dots can only be connected in a path to gray
or same-color dots that are directly above, below, left, or right of them. When dots are removed from the board in a
path, above dots fall into their spot in the 2D array. When dots are removed from the board in a closed path, all of the
dots of the same color fall as well. In order to remove dots, I set their color to white to indicate to fall() that
those are the dots to be removed.

  3. Recursion

I used recursion to remove the longest path of dots possible from the board. My method getBestMove() went through every
dot of each color, identifying dots that have only one same-colored or gray neighbor. These dots are leaves on a tree. I
know that the maximum path in a tree of same-colored and gray dots will have to go from one leaf to another. I go
through all possible pairs of leaf dots to find the longest path. I ask the start dot to see the length of its path from
a neighbor until the end dot in pathHelper(). Then, the neighbor calls pathHelper() on its neighbors, until the longest
path to the end dot is found, or no path at all.

  4. I/O

I used I/O to create a leaderboard. At the end of a round, a user submits their score no matter how low. A JOptionPane
pops up, and they are  asked to input a username that is written to a file. Only the highest scores of every user are
saved in a Map in the ScoreScanner class. The top ten scores are written to the "High Scores" screen, which can be seen
in the pause menu.


=========================
=: Your Implementation :=
=========================

- Provide an overview of each of the classes in your code, and what their
  function is in the overall game.

Dot: Holds the state of a dot, including its location, color, and whether or not it is clear. We can also set the color
of a clear dot.

Path: Holds a LinkedList of Dots that have the same color. Controls which dots can be connected to one another. This
includes capping path length when a path becomes closed. We can also get attributes of path, such as its last element
and color. Finally, we can set elements of Path, such as adding and removing dots to its LinkedList.

ScoreScanner: Reads the highscores.sarah document with all of the scores ever achieved in the game. Adds the highest
scores for each user to a map, which is later written onto the High Scores screen.

Game: Runs the game. Also adds some of the power-up JLabels and JButtons to the JFrame.

GameBoard: Same as GameCourt, just renamed.

- Were there any significant stumbling blocks while you were implementing your
  game (related to your design, or otherwise)?

Implementing the recursion was a lot more difficult than I expected! I needed multiple helper functions, so when I was
first debugging this code, I wasn't where it was failing.

It was also difficult to get focus on the correct window for using KeyListener. When I implemented my power-ups, they
only seemed to work on the first key press or not at all.

- Evaluate your design. Is there a good separation of functionality? How well is
  private state encapsulated? What would you refactor, if given the chance?

My design has a good separation of functionality, as the dots have a number of getters and setters that maintain the
state of the Dots in the grid. Path maintains which dots are connected to one another. GameBoard completes a lot of the
logic regarding the status of the 2D array board, including which dots have fallen when they are removed in a path.

If I had more time, I would consider closed paths in Best Move, as sometimes creating a closed path and making all of
the dots of the same color disappear is the best move, when it is possible.


========================
=: External Resources :=
========================

- Cite any external resources (libraries, images, tutorials, etc.) that you may
  have used while implementing your game.



