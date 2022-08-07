package submit;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import graph.FindState;
import graph.Finder;
import graph.FleeState;
import graph.Node;
import graph.NodeStatus;

/** A solution with find-the-Orb optimized and flee getting out as fast as possible. */
public class Pollack extends Finder {

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
     * currentLoc(), neighbors(), and distanceToOrb() in FindState.<br>
     * You know you are standing on the orb when distanceToOrb() is 0.
     *
     * Use function moveTo(long id) in FindState to move to a neighboring<br>
     * tile by its ID. Doing this will change state to reflect your new position.
     *
     * A suggested first implementation that will always find the orb, but <br>
     * likely won't receive a large bonus multiplier, is a depth-first search. <br>
     * Some modification is necessary to make the search better, in general. */
    @Override
    public void find(FindState state) {
        // TODO 1: Walk to the orb

        // Basic DFS Walk w/o optimization
        // dfsBasicWalk(state, new HashSet<>());

        // Optimized DFS Walk
        dfsOptimizedWalk(state, new HashSet<>());
    }

    /** Basic DFS Walk: initial implementation of a DFS walk without optimization. */
    public static void dfsBasicWalk(FindState s, Set<Long> visited) {

        if (s.distanceToOrb() == 0) return;

        Long u= s.currentLoc();

        // Set of all visited nodes (Visit u)
        visited.add(u);

        // Checking all neighbors
        for (NodeStatus n : s.neighbors()) {
            if (!visited.contains(n.getId())) {
                s.moveTo(n.getId());
                dfsBasicWalk(s, visited);
                if (s.distanceToOrb() == 0) return;
                s.moveTo(u);
            }
        }
    }

    /** Optimized DFS Walk: */
    public void dfsOptimizedWalk(FindState s, Set<Long> visited) {
        if (s.distanceToOrb() == 0) return;

        Long u= s.currentLoc();

        // Set of all visited nodes (Visit u)
        visited.add(u);

        // Min Heap of nodes with priority given to nodes with smallest distance to orb
        Heap<NodeStatus> minOrbDist= new Heap<>(true);
        for (NodeStatus n : s.neighbors()) {
            int dist= n.getDistanceToTarget();
            minOrbDist.insert(n, dist);
        }

        while (minOrbDist.size() > 0) {
            NodeStatus minNode= minOrbDist.poll();

            // Checking all Neighbors
            if (!visited.contains(minNode.getId())) {
                s.moveTo(minNode.getId());
                dfsOptimizedWalk(s, visited);
                if (s.distanceToOrb() == 0) return;
                s.moveTo(u);
            }
        }
    }

    /** Get out the cavern before the ceiling collapses, trying to collect as <br>
     * much gold as possible along the way. Your solution must ALWAYS get out <br>
     * before steps runs out, and this should be prioritized above collecting gold.
     *
     * You now have access to the entire underlying graph, which can be accessed <br>
     * through FleeState state. <br>
     * currentNode() and exit() will return Node objects of interest, and <br>
     * allsNodes() will return a collection of all nodes on the graph.
     *
     * Note that the cavern will collapse in the number of steps given by <br>
     * stepsLeft(), and for each step this number is decremented by the <br>
     * weight of the edge taken. <br>
     * Use stepsLeft() to get the steps still remaining, and <br>
     * moveTo() to move to a destination node adjacent to your current node.
     *
     * You must return from this function while standing at the exit. <br>
     * Failing to do so before steps runs out or returning from the wrong <br>
     * location will be considered a failed run.
     *
     * You will always have enough steps to flee using the shortest path from the <br>
     * starting position to the exit, although this will not collect much gold. <br>
     * For this reason, using Dijkstra's to plot the shortest path to the exit <br>
     * is a good starting solution
     *
     * Here's another hint. Whatever you do you will need to traverse a given path. It makes sense
     * to write a method to do this, perhaps with this specification:
     *
     * // Traverse the nodes in moveOut sequentially, starting at the node<br>
     * // pertaining to state <br>
     * // public void moveAlong(FleeState state, List<Node> moveOut) */
    @Override
    public void flee(FleeState state) {
        // TODO 2. Get out of the cavern in time, picking up as much gold as possible.

        // Basic Flee
        // basicFlee(state);

        // Basic Coin Flee
        // basicCoinFlee(state);

        // Optimized Basic Coin Flee
        optBasicCoinFlee(state);

        // modified DFS flee
        // dfsCoinFlee(state);
    }

    /** basicFlee: Computes shortest path to exit using Djikstra's shortest path algorithm based on
     * current state. */
    public void basicFlee(FleeState state) {

        Node start= state.currentNode();
        Node end= state.exit();

        // Finds the shortest path using Djikstra's algorithm
        List<Node> shortPath= Path.shortestPath(start, end);

        // Moves along the shortest path
        moveAlong(state, shortPath);
    }

    /** basicCoinFlee: gathers some coins by following shortest path to max coins once. */
    public void basicCoinFlee(FleeState state) {
        Node start= state.currentNode();
        Node end= state.exit();

        // Max Heap of nodes with priority given to nodes with most coins
        Heap<Node> maxCoin= new Heap<>(false);
        Collection<Node> allnodes= state.allNodes();
        for (Node n : allnodes) {
            int coins= n.getTile().gold();
            if (coins > 0) {
                maxCoin.insert(n, coins);
            }
        }

        // Find a path from the start to a node with max coins
        Node bigCoin= maxCoin.peek();
        List<Node> coinPath= Path.shortestPath(start, bigCoin);

        // Find a path from bigCoin to end
        List<Node> endPath= Path.shortestPath(bigCoin, end);

        // Distance from start to big coin
        int distCoin= Path.pathSum(coinPath);
        // Distance from big coin to end
        int distEnd= Path.pathSum(endPath);

        int stepsLeft= state.stepsLeft();

        if (stepsLeft > distCoin + distEnd) {
            moveAlong(state, coinPath);
            moveAlong(state, endPath);
        } else {
            List<Node> shortPath= Path.shortestPath(start, end);
            moveAlong(state, shortPath);
        }
    }

    /** Optimizes basicCoinFlee to gather some coins by following shortest path to max coins
     * multiple times before steps run out. */
    public void optBasicCoinFlee(FleeState state) {

        // Exit Node
        Node end= state.exit();

        // Max Heap of nodes with priority given to nodes with most coins
        Heap<Node> maxCoin= new Heap<>(false);
        Collection<Node> allnodes= state.allNodes();
        for (Node n : allnodes) {
            int coins= n.getTile().gold();
            if (coins > 0) {
                maxCoin.insert(n, coins);
            }
        }

        int stepsLeft= 0;
        int distCoin= 0;
        int distEnd= 0;
        while (maxCoin.size() > 0 && stepsLeft >= distCoin + distEnd) {
            // Store current node with most coils in bigCoin and remove it from maxCoin heap
            Node bigCoin= maxCoin.poll();

            // Find a path from the start to a node with max coins
            List<Node> coinPath= Path.shortestPath(state.currentNode(), bigCoin);

            // Find a path from bigCoin to end
            List<Node> endPath= Path.shortestPath(bigCoin, end);

            // Distance from start to big coin
            distCoin= Path.pathSum(coinPath);
            // Distance from big coin to end
            distEnd= Path.pathSum(endPath);

            stepsLeft= state.stepsLeft();

            if (stepsLeft > distCoin + distEnd) {
                moveAlong(state, coinPath);
                moveAlong(state, endPath);
            } else {
                List<Node> shortPath= Path.shortestPath(state.currentNode(), end);
                moveAlong(state, shortPath);
            }
        }

    }

    /** DFS Coin Flee: tries to gather more coins using a DFS algorithm */
    public void dfsCoinFlee(FleeState state) {

        // Max Heap of nodes with priority given to nodes with most coins
        Heap<Node> maxCoin= new Heap<>(false);
        Collection<Node> allnodes= state.allNodes();
        for (Node n : allnodes) {
            int coins= n.getTile().gold();
            if (coins > 0) {
                maxCoin.insert(n, coins);
            }
        }

        // Set containing all visited nodes for a modified iterative DFS
        Set<Long> visited= new HashSet<>();

        /*Visit every node reachable along paths of unvisited nodes from node u.*/

    }

    /** Helper function to traverse the nodes in moveOut sequentially, starting at the node
     * pertaining to state. */
    public void moveAlong(FleeState state, List<Node> moveOut) {
        for (Node n : moveOut) {
            if (n != state.currentNode()) {
                state.moveTo(n);
            }
        }
    }
}
