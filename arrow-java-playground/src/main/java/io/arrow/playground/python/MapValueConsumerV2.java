package io.arrow.playground.python;

import org.apache.arrow.c.ArrowArray;
import org.apache.arrow.c.ArrowSchema;
import org.apache.arrow.c.CDataDictionaryProvider;
import org.apache.arrow.c.Data;
import org.apache.arrow.memory.BufferAllocator;
import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.vector.BigIntVector;
import org.apache.arrow.vector.FieldVector;


public class MapValueConsumerV2 {
    private final static BufferAllocator allocator = new RootAllocator();
    private final CDataDictionaryProvider provider;
    private FieldVector vector;
    final static BigIntVector intVector =
            new BigIntVector("internal_test", allocator);


    public MapValueConsumerV2(CDataDictionaryProvider provider) {
        this.provider = provider;
    }

    public MapValueConsumerV2() {
        this.provider = null;
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

    public FieldVector updateFromJava(long c_array_ptr, long c_schema_ptr) {
        ArrowArray arrow_array = ArrowArray.wrap(c_array_ptr);
        ArrowSchema arrow_schema = ArrowSchema.wrap(c_schema_ptr);
        vector = Data.importVector(allocator, arrow_array, arrow_schema, null);
        this.doWorkInJava(vector);
        return vector;
    }

    private void doWorkInJava(FieldVector vector) {
        System.out.println("Doing work in Java");
        BigIntVector bigIntVector = (BigIntVector)vector;
        bigIntVector.setSafe(0, 2);
    }

    public static void main(String[] args) {
        simulateAsAJavaConsumers();
        close();
    }

    public static BigIntVector getIntVectorForJavaConsumers() {
        intVector.allocateNew(3);
        intVector.set(0, 1);
        intVector.set(1, 7);
        intVector.set(2, 93);
        intVector.setValueCount(3);
        return intVector;
    }

    public static void simulateAsAJavaConsumers() {
        CDataDictionaryProvider provider = new CDataDictionaryProvider();
        MapValueConsumerV2 mvc = new MapValueConsumerV2(provider);//FIXME! Use constructor with dictionary provider
        try (
            ArrowArray arrowArray = ArrowArray.allocateNew(allocator);
            ArrowSchema arrowSchema = ArrowSchema.allocateNew(allocator)
        ) {
            Data.exportVector(allocator, getIntVectorForJavaConsumers(), provider, arrowArray, arrowSchema);
            FieldVector updatedVector = mvc.updateFromJava(arrowArray.memoryAddress(), arrowSchema.memoryAddress());
            try (ArrowArray usedArray = ArrowArray.allocateNew(allocator);
                ArrowSchema usedSchema = ArrowSchema.allocateNew(allocator)) {
                Data.exportVector(allocator, updatedVector, provider, usedArray, usedSchema);
                try(FieldVector valueVectors = Data.importVector(allocator, usedArray, usedSchema, provider)) {
                    System.out.println(valueVectors);
                }
            }
        }
    }

    public static void close() {
        intVector.close();
        //allocator.close();
    }
}

