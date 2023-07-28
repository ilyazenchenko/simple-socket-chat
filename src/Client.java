import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static Socket socket;
    private static Scanner scanner;
    private static BufferedReader in;
    private static BufferedWriter out;

    private static String name;

    public static void main(String[] args) {
        try {
            socket = new Socket("localhost", 4004);
            scanner = new Scanner(System.in);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            System.out.println("Введите имя: ");
            name = scanner.nextLine();

            ClientReader clientReader = new ClientReader();
            ClientWriter clientWriter = new ClientWriter();
            clientReader.start();
            clientWriter.start();

            clientWriter.join();
            clientReader.interrupt();

        } catch (IOException e) {
            System.out.println("Ошибка :(");
            System.out.println(e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            System.out.println("Клиент закрыт");
            try {
                socket.close();
                in.close();
                out.close();
                scanner.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static class ClientReader extends Thread {
        @Override
        public void run() {
            try {
                String serverAnswer;
                do {
                    serverAnswer = in.readLine();
                    System.out.println(serverAnswer);
                } while (!interrupted());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    static class ClientWriter extends Thread {
        @Override
        public void run() {
            try {
                String line = "";
                while (!line.equals("exit")) {
                    line = scanner.nextLine();
                    out.write(name + ": " + line + "\n");
                    out.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
