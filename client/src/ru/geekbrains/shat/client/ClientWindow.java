package ru.geekbrains.shat.client;

import ru.geekbrains.network.TCPConection;
import ru.geekbrains.network.TCPConnactionLisen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ClientWindow extends JFrame implements ActionListener, TCPConnactionLisen {
    private static final String IP_ADDR = "192.168.0.89";// это адресс моего компьютера
    private static final int PORT = 8189;
    private static final int WIDTH = 600;// размеры окна
    private static final int HEIGHT = 400;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                //выполняем эту строчку в потоке EDT
                new ClientWindow();
            }
        });

    }

    //создаем поле где будут старые написанные сообщения
    private final JTextArea log = new JTextArea();
    //создадим ник свой
    private final JTextField fieldNickname = new JTextField("Alex");
    // создаем окошко куда будем писать сообщение
    private final JTextField fielInput = new JTextField();

    private TCPConection conection; //типо когда контакт или нет

    //создаем конструктор графический интерфейс
    private ClientWindow() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);//закрытие окна крестиком
        setSize(WIDTH, HEIGHT);// размер окна
        setLocationRelativeTo(null);// делаем оконо посередине
        setAlwaysOnTop(true);// делаем окно всегда сверху


        log.setEditable(false);//отменить редактирование написанного текста
        log.setLineWrap(true);// автоматический перенос строк
        add(log, BorderLayout.CENTER); //добавим текст в окошко по центру, старые сообщения наши

        fielInput.addActionListener(this);//добавляем себя
        add(fielInput, BorderLayout.SOUTH);//добавим внизу что будем писать
        add(fieldNickname, BorderLayout.NORTH);// имя добавим наверх

        setVisible(true);//увидеть окно
        try {
            conection = new TCPConection(this, IP_ADDR, PORT);//осуществляем контак себя и своего порта
        } catch (IOException e) {
            printMeg("Connection exception" + e);//если не получился контакт
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // по нажатие на кнопку, должны передать сообщение
        String msg = fielInput.getText();//получим строчку, с помощью поля, которое мы ввели
// если строчка пустая, то не передаем ее и выходим из метода
        if (msg.equals("")) return;
        fielInput.setText(null);//если что-то написано, но мы стираем текст, там где его писали
        conection.sendString(fieldNickname.getText() + " : " + msg);//далее вывести имя и текст, который мы написали

    }

    @Override
    public void onConnectionReady(TCPConection tcpConection) {
        printMeg("Connection ready...");
    }

    @Override
    public void onReciveSting(TCPConection tcpConection, String value) {
        printMeg(value);// просто печатаем строчку
    }

    @Override
    public void onDisconnect(TCPConection tcpConection) {
        printMeg("Connection close");
    }

    @Override
    public void onIoexaption(TCPConection tcpConection, Exception e) {
        printMeg("Connection exception" + e);
    }

    // метод будет работать из разных потоков с потоком соединения и окошка
    private synchronized void printMeg(String msg) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                //и тут рботаем с элементами управления
                log.append(msg + "\n");// добавляем строчку и переход на новую строку
                log.setCaretPosition(log.getDocument().getLength());// устанавливает каретку в самый конец документа
            }
        });
    }


}
