# Простой чат на Сокетах

Чат состоит из двух классов – Server и Client. Server запускается в одном экземпляре, и принимает запросы на подключение от клиентов (Client). Для обработки запросов каждого клиента создается новый поток с сокетом клиента, и клиент заносится в список. У клиентов создается 2 потока (внутренние классы) для отправки и чтения сообщений с сервера. При получении сообщения от клиента сервер отправляет его всем остальным клиентам из списка.
