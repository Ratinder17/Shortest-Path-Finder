package com.techweb.helloworld;

public class Node implements Comparable<Node> {
    public String city;
    public int cost;

    public Node(String city, int cost) {
        this.city = city;
        this.cost = cost;
    }

    @Override
    public int compareTo(Node other) {
        return Integer.compare(this.cost, other.cost);
    }
}