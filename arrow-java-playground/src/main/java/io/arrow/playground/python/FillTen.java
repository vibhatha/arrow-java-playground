package io.arrow.playground.python;

import org.apache.arrow.c.ArrowArray;
import org.apache.arrow.c.ArrowSchema;
import org.apache.arrow.c.Data;
import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.vector.BigIntVector;
import org.apache.arrow.vector.FieldVector;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.apache.arrow.vector.compare.VectorEqualsVisitor;


public class FillTen {
    static RootAllocator allocator = new RootAllocator();

    private BigIntVector intVector;

    public FillTen() {}

    public BigIntVector createArray() {
        this.intVector = new BigIntVector("ints", allocator);
        this.intVector.allocateNew(10);
        this.intVector.setValueCount(10);
        this.fillVector(intVector);
        return this.intVector;
    }

    private void fillVector(BigIntVector iv) {
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

    public boolean compareArrays(long c_array_ptr, long c_schema_ptr) {
        ArrowArray arrow_array = ArrowArray.wrap(c_array_ptr);
        ArrowSchema arrow_schema = ArrowSchema.wrap(c_schema_ptr);

        FieldVector imported = Data.importVector(allocator, arrow_array, arrow_schema, null);
        FieldVector vector = this.intVector;
        System.out.println(imported + " <> " + vector);
        return VectorEqualsVisitor.vectorEquals(imported, vector);
    }
}
