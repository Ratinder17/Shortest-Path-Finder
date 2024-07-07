package com.techweb.helloworld;

import com.google.maps.errors.ApiException;
import java.io.IOException;
import java.util.*;

public class Dijkstra {
    private final Map<String, List<Node>> graph;
    private final DistanceService distanceService;
    private final Map<String, String> predecessors;

    public Dijkstra(DistanceService distanceService) {
        this.distanceService = distanceService;
        this.graph = new HashMap<>();
        this.predecessors = new HashMap<>(); 
    }

    public void addRoad(String source, String destination) throws InterruptedException, ApiException, IOException {
        try {
            long distance = distanceService.getDistance(source, destination) / 1000; // convert meters to kilometers
            graph.computeIfAbsent(source, k -> new ArrayList<>()).add(new Node(destination, (int) distance));
            graph.computeIfAbsent(destination, k -> new ArrayList<>()).add(new Node(source, (int) distance));
        } catch (IOException e) {
            System.err.println("Failed to add road from " + source + " to " + destination + ": " + e.getMessage());
            throw e; 
        }
    }


    public Map<String, Integer> shortestPath(String source) {
        PriorityQueue<Node> queue = new PriorityQueue<>();
        Map<String, Integer> distances = new HashMap<>();
        Set<String> visited = new HashSet<>();

        queue.add(new Node(source, 0));
        distances.put(source, 0);

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            if (!visited.add(current.city)) {
                continue;
            }

            for (Node neighbor : graph.getOrDefault(current.city, Collections.emptyList())) {
                if (visited.contains(neighbor.city)) {
                    continue;
                }

                int newDist = distances.get(current.city) + neighbor.cost;
                if (newDist < distances.getOrDefault(neighbor.city, Integer.MAX_VALUE)) {
                    distances.put(neighbor.city, newDist);
                    queue.add(new Node(neighbor.city, newDist));
                    predecessors.put(neighbor.city, current.city);
                }
            }
        }

        return distances;
    }

    public List<String> getPath(String target) {
        List<String> path = new ArrayList<>();
        String step = target;

        if (predecessors.get(step) == null) {
            return null; // No path found
        }

        path.add(step);
        while (predecessors.get(step) != null) {
            step = predecessors.get(step);
            path.add(step);
        }

        Collections.reverse(path);
        return path;
    }

    public void buildGraph(List<String> cities) throws InterruptedException, ApiException, IOException {
        for (int i = 0; i < cities.size(); i++) {
            for (int j = i + 1; j < cities.size(); j++) {
                try {
                    addRoad(cities.get(i), cities.get(j));
                } catch (IOException e) {
                    System.err.println("Failed to add road from " + cities.get(i) + " to " + cities.get(j) + ": " + e.getMessage());
                }
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("**************************Cities in Arizona***********************************");
        List<String> cities = Arrays.asList("Phoenix", "Tempe", "Tucson", "Mesa", "Chandler", "Gilbert", "Glendale", "Scottsdale", "Flagstaff", "Lake Havasu City", "Buckeye", "Yuma", "Casa Grande", "Prescott");
        for(int i = 0;i<cities.size();i++) {
        	System.out.println("-> " + cities.get(i));
        }
        

        try {
            DistanceService distanceService = new DistanceService("AIzaSyDez5mRw1nvOZaEsLzNqaYKsOoQ8-_Bh7w");
            Dijkstra dijkstra = new Dijkstra(distanceService);
           

            dijkstra.buildGraph(cities);

            System.out.println("Enter the source city:");
            String source = scanner.nextLine();
            System.out.println("Enter the destination city:");
            String destination = scanner.nextLine();

            Map<String, Integer> distances = dijkstra.shortestPath(source);

            if (distances.containsKey(destination)) {
                System.out.println("Shortest distance from " + source + " to " + destination + " is " + distances.get(destination) + " kilometers.");
                List<String> path = dijkstra.getPath(destination);
                StringBuilder sb = new StringBuilder("Path: ");
                for (int i = 0; i < path.size(); i++) {
                    sb.append(path.get(i));
                    if (i < path.size() - 1) {
                        sb.append(" -> ");
                    }
                }
                System.out.println(sb.toString());
            } else {
                System.out.println("No path found from " + source + " to " + destination + ".");
            }

            distanceService.close();
        } catch (Exception e) {
            e.printStackTrace();
        } 
        scanner.close();
    }
}

