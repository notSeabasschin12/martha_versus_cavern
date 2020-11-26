package submit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import game.FindState;
import game.Finder;
import game.Node;
import game.NodeStatus;
import game.ScramState;

/** Student solution for two methods. */
public class Pollack extends Finder {
    /** A Set of Nodes that have been visited already. */
    HashSet<Long> visited= new HashSet<>();

    /** Get to the orb in as few steps as possible. <br>
     * Once you get there, you must return from the function in order to pick it up. <br>
     * If you continue to move after finding the orb rather than returning, it will not count.<br>
     * If you return from this function while not standing on top of the orb, it will count as <br>
     * a failure.
     *
     * There is no limit to how many steps you can take, but you will receive<br>
     * a score bonus multiplier for finding the orb in fewer steps.
     *
     * At every step, you know only your current tile's ID and the ID of all<br>
     * open neighbor tiles, as well as the distance to the orb at each of <br>
     * these tiles (ignoring walls and obstacles).
     *
     * In order to get information about the current state, use functions<br>
     * state.currentLoc(), state.neighbors(), and state.distanceToOrb() in FindState.<br>
     * You know you are standing on the orb when distanceToOrb() is 0.
     *
     * Use function state.moveTo(long id) in FindState to move to a neighboring<br>
     * tile by its ID. Doing this will change state to reflect your new position.
     *
     * A suggested first implementation that will always find the orb, but <br>
     * likely won't receive a large bonus multiplier, is a depth-first walk. <br>
     * Some modification is necessary to make the search better, in general. */
    @Override
    public void findOrb(FindState state) {
        moveToOrb(state);
    }

    /** Martha is standing on a tile u that has not been visited yet. Visit every node reachable
     * along paths of unvisited tiles starting from tile u.
     *
     * Precondition: Martha is standing on some tile that has not been visited <br>
     * Postcondition: If Martha reaches the tile with the orb on it, return on the tile which
     * contains the orb. If Martha has not reached the target tile and is at a dead end, then
     * backtrack to the tile before and continue searching. */
    public void moveToOrb(FindState state) {
        if (state.distanceToOrb() == 0) { return; }
        visited.add(state.currentLoc());

        /* Store the current tile's state. If in the for loop, when you move to a neighbor
         * tile and it is a dead end but isn't the target tile, need to backtrack/move to
         * previous tile and to do so we need to somehow maintain the previous tile's state.
         */
        Long prevTile= state.currentLoc();

        // Put collection state.neighbors() into an ArrayList and then sort it
        ArrayList<NodeStatus> sortNeighbors= new ArrayList<>();
        for (NodeStatus n : state.neighbors()) {
            sortNeighbors.add(n);
        }
        insertionSort(sortNeighbors);

        // Find the neighbor with the lowest distance to the target tile.
        for (NodeStatus n : sortNeighbors) {
            if (!visited.contains(n.getId())) {
                state.moveTo(n.getId());
                moveToOrb(state);
                // If tile is target tile, return
                if (state.distanceToOrb() == 0) { return; }
                state.moveTo(prevTile);
            }

        }
        // Backtrack to the tile before this one and continue search through neighbors of prev tile.
        return;
    }

    /** Method to insertion sort an ArrayList of type NodeStatus <br>
     * Precondition: b[0..b.length-1] is unknown. <br>
     * Postcondition: b[0..b.length-1] is sorted in ascending order according to method compareTo in
     * class NodeStatus. */
    public void insertionSort(ArrayList<NodeStatus> neighbors) {
        // Invariant: b[0..k-1] is sorted and b[k..b.length-1] is not.
        int k= 0;
        while (k != neighbors.size()) {
            int j= k;
            // push b[k] into position
            while (0 < j && neighbors.get(j - 1).compareTo(neighbors.get(j)) > 0) {
                // swap b[j-1] and b[j]
                swap(neighbors, j, j - 1);
                j-- ;
            }
            k++ ;
        }
    }

    private void swap(ArrayList<NodeStatus> end, int node1, int node2) {
        NodeStatus temp= end.get(node1);
        end.remove(node1);
        end.add(node1, end.get(node2));
        end.remove(node2);
        end.add(node2, temp);
    }

    /** Pres Pollack is standing at a node given by parameter state.<br>
     *
     * Get out of the cavern before the ceiling collapses, trying to collect as <br>
     * much gold as possible along the way. Your solution must ALWAYS get out <br>
     * before time runs out, and this should be prioritized above collecting gold.
     *
     * You now have access to the entire underlying graph, which can be accessed <br>
     * through parameter state. <br>
     * state.currentNode() and state.getExit() will return Node objects of interest, and <br>
     * state.allNodes() will return a collection of all nodes on the graph.
     *
     * The cavern will collapse in the number of steps given by <br>
     * state.stepsLeft(), and for each step this number is decremented by the <br>
     * weight of the edge taken. <br>
     * Use state.stepsLeft() to get the time still remaining, <br>
     * Use state.moveTo() to move to a destination node adjacent to your current node.<br>
     * Do not call state.grabGold(). Gold on a node is automatically picked up <br>
     * when the node is reached.<br>
     *
     * The method must return from this function while standing at the exit. <br>
     * Failing to do so before time runs out or returning from the wrong <br>
     * location will be considered a failed run.
     *
     * You will always have enough time to scram using the shortest path from the <br>
     * starting position to the exit, although this will not collect much gold. <br>
     * For this reason, using the shortest path method to calculate the shortest <br>
     * path to the exit is a good starting solution */
    @Override
    public void scram(ScramState state) {
        List<Node> shortestPath= Path.shortest(state.currentNode(), state.getExit());
        List<Node> nearestPath;
        List<Node> goldPath;

        while (true) {
            Node nearest= findNearestGold(state);
            if (nearest != null) {
                nearestPath= Path.shortest(state.currentNode(), nearest); // Node path to nearest gold tile
                goldPath= Path.shortest(nearest, state.getExit()); // Node path from nearest gold tile to exit
                if (totalLength(nearestPath) + totalLength(goldPath) < state.stepsLeft()) {
                    for (Node n : nearestPath) {
                        Node originalLocation= state.currentNode();
                        if (!n.equals(originalLocation)) {
                            state.moveTo(n);
                        }
                        shortestPath= Path.shortest(nearest, state.getExit());
                    }
                } 
                else {break}
            } else {break}
        }

        Node originalLocation= state.currentNode();
        for (Node n : shortestPath) {
            if (!n.equals(originalLocation)) {
                state.moveTo(n);
            }
        }
        return;
    }

    /** Find the nearest tile from the current tile that has gold. If no tile with gold exists,
     * return null. */
    private Node findNearestGold(ScramState state) {
        Collection<Node> all= state.allNodes();
        ArrayList<Node> nodesGold= new ArrayList<>();
        // Get all nodes with gold on them
        for (Node n : all) {
            if (n.getTile().gold() != 0) {
                nodesGold.add(n);
            }
        }
        Node closest= null;
        int min= Integer.MAX_VALUE;
        List<Node> shortest;
        for (Node x : nodesGold) {
            shortest= Path.shortest(state.currentNode(), x); // Calculate node path to nodes with gold on them
            int path= totalLength(shortest); // Calculate steps to that tile
            if (path < min) {
                min= path;
                closest= x;
            }
        }
        return closest;
    }

    /** Given a list of Nodes in a path, calculate the total distance from the starting node to the
     * end. nodeList is calculated using Djikstra's algorithm. */
    private int totalLength(List<Node> nodeList) {
        int totalDist= 0;
        List<Integer> visited= new ArrayList<>();
        for (int i= 0; i < nodeList.size(); i++ ) {
            visited.add(0);
        }
        for (Node n : nodeList) {
            for (Node e : n.getNeighbors()) {
                if (nodeList.contains(e) && visited.get(nodeList.indexOf(e)) != 1) {
                    totalDist+= n.getEdge(e).length();
                }
                visited.set(nodeList.indexOf(n), 1);
            }
        }
        return totalDist;
    }
}
