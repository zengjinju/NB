package com.zjj.nb.biz;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by admin on 2017/9/16.
 */
public class SocketTest {

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket=new ServerSocket(8087,1, InetAddress.getByName("127.0.0.1"));
        Socket socket=serverSocket.accept();
        OutputStream out=socket.getOutputStream();
        BufferedReader reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
        StringBuilder builder=new StringBuilder();
        String line=reader.readLine();
        while(line!=null&&!"".equals(line)){
            builder.append(line);
            line=reader.readLine();
        }
        System.out.println(builder.toString());
        out.write(builder.toString().getBytes());
        PrintWriter writer=new PrintWriter(out);
        writer.write(builder.toString());
    }
}
