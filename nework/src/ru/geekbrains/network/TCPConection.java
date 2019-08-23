package ru.geekbrains.network;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class TCPConection {
    private final Socket socket;// с помощью класса сокет можно содинение устанавливать
    private final Thread rxThread;//поток слушает входящее сообщение и если строчка прилетела то поток будет генерировать событие
    private final TCPConnactionLisen evenListener;
    private final BufferedReader in;
    private final BufferedWriter out;

    // первый конструктор сокет создается внутри
    public TCPConection(TCPConnactionLisen evenListener, String ipaddr, int port) throws IOException {
//вызываем второй констркутор из первого
        this(evenListener, new Socket(ipaddr, port));
    }


    // второй конструктор при подключении кто-то снаружи сделает соединение
    public TCPConection(TCPConnactionLisen evenListener, Socket socket) throws IOException {

        this.evenListener = evenListener;
        this.socket = socket;
        //теперь необходимо получить входящий поток, чтобы принимать какие-то байты
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
        //создаем новый поток который слушает все входящее
        rxThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //устанавливаем соединение
                    evenListener.onConnectionReady(TCPConection.this);
                    // далее должны получать строчки пока поток не прерван
                    while (!rxThread.isInterrupted()) {
                        String msg = in.readLine();// читаем строку
                        //  передаем обьект соединения и строку отдаем
                        evenListener.onReciveSting(TCPConection.this, msg);
                    }

                } catch (IOException e) {
                    evenListener.onIoexaption(TCPConection.this, e);

                } finally {
                    evenListener.onDisconnect(TCPConection.this);

                }
            }
        });
        rxThread.start();
    }


    //метод, отправить соединеия, безопасно обращаться из разных потоков
    public synchronized void sendString(String value) {
        try {
            out.write(value + "\r\n");//возврат каретки
            out.flush();//сбросили буфер
        } catch (IOException e) {
            evenListener.onIoexaption(TCPConection.this, e);
            disconnect();
        }
    }

    // метод оборвать соединение
    public synchronized void disconnect() {
        // прерываем поток и закрываем сокет
        rxThread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            evenListener.onIoexaption(TCPConection.this, e);
        }
    }

    @Override
    public String toString() {
        //адресс соединения и номер порта
        return "TCPConnection" + socket.getInetAddress() + " : " + socket.getPort();
    }
}
