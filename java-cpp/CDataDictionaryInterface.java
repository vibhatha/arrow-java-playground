import org.apache.arrow.c.ArrowArray;
import org.apache.arrow.c.ArrowSchema;
import org.apache.arrow.c.CDataDictionaryProvider;
import org.apache.arrow.c.Data;
import org.apache.arrow.memory.BufferAllocator;
import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.vector.TinyIntVector;
import org.apache.arrow.vector.FieldVector;


public class CDataDictionaryInterface {

    public static void main(String[] args) {
        try(
            BufferAllocator allocator = new RootAllocator();
            ArrowSchema arrowSchema = ArrowSchema.allocateNew(allocator);
            ArrowArray arrowArray = ArrowArray.allocateNew(allocator)
        ){
            CDataDictionaryJavaToCppExample.UpdateDictionary(
                    arrowSchema.memoryAddress(), arrowArray.memoryAddress());
            
            try(
                CDataDictionaryProvider provider = new CDataDictionaryProvider();
                FieldVector vector = (FieldVector) Data.importVector(allocator, arrowArray, arrowSchema, provider)
            ) {
                TinyIntVector tinyIntVector = (TinyIntVector)vector;
                tinyIntVector.setSafe(3, 1);
                System.out.println("C++-allocated DictionaryArray: " + vector);
            }
        }
    }
}
