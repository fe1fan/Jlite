package io.xka.jlite.example;

import java.util.Arrays;

public class Main {
    static int INF = Integer.MAX_VALUE;  // 定义一个极大值常量
    static int V = 5;  // 图中节点的个数

    public static void main(String[] args) {
        // 模拟一个5个节点的图
        int[][] graph = new int[][]{
                {0, 10, 3, INF, INF},
                {10, 0, 6, 7, INF},
                {3, 6, 0, INF, 8},
                {INF, 7, INF, 0, 2},
                {INF, INF, 8, 2, 0}
        };

        dijkstra(graph, 0);  //计算从节点0开始到其它节点的最短路径
    }

    public static void dijkstra(int[][] graph, int src) {
        int[] dist = new int[V];  // 存储从源节点到各个节点的最短距离
        boolean[] visited = new boolean[V];  // 存储节点是否已经被遍历

        Arrays.fill(dist, INF);  // 初始化最短距离数组
        dist[src] = 0;  // 源节点到自身的最短距离为0

        // 计算最短距离
        for (int count = 0; count < V - 1; count++) {
            int u = -1;
            for (int i = 0; i < V; i++) {
                if (!visited[i] && (u == -1 || dist[i] < dist[u])) {
                    u = i;
                }
            }

            visited[u] = true;
            for (int v = 0; v < V; v++) {
                if (graph[u][v] != INF && !visited[v]) {
                    dist[v] = Math.min(dist[v], dist[u] + graph[u][v]);
                }
            }
        }

        // 输出结果
        for (int i = 0; i < V; i++) {
            System.out.println("从源节点到节点 " + i + " 的最短距离为：" + dist[i]);
        }
    }
}

