import java.util.*;
import java.io.*;

public class Main {
	static class Unit {
        int id;
        int x;
        int y;
        int dir;

        public Unit(int id, int x, int y, int dir) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.dir = dir;
        }
    }

    static Unit[] units;
    static int R, C, K, res = 0;
    static int[][] arr;
    static int[][] exitMap;
    static int[] maxRowValue;
    static int[] parent;

    static int[] dx = {-1, 0, 1, 0};
    static int[] dy = {0, 1, 0, -1};

    static int[] tx = {2, 1, 1};
    static int[] ty = {0, -1, 1};

    static int[] lx = {0, 1, -1, 1, 2};
    static int[] ly = {-2, -1, -1, -2, -1};

    static int[] rx = {0, 1, -1, 1, 2};
    static int[] ry = {2, 1, 1, 2, 1};

    static int[] ex = {-2, -1, 0, 1, 2, 1, 0, -1};
    static int[] ey = {0, 1, 2, 1, 0, -1, -2, -1};

    public static void main(String[] args) throws Exception{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        R = Integer.parseInt(st.nextToken());
        C = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());

        init();

        maxRowValue = new int[K + 1];
        parent = new int[K + 1];
        for (int i = 0; i <= K; i++) {
            parent[i] = i;
        }
        units = new Unit[K + 1];

        for (int i = 1; i <= K; i++) {
            st = new StringTokenizer(br.readLine());
            units[i] = new Unit(i, 1,Integer.parseInt(st.nextToken()) - 1, Integer.parseInt(st.nextToken()));
            simulation(units[i]);
        }

        System.out.println(res);


    }

    /**
     * 초기화
     */
    static void init() {
        arr = new int[R + 3][C];
        exitMap = new int[R + 3][C];
    }

    /**
     * 골렘에 대해서 시뮬레이션
     */
    static void simulation(Unit unit) {
        while (true) {
            // 남쪽 이동 가능 여부
            if (canMove(unit, tx, ty)) {
                unit.x++;
                continue;
            }
            // 서쪽 이동 가능 여부
            if (canMove(unit, lx, ly)) {
                unit.dir = (unit.dir + 3) % 4; // 반시계 방향
                unit.x++;
                unit.y--;
                continue;
            }
            // 동쪽 이동 가능 여부
            if (canMove(unit, rx, ry)) {
                unit.dir = (unit.dir + 1) % 4;
                unit.x++;
                unit.y++;
                continue;
            }
            break;
        }

        // 만약 범위를 벗어나있다면 맵을 초기화
        if (isOut(unit)) {
            init();
            return;
        }

        exitMap[unit.x + dx[unit.dir]][unit.y + dy[unit.dir]] = unit.id;
        arr[unit.x][unit.y] = unit.id;
        for (int d = 0; d < 4; d++) {
            arr[unit.x + dx[d]][unit.y + dy[d]] = unit.id;
        }

        maxRowValue[unit.id] = unit.x - 1;

        int exitX = unit.x + dx[unit.dir];
        int exitY = unit.y + dy[unit.dir];
        for (int d = 0; d < 4; d++) {
            int nx = exitX + dx[d];
            int ny = exitY + dy[d];
            if (isInRange(nx, ny) && arr[nx][ny] != 0) {
                if (arr[nx][ny] == unit.id) {
                    continue;
                }
                int n = arr[nx][ny];
                if (maxRowValue[n] > maxRowValue[unit.id]) {
                    maxRowValue[unit.id] = maxRowValue[n];
                    parent[unit.id] = find(parent[n]);
                }
            }
        }

        res += maxRowValue[unit.id];

        boolean[] check = new boolean[K + 1];
        check[unit.id] = true;
        Queue<Integer> q = new ArrayDeque<>();
        q.add(unit.id);
        while (!q.isEmpty()) {
            int id = q.poll();
            for (int d = 0; d < 8; d++) {
                int nx = units[id].x + ex[d];
                int ny = units[id].y + ey[d];
                if (isInRange(nx, ny) && exitMap[nx][ny] != 0 && !check[exitMap[nx][ny]]) {
                    int near = exitMap[nx][ny];
                    check[near] = true;
                    q.add(near);
                    if (maxRowValue[unit.id] > maxRowValue[near]) {
                        parent[near] = find(parent[unit.id]);
                        maxRowValue[near] = maxRowValue[unit.id];
                    }
                }
            }
        }

    }

    static int find(int n) {
        if (parent[n] == n) {
            return n;
        } else {
            return parent[n] = find(parent[n]);
        }
    }

    private static boolean isOut(Unit unit) {
        for (int d = 0; d < 4; d++) {
            if (!isInForest(unit.x + dx[d], unit.y + dy[d])) {
                return true;
            }
        }
        return false;
    }

    /**
     * 골렘이 숲내부에 존재 여부
     */
    private static boolean isInForest(int x, int y) {
        if (x >= 3 && x < R + 3 && y >= 0 && y < C) {
            return true;
        }
        return false;
    }

    /**
     * 골렘의 이동 가능 여부
     */
    static boolean canMove(Unit unit, int[] xArr, int[] yArr) {
        int len = xArr.length;

        for (int d = 0; d < len; d++) {
            int nx = unit.x + xArr[d];
            int ny = unit.y + yArr[d];

            if (!(isInRange(nx, ny) && arr[nx][ny] == 0)) return false;
        }
        return true;
    }

    /**
     * 범위 체크
     */
    static boolean isInRange(int x, int y) {
        if (x >= 0 && x < R + 3 && y >= 0 && y < C) {
            return true;
        }
        return false;
    }
}