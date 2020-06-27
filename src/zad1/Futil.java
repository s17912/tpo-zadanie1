package zad1;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;


public class Futil {

    public static void processDir(String path, String resultFileName) {
        Path sourcePath = Paths.get(path);
        Path targetPath = Paths.get(resultFileName);


        try {
            if(Files.exists(targetPath)) {
                FileChannel.open(targetPath, StandardOpenOption.WRITE).truncate(0).close();
            }
            Files.walkFileTree(sourcePath, new FileVisitor<Path>() {


                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {

                   if (Files.isReadable(path) && !Files.isHidden(path)) {
                        FileChannel   fileChannelIn= FileChannel.open(path);
                        FileChannel fileChannelOut = FileChannel.open(targetPath, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
                       fileChannelOut.position(fileChannelOut.size());

                        ByteBuffer byteBufferIn = ByteBuffer.allocate(256);
                        int numberOfBytesRead = fileChannelIn.read(byteBufferIn);

                        while (numberOfBytesRead != -1){
                            byteBufferIn.flip();
                            CharBuffer charBuffer = Charset.forName("CP1250").decode(byteBufferIn);
                            ByteBuffer byteBufferOut = StandardCharsets.UTF_8.encode(charBuffer);
                            while (byteBufferOut.hasRemaining()){
                                fileChannelOut.write(byteBufferOut);
                            }
                            byteBufferIn.clear();
                            numberOfBytesRead = fileChannelIn.read(byteBufferIn);
                        }


                       fileChannelIn.close();
                       fileChannelOut.close();
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException ioex) {
            ioex.printStackTrace();
            ioex.getCause();

        }
    }
}
