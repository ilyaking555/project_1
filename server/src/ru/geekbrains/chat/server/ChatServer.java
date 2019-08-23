package ru.geekbrains.chat.server;

import ru.geekbrains.network.TCPConection;
import ru.geekbrains.network.TCPConnactionLisen;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ChatServer implements TCPConnactionLisen {

    public static void main(String[] args) {
        new ChatServer();
    }

    // создадим список соединений, сколько сидит на порте
    private final ArrayList<TCPConection> connactions = new ArrayList<>();


    //конструктор
    private ChatServer() {
        System.out.println("Server running...");
        //класс слушает порт и принимает входящее соединение
        //сервер слушает порт 8189
        try (ServerSocket serverSocket = new ServerSocket(8189)) {
            while (true) {
                //ошибки при подключении клиентов
                try {
                    // на каждое новое соединеие,мы создаем TCPConnection,передаем себя и обьект сокета
                    new TCPConection(this, serverSocket.accept());// асепт ждет новое соединение

                } catch (IOException e) {
                    System.out.println("TCPConnection exception: " + e);
                }
            }


        } catch (IOException e) {
            throw new RuntimeException();
        }

    }


    @Override
    public synchronized void onConnectionReady(TCPConection tcpConection) {
// добавляетя наше соединение в список соединений
        connactions.add(tcpConection);
        //оповещаем кто подключился
        sendAllConnaction(" Client connected " + tcpConection);
    }

    @Override
    public synchronized void onReciveSting(TCPConection tcpConection, String value) {
        sendAllConnaction(value);
    }

    @Override
    public synchronized void onDisconnect(TCPConection tcpConection) {
        connactions.remove(tcpConection);
        sendAllConnaction("Client disconnected" + tcpConection);
    }

    @Override
    public synchronized void onIoexaption(TCPConection tcpConection, Exception e) {
        System.out.println("TCPConection exaption:" + e);
    }

    //если приняли строчку, нужно разослать всем клиетнам информацию
    private void sendAllConnaction(String value) {
        System.out.println(value);
        final int cnt = connactions.size();
        for (int i = 0; i < cnt; i++) {
            //пробегаем по списку всех соединений
            connactions.get(i).sendString(value);
        }
    }

}
