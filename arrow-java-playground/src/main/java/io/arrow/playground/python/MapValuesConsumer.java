package io.arrow.playground.python;


import org.apache.arrow.c.ArrowArray;
import org.apache.arrow.c.ArrowSchema;
import org.apache.arrow.c.Data;
import org.apache.arrow.c.CDataDictionaryProvider;
import org.apache.arrow.memory.BufferAllocator;
import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.util.AutoCloseables;
import org.apache.arrow.vector.FieldVector;
import org.apache.arrow.vector.BigIntVector;


public class MapValuesConsumer implements AutoCloseable {
    private final BufferAllocator allocator;
    private final CDataDictionaryProvider provider;
    private FieldVector vector;
    private final BigIntVector intVector;


    public MapValuesConsumer(CDataDictionaryProvider provider, BufferAllocator allocator) {
        this.provider = provider;
        this.allocator = allocator;
        this.intVector = new BigIntVector("internal_test_vector", allocator);
    }

    public BufferAllocator getAllocatorForJavaConsumer() {
        return this.allocator;
    }

    public FieldVector getVector() {
        return this.vector;
    }

    public void callToJava(long c_array_ptr, long c_schema_ptr) {
        ArrowArray arrow_array = ArrowArray.wrap(c_array_ptr);
        ArrowSchema arrow_schema = ArrowSchema.wrap(c_schema_ptr);
        this.vector = Data.importVector(allocator, arrow_array, arrow_schema, this.provider);
        this.doWorkInJava(vector);
    }

    public FieldVector callFromJava(long c_array_ptr, long c_schema_ptr) {
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

    private BigIntVector getIntVectorForJavaConsumers() {
        intVector.allocateNew(3);
        intVector.set(0, 1);
        intVector.set(1, 7);
        intVector.set(2, 93);
        intVector.setValueCount(3);
        return intVector;
    }


    @Override
    public void close() throws Exception {
        AutoCloseables.close(intVector);
    }

    public static void main(String[] args) {

        try (BufferAllocator allocator = new RootAllocator()) {
            CDataDictionaryProvider provider = new CDataDictionaryProvider();
            try (MapValuesConsumer mvc = new MapValuesConsumer(provider, allocator)) {
                try (
                ArrowArray arrowArray = ArrowArray.allocateNew(allocator);
                ArrowSchema arrowSchema = ArrowSchema.allocateNew(allocator)
                ) {
                    Data.exportVector(allocator, mvc.getIntVectorForJavaConsumers(), provider, arrowArray, arrowSchema);
                    FieldVector updatedVector = mvc.callFromJava(arrowArray.memoryAddress(), arrowSchema.memoryAddress());
                    try (ArrowArray usedArray = ArrowArray.allocateNew(allocator);
                        ArrowSchema usedSchema = ArrowSchema.allocateNew(allocator)) {
                        Data.exportVector(allocator, updatedVector, provider, usedArray, usedSchema);
                        try(FieldVector valueVectors = Data.importVector(allocator, usedArray, usedSchema, provider)) {
                            System.out.println(valueVectors);
                        }
                    }
                    updatedVector.close(); 
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
