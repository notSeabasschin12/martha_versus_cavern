# martha_versus_cavern
Code written to navigate a maze in order to find a gem, then exit the maze while collecting gold. In order to navigate the maze to find the gem, a depth-first search is used. This DFS algorithm is optimized since we know, at any given tile at the maze, the distance from the given tile to the orb. Thus, instead of picking a random tile to go to next, pick the tile with the shortest distance to the gem. To leave the maze, Dijkstra's algorithm is used to find the shortest path out of the maze from a given tile.

Only the files with code that I wrote are in this repository. 

Files:
Pollack.java contains the methods to navigate and exit the maze. 
path.java contains Dijkstra's algorithm (lines 51-104 were provided to us).
