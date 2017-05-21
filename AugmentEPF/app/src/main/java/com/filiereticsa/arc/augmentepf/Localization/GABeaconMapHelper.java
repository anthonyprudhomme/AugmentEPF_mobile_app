package com.filiereticsa.arc.augmentepf.Localization;

import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Created by anthonyprudhomme on 11/10/16.
 * Copyright © 2016 Granite Apps. All rights reserved.
 */
public class GABeaconMapHelper {

    private static final String TAG = "Ici";

    private class Edge  {
        Vertex vertex1;
        Vertex vertex2;
        int distance;

        public Edge(Vertex vertex1, Vertex vertex2, int distance) {
            this.vertex1 = vertex1;
            this.vertex2 = vertex2;
            this.distance = distance;
        }

        @Override
        public boolean equals(Object object) {
            boolean retVal = false;

            if (object instanceof Edge) {
                Edge edge = (Edge) object;
                retVal = (this.vertex1.indexPath.first.equals(edge.vertex1.indexPath.first)
                        && this.vertex1.indexPath.second.equals(edge.vertex1.indexPath.second)
                        && this.vertex2.indexPath.first.equals(edge.vertex2.indexPath.first)
                        && this.vertex2.indexPath.second.equals(edge.vertex2.indexPath.second))
                ||(this.vertex1.indexPath.first.equals(edge.vertex2.indexPath.first)
                        && this.vertex1.indexPath.second.equals(edge.vertex2.indexPath.second)
                        && this.vertex2.indexPath.first.equals(edge.vertex1.indexPath.first)
                        && this.vertex2.indexPath.second.equals(edge.vertex1.indexPath.second))
                ;
            }
            return retVal;
        }
    }

    private class Vertex {
        private Pair<Integer, Integer> indexPath;
        int distance = -1;
        Vertex predecessor;
        boolean visited = false;

        public Vertex(Pair<Integer, Integer> indexPath) {
            this.indexPath = indexPath;
        }

    }

    private ArrayList<Edge> graph;
    private Map<Pair<Integer, Integer>, Vertex> vertices;
    private Vertex globalCurrentVertex;


    private GABeaconMap map;

    public void setBeaconMap(GABeaconMap newBeaconMap) {
        this.map = newBeaconMap;
        this.buildGraph();
    }

    // MARK: - Path finding functions


    // Returns a dictionary with the distance from the provided index path to all the provided target index paths
    protected Map<Pair<Integer, Integer>, Pair<ArrayList<Pair<Integer, Integer>>, Integer>> pathsToTargets(Pair<Integer, Integer> origin, ArrayList<Pair<Integer, Integer>> targetIndexPaths) {
        Map<Pair<Integer, Integer>, Pair<ArrayList<Pair<Integer, Integer>>, Integer>> resultPaths = new HashMap<>();

        this.reinitPathFinding();
        // If our origin is not a known vertex, let's look for all vertices around our origin (in each direction)
        // And

        return resultPaths;
    }

    // Returns the distance and the path () from the provided index path to the provided target
    protected Pair<ArrayList<Pair<Integer, Integer>>, Integer> pathFrom(Pair<Integer, Integer> originIndexPath, Pair<Integer, Integer> targetIndexPath) {
        this.reinitPathFinding();
        if (this.vertices.get(originIndexPath) == null) {
            return null;
        }
        // Let's find out the neighbours around our indexPath and explore all directions

        this.vertices.get(originIndexPath).distance = 0;
        this.vertices.get(originIndexPath).visited = true;
        if (this.vertices.get(targetIndexPath) == null) {
            return null;
        }
        PriorityQueue<Vertex> queue = new PriorityQueue<>(vertices.size(), new Comparator<Vertex>() {
            @Override
            public int compare(Vertex vertex1, Vertex vertex2) {
                if (vertex1.distance > vertex2.distance) {
                    return 1;
                } else {
                    if (vertex1.distance < vertex2.distance) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            }
        });
        queue.add(this.vertices.get(originIndexPath));
        while (!queue.isEmpty()) {
            Vertex v = queue.poll();
            if (v == this.vertices.get(targetIndexPath)) {
                return this.backTrace(v);
            }

            ArrayList<Edge> edges = new ArrayList<>();
            for (int i = 0; i < this.graph.size(); i++) {
                if (this.graph.get(i).vertex1 == v || this.graph.get(i).vertex2 == v) {
                    edges.add(this.graph.get(i));
                }
            }
            for (int i = 0; i < edges.size(); i++) {
                Vertex nextVertex;
                if (edges.get(i).vertex1 == v) {
                    nextVertex = this.vertices.get(edges.get(i).vertex2.indexPath);
                } else {
                    nextVertex = this.vertices.get(edges.get(i).vertex1.indexPath);
                }
                int newDistance = v.distance + edges.get(i).distance;
                if (!nextVertex.visited && (nextVertex.distance > newDistance)) {
                    nextVertex.distance = newDistance;
                    nextVertex.predecessor = v;
                    queue.add(nextVertex);
                }
            }
        }
        return null;
    }

    // Return the closest index path from a provided vertex on the way to a second provided vertex
    protected Pair<Integer, Integer> indexPathAtDistance(int maxDistance, Pair<Integer, Integer> origin, Pair<Integer, Integer> target) {
        Pair<ArrayList<Pair<Integer, Integer>>, Integer> pair = this.pathFrom(origin, target);
        if (pair == null) {
            return null;
        }
        pair.first.add(target);
        if (pair.second <= maxDistance) {
            return target;
        }

        int distance = 0;
        Pair<Integer, Integer> v = origin;
        for (int i = 0; i < pair.first.size(); i++) {
            Pair<Integer, Integer> t = pair.first.get(i);
            int d = Math.max(Math.abs(t.first - v.first), Math.abs(t.second - v.second));
            distance += d;

            if (distance == maxDistance) {
                return t;
            }

            // If the distance is already smaller than the max distance,
            // then we should return the index path between v and t at max distance
            // (minus the distance travelled so far).
            if (distance > maxDistance) {
                int d_row = t.first < v.first ? -1 : 0;
                if (t.first > v.first) {
                    d_row = 1;
                }
                int d_section = t.second < v.second ? -1 : 0;
                if (t.second > v.second) {
                    d_section = 1;
                }
                int n = maxDistance + d - distance;
                return new Pair<>(v.first + n * d_row, v.second + n * d_section);
            }
            // Otherwise let's move on to the next vertex
            v = pair.first.get(i);
        }
        return pair.first.get(pair.first.size() - 1);
    }

    private void reinitPathFinding() {
        if (this.vertices != null) {
            for (Map.Entry<Pair<Integer, Integer>, Vertex> entry : this.vertices.entrySet()) {
                this.vertices.get(entry.getKey()).distance = Integer.MAX_VALUE;
                this.vertices.get(entry.getKey()).predecessor = null;
                this.vertices.get(entry.getKey()).visited = false;
            }
        }
    }

    private Pair<ArrayList<Pair<Integer, Integer>>, Integer> backTrace(Vertex vertex) {
        ArrayList<Pair<Integer, Integer>> path = new ArrayList<>();
        Vertex v = vertex.predecessor;
        if (v != null) {
            Vertex p = v.predecessor;
            while (p != null) {
                path.add(v.indexPath);
                v = p;
                p = v.predecessor;
            }
        }
        Collections.reverse(path);
        return new Pair<>(path, vertex.distance);
    }

    // MARK: - Other utility internal or public functions
    protected ArrayList<Pair<Integer, Integer>> neighboursIndexPaths(Pair<Integer, Integer> indexPath) { // O( 8 x ( n x (n+2) + 1)
        ArrayList<Pair<Integer, Integer>> neighbours = new ArrayList<>();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i != 0 || j != 0) {

                    MapItem item = null;
                    Pair<Integer,Integer> currentIndexPath = new Pair<>(indexPath.first + i, indexPath.second + j);

                    ArrayList<MapItem> mapItems =  map.mapItemsAtIndexPath(currentIndexPath); //O( n x (n+1))

                    if (mapItems != null && mapItems.size() != 0) {
                        item = mapItems.get(0); // O(1)
                    }
                    if (item != null) {
                        neighbours.add(currentIndexPath); //O(n)
                    }
                }
            }
        }

        return neighbours;
    }

    // MARK: - Graph building functions
    // Build edges with the provided intersections
    private void buildEdgesAndVertices(ArrayList<Integer> intersectionIds) {
        this.graph = new ArrayList<>();
        this.vertices = new HashMap<>();
        for (int i = 0; i < intersectionIds.size(); i++) {
            MapItem originItem = map.mapItemWithId(i);
            if (originItem == null) {
                return;
            }
            // Create vertex if necessary
            globalCurrentVertex = this.vertices.get(originItem.coordinates);
            if (globalCurrentVertex != null) {
                this.exploreAndBuildFromVertex(null);
                this.vertices.put(originItem.coordinates, globalCurrentVertex);
            } else {
                globalCurrentVertex = new Vertex(originItem.coordinates);
                this.vertices.put(globalCurrentVertex.indexPath, globalCurrentVertex);
                this.exploreAndBuildFromVertex(null);
                this.vertices.put(globalCurrentVertex.indexPath, globalCurrentVertex);
            }
        }
    }

    private ArrayList<Integer> exploreAndBuildFromVertex(Pair<Integer, Integer> direction) {
        globalCurrentVertex.visited = true;
        if (direction != null) {
            Pair<ArrayList<MapItem>, Integer> nextVertices = this.nextVertexItems(globalCurrentVertex.indexPath, direction, 0);
            Vertex currentVertex = globalCurrentVertex;
            if (nextVertices != null) {
                while (nextVertices.first.size() > 0) {
                    // If a vertex already exist, and it was already visited
                    Vertex nextVertex = this.vertices.get(nextVertices.first.get(0).coordinates);
                    if (nextVertex == null) {
                        // Create vertex for target
                        nextVertex = new Vertex(nextVertices.first.get(0).coordinates);
                        this.vertices.put(nextVertices.first.get(0).coordinates, nextVertex);
                    }
                    if (!nextVertex.visited) {
                        // Create edge
                        this.graph.add(new Edge(currentVertex, nextVertex, nextVertices.second));

                        // Return reached intersections if any
                        MapItem intersectionItem = null;
                        for (int i = 0; i < nextVertices.first.size(); i++) {
                            if (nextVertices.first.get(i).type == MapItem.MapItemType.Intersection) {
                                intersectionItem = nextVertices.first.get(i);
                                break;
                            }
                        }
                        if (intersectionItem != null) {
                            ArrayList<Integer> listToReturn = new ArrayList<>();
                            listToReturn.add(intersectionItem.getId());
                            return listToReturn;
                        }
                        nextVertex.visited = true;
                        nextVertices = this.nextVertexItems(nextVertex.indexPath, direction, 0);
                        currentVertex = nextVertex;
                    } else {
                        return null;
                    }
                }
            }
        } else {
            // Explore in all directions
            ArrayList<Integer> reachedIntersections = new ArrayList<>();
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (i != 0 || j != 0) {
                        ArrayList<Integer> intersections = this.exploreAndBuildFromVertex(new Pair<>(i, j));
                        if (intersections != null) {
                            reachedIntersections.addAll(intersections);
                        }
                    }
                }
            }
        }
        return null;
    }


    // Return the next found items that are not free in the provided direction
    private Pair<ArrayList<MapItem>, Integer> nextVertexItems(Pair<Integer, Integer> originIndexPath, Pair<Integer, Integer> direction, int distance) {
        ArrayList<MapItem> nextItems = map.mapItemsAtIndexPath(new Pair<>(originIndexPath.first + direction.first, originIndexPath.second + direction.second));
        if (nextItems.size() == 0) {
            return null;
        }
        ArrayList<MapItem> nextVertices = new ArrayList<>();
        for (int i = 0; i < nextItems.size(); i++) {
            if (nextItems.get(i).type != MapItem.MapItemType.Free) {
                nextVertices.add(nextItems.get(i));
            }
        }
        if (nextVertices.size() > 0) {
            return new Pair<>(nextVertices, 1 + distance);
        } else {
            return nextVertexItems(nextItems.get(0).coordinates, direction, 1 + distance);
        }
    }

    private void buildGraphFromIndexPath(Pair<Integer, Integer> indexPath) { // O( 11n^2 + 18n)
        Vertex v = this.vertices.get(indexPath);
        if (v == null) {
            v = new Vertex(indexPath);
            this.vertices.put(indexPath, v);
        }
        ArrayList<Pair<Integer, Integer>> neighboursIndexPaths = this.neighboursIndexPaths(indexPath); // O( 8 x ( n x (n+2) + 1)
        for (int i = 0; i < neighboursIndexPaths.size(); i++) { // O (n x (3n+1))
            Pair<Integer, Integer> currentPair = neighboursIndexPaths.get(i); // O(1)
            Vertex nv = this.vertices.get(currentPair); // O(1)
            if (nv == null) {
                nv = new Vertex(currentPair);
                this.vertices.put(currentPair, nv); // O (n)
            }
            Edge e = new Edge(v, nv, 1);
            if (!this.graph.contains(e)) { // O(n)
                this.graph.add(e); //O(n)
            }
        }
    }

    private void buildGraph() { // O(11n^3 + 19n^2)
        ArrayList<Pair<Integer, Integer>> mapItemsIndexPathsToVisit = new ArrayList<>();
        ArrayList<MapItem> mapItems = this.map.getMapItems(); // O(n)
        for (int i = 0; i < mapItems.size(); i++) { // O(n^2)
            mapItemsIndexPathsToVisit.add(mapItems.get(i).coordinates);
        }
        if (mapItemsIndexPathsToVisit.size() != 0) {
            graph = new ArrayList<>();
            vertices = new HashMap<>();
            while (mapItemsIndexPathsToVisit.size() > 0) { // O(n x (11n^2 + 18n))
                this.buildGraphFromIndexPath(mapItemsIndexPathsToVisit.remove(0)); // O( 11n^2 + 18n)
            }
        } else {
            graph = null;
            vertices = null;
        }
    }
}
