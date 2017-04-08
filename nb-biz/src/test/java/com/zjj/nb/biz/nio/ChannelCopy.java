package com.zjj.nb.biz.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * Created by jinju.zeng on 2017/4/5.
 */
public class ChannelCopy {

    public static void main(String[] args) throws IOException {
        ReadableByteChannel source= Channels.newChannel(System.in);
        WritableByteChannel dest=Channels.newChannel(System.out);
        channelCopy(source,dest);
        source.close();
        dest.close();
    }

    public static void channelCopy(ReadableByteChannel source,WritableByteChannel dest) {
        ByteBuffer buffer=ByteBuffer.allocateDirect(1024);
        try {
            while(source.read(buffer)!=-1){
                //准备是否缓冲区的内容
                buffer.flip();
                //确保buffer中的内容已经完全的释放掉
                while(buffer.hasRemaining()){
                    dest.write(buffer);
                }
                buffer.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
