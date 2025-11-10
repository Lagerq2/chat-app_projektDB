package se.jensen.elias.chatapp.server;

public class ChatServerMain {
    public static void main(String[] args) {

        new ChatServer(5555).start();
    }
}
