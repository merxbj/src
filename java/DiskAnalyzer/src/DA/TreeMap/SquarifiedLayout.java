/**
 * Copyright (C) 2001 by University of Maryland, College Park, MD 20742, USA 
 * and Martin Wattenberg, w@bewitched.com
 * All rights reserved.
 * Authors: Benjamin B. Bederson and Martin Wattenberg
 * http://www.cs.umd.edu/hcil/treemaps
 */
package DA.TreeMap;

import DA.common.SimpleFileComparator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * "Squarified" treemap layout invented by
 * J.J. van Wijk.
 */
public final class SquarifiedLayout {

    public static final int VERTICAL = 0, HORIZONTAL = 1;
    public static final int ASCENDING = 0, DESCENDING = 1;
    private static final int MIN_WIDTH = 25, MIN_HEIGHT = 25;

    public static void layout(SimpleFile file, Rect bounds) {
        layout(file.getContent(), bounds);
    }

    public static void layout(List<SimpleFile> items, Rect bounds) {

        layout(sortDescending(items), 0, items.size() - 1, bounds);
    }

    public static void layout(List<SimpleFile> items, int start, int end, Rect bounds) {
        if ((start > end)) {
            return;
        }

        if (end - start < 2) {
            layoutBest(items, start, end, bounds);
            return;
        }
        double x = bounds.x, y = bounds.y, width = bounds.width, height = bounds.height;
        //end = getNewEnd(items, start, end, width, height);
        double total;
        try {
            total = sum(items, start, end);
        } catch (Exception e) {
            return;
        }

        int mid = start;
        double a = items.get(start).getSize() / total;
        double b = a;

        if (width < height) { // height/width
            while (mid <= end) {
                double aspect = normAspect(height, width, a, b);
                double q = items.get(mid).getSize() / total;
                if (normAspect(height, width, a, b + q) > aspect) {
                    break;
                }
                mid++;
                b += q;
            }
            layoutBest(items, start, mid, new Rect(x, y, width, height * b));
            layout(items, mid + 1, end, new Rect(x, y + height * b, width, height * (1 - b)));
        } else { // width/height
            while (mid <= end) {
                double aspect = normAspect(width, height, a, b);
                double q = items.get(mid).getSize() / total;
                if (normAspect(width, height, a, b + q) > aspect) {
                    break;
                }
                mid++;
                b += q;
            }
            layoutBest(items, start, mid, new Rect(x, y, width * b, height));
            layout(items, mid + 1, end, new Rect(x + width * b, y, width * (1 - b), height));
        }

    }

   /* private static int getNewEnd(List<SimpleFile> items, int start, int end, double width, double height) {
        if ((start > end)) {
            return 0;
        }
        double total;
        try {
            total = sum(items, start, end);
        } catch (Exception e) {
            return 0;
        }

        int mid = start;
        double a = items.get(start).getSize() / total;
        double b = a;
        if (width < height) { // height/width
            while (mid <= end) {
                double aspect = normAspect(height, width, a, b);
                double q = items.get(mid).getSize() / total;
                if (normAspect(height, width, a, b + q) > aspect) {
                    break;
                }
                mid++;
                b += q;
            }
            if (height * b >= MIN_HEIGHT) {
                end = getNewEnd(items, mid + 1, end, width, height * b);
            } else {
                return end--;
            }
        } else { // width/height
            while (mid <= end) {
                double aspect = normAspect(width, height, a, b);
                double q = items.get(mid).getSize() / total;
                if (normAspect(width, height, a, b + q) > aspect) {
                    break;
                }
                mid++;
                b += q;
            }
            if (width * b >= MIN_HEIGHT) {
                end = getNewEnd(items, mid + 1, end, width * b, height);
            } else {
                return end--;
            }
        }

        return end;
    }*/

    private static void layoutBest(List<SimpleFile> items, int start, int end, Rect bounds) {
        sliceLayout(items, start, end, bounds, bounds.width > bounds.height ? HORIZONTAL : VERTICAL, ASCENDING);
    }

    private static void sliceLayout(List<SimpleFile> items, int start, int end, Rect bounds, int orientation, int order) {
        double total = 0;
        try {
            total = sum(items, start, end);
        } catch (Exception e) {
            return;
        }

        double sideA = 0;
        for (int i = start; i <= end; i++) {
            Rect rectangle = new Rect();
            double sideB = items.get(i).getSize() / total;
            if (orientation == VERTICAL) {
                rectangle.x = bounds.x;
                rectangle.width = bounds.width;
                if (order == ASCENDING) {
                    rectangle.y = bounds.y + bounds.height * sideA;
                } else {
                    rectangle.y = bounds.y + bounds.height * (1 - sideA - sideB);
                }
                rectangle.height = bounds.height * sideB;
            } else {
                if (order == ASCENDING) {
                    rectangle.x = bounds.x + bounds.width * sideA;
                } else {
                    rectangle.x = bounds.x + bounds.width * (1 - sideA - sideB);
                }
                rectangle.width = bounds.width * sideB;
                rectangle.y = bounds.y;
                rectangle.height = bounds.height;
            }
            items.get(i).setBounds(rectangle);
            sideA += sideB;
        }
    }

    private static double aspect(double big, double small, double a, double b) {
        return (big * b) / (small * a / b);
    }

    private static double normAspect(double big, double small, double a, double b) {
        double x = aspect(big, small, a, b);
        if (x < 1) {
            return 1 / x;
        }
        return x;
    }

    private static double sum(List<SimpleFile> items, int start, int end) {
        double s = 0;
        for (int i = start; i <= end; i++) {
            s += items.get(i).getSize();
        }
        return s;
    }

    private static List<SimpleFile> sortDescending(List<SimpleFile> items) {
        List<SimpleFile> sortedList = new ArrayList<SimpleFile>();
        sortedList.addAll(items);
        Collections.sort(sortedList, SimpleFileComparator.SORT_BY_SIZE);

        return sortedList;
    }

    private static void resetBounds(List<SimpleFile> items, int start, int end) {
        for (int i = start; i <= end; i++) {
            try {
                items.get(i).setBounds(null);
            } catch (Exception e) {
            };
        }
    }

    public String getName() {
        return "Squarified";
    }

    public String getDescription() {
        return "Algorithm used by J.J. van Wijk.";
    }
}
