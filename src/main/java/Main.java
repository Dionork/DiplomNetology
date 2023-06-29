import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.ServerSocket;

public class Main {
    static final int SERVER = 8989;

    public static void main(String[] args) throws Exception {
        SearchEngine engine = new BooleanSearchEngine(new File("pdfs"));
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
// Старт СЕРВЕРА
        try (ServerSocket serverSocket = new ServerSocket(SERVER)) {
            //Принимаем подключения
            while (true) {
                try (
                        var socket = serverSocket.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                    var request = in.readLine();
                    var response = gson.toJson(engine.search(request));
                    out.println(response);
                }
            }
        } catch (IOException e) {
            System.out.println("Сервер не запускается!");
            e.printStackTrace();
        }
    }
}