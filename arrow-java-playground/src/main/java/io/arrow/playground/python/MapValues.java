package io.arrow.playground.python;

import org.apache.arrow.c.ArrowArray;
import org.apache.arrow.c.ArrowSchema;
import org.apache.arrow.c.Data;
import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.vector.BigIntVector;
import org.apache.arrow.vector.FieldVector;
import org.apache.arrow.vector.ValueVector;
import org.apache.arrow.vector.VarCharVector;
import org.apache.arrow.vector.dictionary.Dictionary;
import org.apache.arrow.vector.dictionary.DictionaryEncoder;
import org.apache.arrow.vector.types.pojo.ArrowType;
import org.apache.arrow.vector.types.pojo.DictionaryEncoding;
import java.nio.charset.StandardCharsets;


public class MapValues {

    static RootAllocator allocator = new RootAllocator();
    private VarCharVector countries;
    private DictionaryEncoding encoding;
    private Dictionary countriesDictionary;


    public MapValues() {
        this.countries = new VarCharVector("country-dict", allocator);
        this.encoding = new DictionaryEncoding(/*id=*/1L, /*ordered=*/false, /*indexType=*/new ArrowType.Int(8, true));
        this.countriesDictionary = null;
    }

    public void createDictionary() {
        
        this.countries.allocateNew(10);
        this.countries.set(0, "Andorra".getBytes(StandardCharsets.UTF_8));
        this.countries.set(1, "Cuba".getBytes(StandardCharsets.UTF_8));
        this.countries.set(2, "Grecia".getBytes(StandardCharsets.UTF_8));
        this.countries.set(3, "Guinea".getBytes(StandardCharsets.UTF_8));
        this.countries.set(4, "Islandia".getBytes(StandardCharsets.UTF_8));
        this.countries.set(5, "Malta".getBytes(StandardCharsets.UTF_8));
        this.countries.set(6, "Tailandia".getBytes(StandardCharsets.UTF_8));
        this.countries.set(7, "Uganda".getBytes(StandardCharsets.UTF_8));
        this.countries.set(8, "Yemen".getBytes(StandardCharsets.UTF_8));
        this.countries.set(9, "Zambia".getBytes(StandardCharsets.UTF_8));
        this.countries.setValueCount(10);
        
        this.countriesDictionary = new Dictionary(this.countries, this.encoding);
        VarCharVector appUserCountriesUnencoded = new VarCharVector("app-use-country-dict", allocator);
        appUserCountriesUnencoded.allocateNew(5);
        appUserCountriesUnencoded.set(0, "Andorra".getBytes(StandardCharsets.UTF_8));
        appUserCountriesUnencoded.set(1, "Guinea".getBytes(StandardCharsets.UTF_8));
        appUserCountriesUnencoded.set(2, "Islandia".getBytes(StandardCharsets.UTF_8));
        appUserCountriesUnencoded.set(3, "Malta".getBytes(StandardCharsets.UTF_8));
        appUserCountriesUnencoded.set(4, "Uganda".getBytes(StandardCharsets.UTF_8));
        appUserCountriesUnencoded.setValueCount(5);
        ValueVector appUserCountriesDictionaryEncoded = DictionaryEncoder
            .encode(appUserCountriesUnencoded, countriesDictionary); 
                appUserCountriesDictionaryEncoded.setValueCount(5);
    }

    public Dictionary getDictionary() {
        return this.countriesDictionary;
    }

    public boolean compareWithPython(long c_array_ptr, long c_schema_ptr) {
        ArrowArray arrow_array = ArrowArray.wrap(c_array_ptr);
        ArrowSchema arrow_schema = ArrowSchema.wrap(c_schema_ptr);

        FieldVector v = Data.importVector(allocator, arrow_array, arrow_schema, null);
        FieldVector u = this.countriesDictionary.getVector();
        
        return v.getValueCount() == u.getValueCount();
    }
    
}
