import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private ServerSocket serverSocket;

    private static List<ClientHandler> handlers = new ArrayList<>();

    public static void main(String[] args) {
        Server server = new Server();

        try {
            server.serverSocket = new ServerSocket(4004);
            System.out.println("Сервер запущен!");
            for (int i = 0; i < 10; i++) {
                ClientHandler handler = new ClientHandler(server.serverSocket.accept());  //ждем пока кто-то не подключится
                handlers.add(handler);
                handler.start();
            }
        } catch (IOException ignored) {
        } finally {
            try {
                server.serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Сервер выключен!");
        }

    }

    public static class ClientHandler extends Thread {

        private final Socket clientSocket;
        private final BufferedReader in;
        private final BufferedWriter out;

        public ClientHandler(Socket socket) {
            clientSocket = socket;
            try {
                // после получения сокета и установки связи создаем потоки ввода и вывода
                out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void run() {
            try {
                try {

                    String text;
                    do {
                        text = in.readLine(); //читаем что-то из потока
                        System.out.println("Получено: " + text);

                        for (ClientHandler handler : handlers) {
                            if (handler != this)
                                handler.send(text);
                        }

                    } while (!text.equals("exit"));
                } finally {
                    clientSocket.close();
                    in.close();
                    out.close();
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        public void send(String text){
            try {
                out.write(text + "\n");
                out.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
