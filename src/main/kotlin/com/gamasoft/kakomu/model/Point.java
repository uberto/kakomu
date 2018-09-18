package com.gamasoft.kakomu.model;

__ByValue public class Point {

    final static String COLS = "ABCDEFGHJKLMNOPQRSTUVWXYZ";

    public final int col;
    public final int row;

    public Point(int col, int row) {
        this.col = col;
        this.row = row;
    }

    public static Point fromCoords(String coords) {

        if (coords.length() < 2 || coords.length() > 3)
            return new Point(0,0);

        String colS = coords.substring(0, 1).toUpperCase();
        String rowS = coords.substring(1);

        int col = COLS.indexOf(colS) + 1;
        int row = Integer.valueOf(rowS);

        return new Point(row, col);

    }

    public static String toCoords(Point point) {
        return COLS.substring(point.col - 1, point.col) + String.valueOf( point.row);
    }
}
