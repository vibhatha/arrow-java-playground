package io.arrow.playground;

import org.apache.commons.cli.*;

import org.apache.arrow.memory.ArrowBuf;
import org.apache.arrow.memory.BufferAllocator;
import org.apache.arrow.memory.RootAllocator;

public class MemoryApp {
    public static void main(String[] args) throws Exception {
        String mode = argParse(args);
        try(BufferAllocator bufferAllocator = new RootAllocator(8 * 1024);) {
            if (mode.equalsIgnoreCase("no-leak")) {
                System.out.println(">>>> No Memory Leak");
                correctMemoryUsage(bufferAllocator);
            } else if (mode.equalsIgnoreCase("leak")) {
                System.out.println(">>>> Memory Leak");
                incorrectMemoryUsage(bufferAllocator);
            } else {
                throw new Exception("Unsupported argument : " + mode);
            }
        }
    }

    public static String argParse(String[] args) {
        Options options = new Options();

        Option inputOption = new Option("m", "mode", true, "Operation mode: no-leak, leak");
        inputOption.setRequired(true);
        options.addOption(inputOption);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);
            System.exit(1);
        }

        return cmd.getOptionValue("mode");
    }

    public static void correctMemoryUsage(BufferAllocator bufferAllocator) {
        ArrowBuf arrowBuf = bufferAllocator.buffer(4 * 1024);
        System.out.println(arrowBuf);
        arrowBuf.close();
    }

    public static void incorrectMemoryUsage(BufferAllocator bufferAllocator) {
        ArrowBuf arrowBuf = bufferAllocator.buffer(4 * 1024);
        System.out.println(arrowBuf);
    }
}
