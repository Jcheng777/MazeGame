
package submit;

/* NetId(s):

 * Name(s):
 * What I thought about this assignment:
 *
 *
 */

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import graph.Edge;
import graph.Node;

/** This class implements the shortest-path algorithm <br>
 * and other methods for an undirected graph. */
public class Path {

    /** Replace "-1" by the time you spent on A2 in hours.<br>
     * Example: for 3 hours 15 minutes, use 3.25<br>
     * Example: for 4 hours 30 minutes, use 4.50<br>
     * Example: for 5 hours, use 5 or 5.0 */
    public static double timeSpent= -1;

    /** = the shortest path from node v to node end <br>
     * ---or the empty list if a path does not exist. <br>
     * Note: The empty list is a list with 0 elements ---it is not "null". */
    public static List<Node> shortestPath(Node v, Node end) {
        /* TODO Implement this method.
         * Read the A6 assignment handout for all details.
         * Remember, the graph is undirected. */

        // Contains an entry for each node in the frontier set. The priority of
        // a node is the length of the shortest known path from v to the node
        // using only settled nodes except for the last node, which is in F.
        var F= new Heap<Node>(true);

        // Put in a declaration of the HashMap here, with a suitable name
        // for it and a suitable definition of its meaning --what it contains,
        // etc. See Section 10 point 4 of the A7 handout for help.

        // Replace arrays d and bk and set S of the abstract algorithm by a single
        // appropriately-named variable of type
        // HashMap<Node, Info>, which should contain an entry for each node of Settled set S and
        // Frontier set F. Do not add
        // any other data structure.
        HashMap<Node, Info> S= new HashMap<>();
        // S contains union of frontier and settled set S!

        // S= {}; F= {v}; d[v]= 0;
        // invariant: pts (1)..(3) given above
        S.put(v, new Info(0, null));
        // Starting node
        F.insert(v, 0);
        // Set start node to part of frontier

        while (F.size() > 0) {
            // While frontier F is not empty

            Node f= F.poll();
            // in F with minimum d value;
            // Remove f from F, add it to S in for loop

            // Condition for creating new path once reach the end point
            if (f.equals(end)) { return pathToEnd(S, f); }

            // Logic for Organizing S and F
            for (Edge e : f.exits()) {
                // for each neighbor w of f {
                Node w= e.other(f);
                int dW= S.get(f).dist;// Distance from start to neighbor w
                if (!S.containsKey(w)) {
                    dW= S.get(f).dist + e.length;
                    F.insert(w, dW);
                    S.put(w, new Info(dW, f));
                    // if (w is not in S or F) {
                    // d[w]= d[f] + wgt(f, w);
                    // add w to F; bk[w]= f;?????????
                } else if (dW + e.length < S.get(w).dist) {
                    S.replace(w, new Info(dW + e.length, f));
                    // Update Priority heap
                    F.changePriority(w, dW + e.length);
                    // else if (d[f] + wgt(f,w) < d[w]) {
                    // d[w]= d[f] + wgt(f, w);
                    // bk[w]= f;

                }
            }
        }

        /*Graph methods available: n.exits(): Return a List<Edge> of edges that leave Node n.
        • e.other(n): n must be one of the nodes of Edge e. Return the other Node.
        • e.length: The length of Edge e.*/

        // Put all your code before this comment. Do not change this comment or return statement
        // no path from v to end.
        return new LinkedList<>();
    }

    /** An instance contains info about a node: <br>
     * the known shortest distance of this node from the start node and <br>
     * its backpointer: the previous node on a shortest path <br>
     * from the first node to this node (null for the start node). */
    private static class Info {
        /** shortest known distance from the start node to this one. */
        private int dist;
        /** backpointer on path (with shortest known distance) from <br>
         * start node to this one */
        private Node bkptr;

        /** Constructor: an instance with dist d from the start node and<br>
         * backpointer p. */
        private Info(int d, Node p) {
            dist= d;     // Distance from start node to this one.
            bkptr= p;    // Backpointer on the path (null if start node)
        }

        /** = a representation of this instance. */
        @Override
        public String toString() {
            return "dist " + dist + ", bckptr " + bkptr;
        }
    }

    /** = the path from the start node to node end.<br>
     * Precondition: SandF contains all the necessary information about<br>
     * ............. the path. */
    public static List<Node> pathToEnd(HashMap<Node, Info> SandF, Node end) {
        List<Node> path= new LinkedList<>();
        var p= end;
        // invariant: All the nodes from p's successor to node
        // . . . . . .end are in path, in reverse order.
        while (p != null) {
            path.add(0, p);
            p= SandF.get(p).bkptr;
        }
        return path;
    }

    /** = the sum of the weights of the edges on path p. <br>
     * Precondition: p contains at least 1 node. <br>
     * If 1 node, it's a path of length 0, i.e. with no edges. */
    public static int pathSum(List<Node> p) {
        synchronized (p) {
            Node w= null;
            var sum= 0;
            // invariant: if w is null, n is the start node of the path.<br>
            // .......... if w is not null, w is the predecessor of n on the path.
            // .......... sum = sum of weights on edges from first node to v
            for (Node n : p) {
                if (w != null) sum= sum + w.edge(n).length;
                w= n;
            }
            return sum;
        }
    }

}
