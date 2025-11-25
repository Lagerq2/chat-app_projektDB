package se.jensen.elias.chatapp.server;

import se.jensen.elias.chatapp.dao.MessageDAO;
import se.jensen.elias.chatapp.dao.UserDAO;
import se.jensen.elias.chatapp.dao.impl.MessageDatabaseDAO;
import se.jensen.elias.chatapp.dao.impl.UserDatabaseDAO;
import se.jensen.elias.chatapp.model.Message;
import se.jensen.elias.chatapp.model.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final ChatServer server;
    private PrintWriter out;
    private User user;

    private final UserDAO userDAO = new UserDatabaseDAO();
    private final MessageDAO messageDAO = new MessageDatabaseDAO();

    ClientHandler(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }

    public User getUser() {
        return user;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)

        ) {
            this.out = writer;

            writer.println("Välkommen! Har du redan ett konto? (ja/nej)");
            String answer = in.readLine();

            //if-sats om användaren har eller inte har ett konto
            if ("ja".equalsIgnoreCase(answer)) {
                writer.println("Ange användarnamn:");
                String username = in.readLine();
                writer.println("Ange lösenord:");
                String password = in.readLine();
                //loggar in användaren om hen skrivit rätt uppgifter
                user = userDAO.login(username, password);
                //om användaren inte finns skrivs fel ut
                if (user == null) {
                    writer.println("Fel användarnamn eller lösenord.");
                    writer.println("Du måste skriva /quit nu för att avsluta denna klient");
                    writer.println("Pröva att återansluta med en ny klient");

                }
                //svarar man nej får man skapa ett konto
            } else {
                writer.println("Skapa nytt konto. Ange användarnamn:");
                String username = in.readLine();
                writer.println("Ange lösenord:");
                String password = in.readLine();
                user = userDAO.register(new User(username, password));  //ett nytt userobjekt skapas med den nya användare, sparas samtidigt i databasen med register-metoden
                writer.println("Konto skapat. Välkommen, " + user.getUsername() + "!");
            }

            writer.println("Du är inloggad som: " + user.getUsername() + " ");
            writer.println("Nu kan du börja skriva meddelanden");
            writer.println("Skriv /quit för att avsluta");
            writer.println("Skriv /mymsgs för att lista alla dina meddelanden");

            System.out.println(user.getUsername() + " anslöt.");

            //applikationen väntar på att användaren ska skriva ett meddelande, /quit eller /mymsgs
            String message;
            while ((message = in.readLine()) != null) {
                if (message.equalsIgnoreCase("/quit")) {
                    break;
                } else if (message.equalsIgnoreCase("/mymsgs")) {
                    List<Message> messages = messageDAO.getMessagesByUserId(user.getId()); //hämtar meddelanden med användarens id och lägger i en lista
                    if (messages.isEmpty()) {
                        out.println("Inga sparade meddelanden.");
                    } else {
                        out.println("Dina meddelanden:");
                        for (Message m : messages) {  // loopar igenom listan och skriver ut meddelanden med datum
                            out.println("[" + m.getTimestamp() + "] " + m.getText());
                        }
                    }
                } else {

                    server.broadcast(message, this);
                    messageDAO.saveMessage(new Message(user.getId(), message, java.time.LocalDateTime.now()));
                }
            }

        } catch (IOException e) {
            System.out.println("Problem med klient: " + e.getMessage());
        } finally {
            server.removeClient(this);
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }

    public void sendMessage(String msg) {
        if (out != null) out.println(msg);
    }
}

