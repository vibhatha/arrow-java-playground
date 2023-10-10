package io.arrow.playground.compressor;

import org.apache.arrow.memory.BufferAllocator;
import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.vector.*;
import org.apache.arrow.vector.ipc.*;
import org.apache.arrow.vector.ipc.message.*;
import java.nio.channels.*;
import java.io.*;

import org.apache.arrow.vector.compression.CompressionUtil;
import org.apache.arrow.vector.compression.NoCompressionCodec;

import org.apache.arrow.compression.CommonsCompressionFactory;


public class ArrowReaderExample {
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        // Specify the path to the Arrow file
        String filePath = "/home/asus/Documents/Work/Arrow/gh-37841/output.arrow";
        File file = new File(filePath);
        final BufferAllocator allocator = new RootAllocator(Integer.MAX_VALUE);
        // Create a SeekableByteChannel for the file
        try (SeekableByteChannel channel = FileChannel.open(file.toPath())) {
            // Create an ArrowFileReader
            try (ArrowStreamReader reader = new ArrowStreamReader(channel, allocator, CommonsCompressionFactory.INSTANCE)) {
                // Get the schema
                org.apache.arrow.vector.types.pojo.Schema schema = reader.getVectorSchemaRoot().getSchema();
                System.out.println("/t>>>>Schema");
                System.out.println(schema);

                // Load the next batch of records
                while (reader.loadNextBatch()) {
                    VectorSchemaRoot root = reader.getVectorSchemaRoot();
                    // Process the batch (this example just prints the value count)
                    System.out.println("Value count: " + root.getRowCount());
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
