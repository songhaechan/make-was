import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class WebServer {
    private static final Logger logger = LoggerFactory.getLogger(WebServer.class);
    private static final int DEFAULT_PORT = 8080;
    private static final int BACK_LOG = 5;
    private static BufferedReader bufferedReader = null;
    private static  ServerSocket serverSocket = null;
    private static final Map<String,String> httpHeader = new HashMap<>();
    public static void main(String[] args) throws IOException {
        try{
            // 소켓 생성
            serverSocket = new ServerSocket();
            // IP, Port, Back_Log 설정
            serverSocket.bind(
                    new InetSocketAddress(serverSocket.getInetAddress(),DEFAULT_PORT)
                    ,BACK_LOG
            );
            logger.info("WebServer Started {} port",DEFAULT_PORT);

            // 연결대기
            Socket client = serverSocket.accept();
            logger.info("{} : success",client.getInetAddress());
            InputStream inputStream = client.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            // httpHeader 파싱
            parseHttpRequestHeader(br);

            // httpBody 파싱
            String body = parseHttpBody(br);
            logger.info(body);

            client.close();
        }catch (IOException e){
            logger.error(e.getMessage());
        }finally {
            try{
                serverSocket.close();
            }catch (Exception e){}
        }
    }
    private static void parseHttpRequestHeader(BufferedReader br) throws IOException {
        String line = null;
        line = br.readLine();
        String[] firstLine = line.split(" ");
        httpHeader.put(firstLine[0],firstLine[1]);

        while((line = br.readLine()) != null){
            if("".equals(line))
                break;
            String[] header = line.split(" ");
            httpHeader.put(header[0].replace(":",""),header[1].trim());
        }
    }
    private static String parseHttpBody(BufferedReader br) throws IOException {
        int contentLength = Integer.parseInt(httpHeader.get("Content-Length"));
        char[] body = new char[contentLength];
        br.read(body);
        return new String(body);
    }
}
