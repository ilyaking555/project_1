package ru.geekbrains.network;

public interface TCPConnactionLisen {
    // событие может быть готово и передаем экземляр соединения, то есть мы запустили наше соединение
    void onConnectionReady(TCPConection tcpConection);

    // мы можем принять строчку и что за строчка
    void onReciveSting(TCPConection tcpConection, String value);

    // может случится дисконет, соединение порвалось
    void onDisconnect(TCPConection tcpConection);

    // иключение и передаем обьект исключения
    void onIoexaption(TCPConection tcpConection, Exception e);

}
