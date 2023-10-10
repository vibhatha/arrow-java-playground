package io.arrow.playground.cpp;

import java.nio.charset.StandardCharsets;

import org.apache.arrow.c.ArrowArray;
import org.apache.arrow.c.ArrowSchema;
import org.apache.arrow.c.CDataDictionaryProvider;
import org.apache.arrow.c.Data;
import org.apache.arrow.memory.BufferAllocator;
import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.vector.BigIntVector;
import org.apache.arrow.vector.VarCharVector;
import org.apache.arrow.vector.dictionary.Dictionary;
import org.apache.arrow.vector.types.pojo.ArrowType;
import org.apache.arrow.vector.types.pojo.DictionaryEncoding;


public class MapValues {
    static {
        System.loadLibrary("ArrowCpp");
    }

    private static RootAllocator allocator = new RootAllocator();

    public native void helloDictionary();

    public native void updateDictionary(long c_array_ptr, long c_schema_ptr);

    public static void updateDictionaryFromCpp() {

    }

    public static void main(String[] args) {
        MapValues mapValues = new MapValues();
        mapValues.helloDictionary();
        CDataDictionaryProvider provider = new CDataDictionaryProvider();
        VarCharVector countries = new VarCharVector("country-dict", allocator);
        DictionaryEncoding encoding = new DictionaryEncoding(/*id=*/1L, /*ordered=*/false, /*indexType=*/new ArrowType.Int(8, true));

        countries.allocateNew(5);
        countries.set(0, "A".getBytes(StandardCharsets.UTF_8));
        countries.set(1, "B".getBytes(StandardCharsets.UTF_8));
        countries.set(2, "C".getBytes(StandardCharsets.UTF_8));
        countries.set(3, "D".getBytes(StandardCharsets.UTF_8));
        countries.set(4, "E".getBytes(StandardCharsets.UTF_8));
        countries.setValueCount(5);
        
        Dictionary countriesDictionary = new Dictionary(countries, encoding);

        try(
            ArrowSchema arrowSchema = ArrowSchema.allocateNew(allocator);
            ArrowArray arrowArray = ArrowArray.allocateNew(allocator)
        ) {
            Data.exportVector(allocator, countriesDictionary.getVector(), provider, arrowArray, arrowSchema);
            mapValues.updateDictionary(arrowArray.memoryAddress(), arrowSchema.memoryAddress());
        }
        
    }
}


