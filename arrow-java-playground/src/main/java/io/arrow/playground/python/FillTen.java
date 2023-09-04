package io.arrow.playground.python;

import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.vector.BigIntVector;


public class FillTen {
    static RootAllocator allocator = new RootAllocator();

    public static BigIntVector createArray() {
        BigIntVector intVector = new BigIntVector("ints", allocator);
        intVector.allocateNew(10);
        intVector.setValueCount(10);
        FillTen.fillVector(intVector);
        return intVector;
    }

    private static void fillVector(BigIntVector iv) {
        iv.setSafe(0, 1);
        iv.setSafe(1, 2);
        iv.setSafe(2, 3);
        iv.setSafe(3, 4);
        iv.setSafe(4, 5);
        iv.setSafe(5, 6);
        iv.setSafe(6, 7);
        iv.setSafe(7, 8);
        iv.setSafe(8, 9);
        iv.setSafe(9, 10);
    }
}
