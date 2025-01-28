package de.tum.cit.ase.bomberquest.bonusFeatures;

import com.badlogic.gdx.math.Vector2;
import de.tum.cit.ase.bomberquest.map.GameMap;

import java.util.*;

/**
 * A very basic implementation of the A* pathfinding algorithm.
 * This algorithm is used to find the shortest path between two points on a grid-based map.
 * It works by evaluating possible paths, prioritizing the ones that seem most promising
 * based on the cost to reach a point and an estimate of how close that point is to the goal.
 */
public class AStarPathFinder {

    // Inner class for representing grid nodes
    // Each "Node" represents a single tile or position on the game map grid.
    private static class Node {
        int x, y; // The position of this node in the grid
        float costFromStart; // The actual cost to move from the start node to this node
        float estimatedCostToGoal; // Heuristic: an estimate of the remaining cost to the goal
        Node previousNode; // A reference to the previous node on the shortest path
        boolean walkable; // Indicates if this tile can be traversed (e.g., not blocked by walls)

        /**
         * Constructor for a Node.
         *
         * @param x The x-coordinate of the node
         * @param y The y-coordinate of the node
         * @param walkable Whether the tile at this position can be walked on
         */
        public Node(int x, int y, boolean walkable) {
            this.x = x;
            this.y = y;
            this.walkable = walkable;
            this.costFromStart = Float.MAX_VALUE; // Initialize with a high cost
            this.estimatedCostToGoal = 0; // Start with zero estimated cost
            this.previousNode = null; // No parent node yet
        }

        /**
         * Calculates the total cost for this node.
         * total cost is the sum of costFromStart and estimatedCostToGoal.
         *
         * @return The total cost for this node
         */
        public float getTotalCost() {
            return costFromStart + estimatedCostToGoal;
        }
    }

    /**
     * This method finds a path from a starting point to a goal point using the A* algorithm.
     * The algorithm explores possible paths by moving through neighboring tiles and calculates
     * the cost of each move, always focusing on the most promising path until the goal is reached.
     *
     * @param gameMap The grid-based map where the pathfinding is happening
     * @param start The starting position of the path (Vector2 with x and y coordinates)
     * @param goal The target position of the path (Vector2 with x and y coordinates)
     * @return A list of Vector2 objects representing the path from start to goal, or an empty list if no path is found
     */
    public static List<Vector2> calculatePath(GameMap gameMap, Vector2 start, Vector2 goal) {
        // Get the dimensions of the game map
        int mapWidth = gameMap.getWidth();
        int mapHeight = gameMap.getHeight();

        // Create a grid of nodes representing each tile in the map
        Node[][] grid = new Node[mapWidth][mapHeight];

        // Initialize the grid by creating a Node for each tile
        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                grid[x][y] = new Node(x, y, gameMap.isTileWalkable(x, y)); // Check if the tile is walkable
            }
        }

        // Convert the start and goal positions from float (Vector2) to integers for grid indexing
        int startX = (int) start.x;
        int startY = (int) start.y;
        int goalX = (int) goal.x;
        int goalY = (int) goal.y;

        // Check if either the start or goal position is invalid (not walkable)
        if (!grid[startX][startY].walkable || !grid[goalX][goalY].walkable) {
            // If either position is not traversable, return an empty path
            return new ArrayList<>();
        }

        // Initialize the lists for open and closed nodes
        List<Node> toExplore = new ArrayList<>(); // Open list: nodes yet to be evaluated
        List<Node> alreadyExplored = new ArrayList<>(); // Closed list: nodes already evaluated

        // Set up the start node with initial values
        Node startNode = grid[startX][startY];
        startNode.costFromStart = 0; // Cost to reach the start node is zero
        startNode.estimatedCostToGoal = calculateManhattanDistance(startX, startY, goalX, goalY); // Calculate estimated cost to reach the goal tile
        toExplore.add(startNode); // Add the start node to the open list

        // Main loop: continue until all nodes are evaluated or the goal is reached
        while (!toExplore.isEmpty()) {
            // Find the node in the open list with the lowest total cost
            Node currentNode = toExplore.get(0);
            for (Node node : toExplore) {
                //If the totalCost of this new node is smaller than the current note that is "the best"
                if (node.getTotalCost() < currentNode.getTotalCost()) {
                    currentNode = node; // Update this node to be the "best candidate".
                }
            }

            // Move the current node from the open list to the closed list
            toExplore.remove(currentNode);
            alreadyExplored.add(currentNode);

            // Check if we have reached the goal
            if (currentNode.x == goalX && currentNode.y == goalY) {
                // If the goal is reached, we need to build the path and return it.
                return buildPath(currentNode);
            }

            // Check all neighboring nodes of the current node
            int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}}; // Directions: up, down, right, left
            for (int[] direction : directions) {
                int neighborX = currentNode.x + direction[0];
                int neighborY = currentNode.y + direction[1];

                // Skip neighbors that are out of map bounds
                if (neighborX < 0 || neighborY < 0 || neighborX >= mapWidth || neighborY >= mapHeight) {
                    continue;
                }

                // Get the neighbor node from the grid
                Node neighborNode = grid[neighborX][neighborY];

                // Skip if the neighbor is not walkable or already evaluated
                if (!neighborNode.walkable || alreadyExplored.contains(neighborNode)) {
                    continue;
                }

                // Calculate the tentative gCost to reach this neighbor
                float newCostFromStart = currentNode.costFromStart + 1; // Assume each move has a cost of 1
                if (newCostFromStart < neighborNode.costFromStart) {
                    // If this path is better, update the neighbor's costs and parent
                    neighborNode.costFromStart = newCostFromStart;
                    neighborNode.estimatedCostToGoal = calculateManhattanDistance(neighborX, neighborY, goalX, goalY);
                    neighborNode.previousNode = currentNode; // Set the current node as the parent

                    // Add the neighbor to the open list if not already there
                    if (!toExplore.contains(neighborNode)) {
                        toExplore.add(neighborNode);
                    }
                }
            }
        }

        // If no path is found, return an empty list
        return new ArrayList<>();
    }

    /**
     * Calculates the Manhattan distance between two points.
     * This heuristic assumes movement is only allowed along grid lines (horizontal/vertical).
     */
    private static float calculateManhattanDistance(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    /**
     * Reconstructs the path from the goal node to the start node.
     * This method traces back from the goal to the start using the previousNode references
     * and builds the path in reverse order.
     *
     * @param goalNode The node representing the goal position
     * @return A list of Vector2 objects representing the path, ordered from start to goal
     */
    private static List<Vector2> buildPath(Node goalNode) {
        List<Vector2> path = new ArrayList<>();
        Node currentNode = goalNode;
        while (currentNode != null) {
            // Add each node's position to the path, inserting at the beginning to reverse order
            path.add(0, new Vector2(currentNode.x, currentNode.y));
            currentNode = currentNode.previousNode; // Move to the parent node
        }
        return path; // Return the reconstructed path
    }
}
