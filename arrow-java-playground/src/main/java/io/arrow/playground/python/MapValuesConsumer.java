package io.arrow.playground.python;


import org.apache.arrow.c.ArrowArray;
import org.apache.arrow.c.ArrowSchema;
import org.apache.arrow.c.Data;
import org.apache.arrow.c.CDataDictionaryProvider;
import org.apache.arrow.memory.BufferAllocator;
import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.vector.FieldVector;
import org.apache.arrow.vector.BigIntVector;


public class MapValuesConsumer {
    private final static BufferAllocator allocator = new RootAllocator();
    private final CDataDictionaryProvider provider;
    private FieldVector vector;

    public MapValuesConsumer(CDataDictionaryProvider provider) {
        this.provider = provider;
    }

    public static BufferAllocator getAllocatorForJavaConsumer() {
        return allocator;
    }

    public FieldVector getVector() {
        return this.vector;
    }

    public void update(long c_array_ptr, long c_schema_ptr) {
        ArrowArray arrow_array = ArrowArray.wrap(c_array_ptr);
        ArrowSchema arrow_schema = ArrowSchema.wrap(c_schema_ptr);
        this.vector = Data.importVector(allocator, arrow_array, arrow_schema, this.provider);
        this.doWorkInJava(vector);
    }

    private void doWorkInJava(FieldVector vector) {
        System.out.println("Doing work in Java");
        BigIntVector bigIntVector = (BigIntVector)vector;
        bigIntVector.setSafe(0, 2);
    }
}
