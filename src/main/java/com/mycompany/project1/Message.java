package com.mycompany.project1;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JOptionPane;
import java.util.stream.Collectors;

public class Message {

    /**
     * Internal class to hold all message data (content and metadata) together.
     */
    private static class MessageObject {
        // Core data
        public final String recipient;
        public final String messageContent;
        public final String flag; 

        // Metadata
        public final String messageID;
        public final String messageHash;

        public MessageObject(String recipient, String messageContent, String flag, String messageID, String messageHash) {
            this.recipient = recipient;
            this.messageContent = messageContent;
            this.flag = flag;
            this.messageID = messageID;
            this.messageHash = messageHash;
        }
    }

    /**
     * 🔑 CRITICAL FIX: The helper class used by GSON must be 'static' 
     * and its fields must be 'public' for reliable serialization.
     */
    private static class MessageData {
        public String recipient;
        public String message;
        public String flag;
    }

    private String currentRecipient;
    private String currentMessageContent;
    private int totalMessagesSent = 0;

    private final List<MessageObject> allMessages = new ArrayList<>();
    private final List<String> disregardedMessages = new ArrayList<>();

    String currentMessageID = "";

    // Constructor automatically loads existing messages
    public Message() {
        readJsonIntoArray();
    }

    public void setRecipient(String recipient) {
        this.currentRecipient = recipient;
    }

    public void setMessageContent(String messageContent) {
        this.currentMessageContent = messageContent;
    }

    // --- Message Validation Methods (Omitted for brevity, assumed correct) ---
    public boolean checkRecipientCall(String callNumber) {
        if (callNumber == null || callNumber.trim().isEmpty()) {
            return false;
        }
        return callNumber.matches("^[+]\\d{11}$");
    }

    public boolean checkMessageLength(String message) {
        if (message == null || message.trim().isEmpty()) {
            return false;
        }
        return message.length() <= 250;
    }

    // --- Message ID and Hash Generation (Omitted for brevity, assumed correct) ---
    public String createMessageID() {
        Random rand = new Random();
        long tenDigitId = (long) (rand.nextDouble() * 9000000000L) + 1000000000L;
        this.currentMessageID = String.valueOf(tenDigitId);
        return this.currentMessageID;
    }

    public String createMessageHash() {
        if (currentMessageContent == null || currentMessageContent.trim().isEmpty() || currentMessageID.length() < 2) {
            return "ERROR: INVALID_STATE";
        }
        String messageIDPrefix = currentMessageID.substring(0, 2);

        String cleanedContent = currentMessageContent.trim().toUpperCase().replaceAll("[^A-Z0-9\\s]", " ");
        String[] words = cleanedContent.split("\\s+");

        String firstWord = words.length > 0 ? words[0] : "";
        String lastWord = words.length > 1 ? words[words.length - 1] : firstWord;

        return messageIDPrefix + ":" + firstWord + lastWord;
    }

    // --- Core Message Processing ---
    public String send(String option, String messageID, String messageHash) {
        switch (option) {
            case "1": // Sent Message
                totalMessagesSent++;
                allMessages.add(new MessageObject(currentRecipient, currentMessageContent, "Sent", messageID, messageHash));
                saveMessagesToJson(); // ✅ Save automatically
                return "Message successfully sent.\nPress D to delete message.";

            case "2": // Disregarded Message
                disregardedMessages.add(currentMessageContent);
                return "Message discarded.";

            case "3": // Stored Message
                allMessages.add(new MessageObject(currentRecipient, currentMessageContent, "Stored", messageID, messageHash));
                saveMessagesToJson(); // ✅ Save automatically
                return "Message successfully stored.";

            case "4":
                return "ChatGPT feature placeholder invoked.";

            default:
                return "Invalid option selected. Message discarded.";
        }
    }

    public int returnTotalMessageSent() {
        return totalMessagesSent;
    }
    
    // --- Utility Features (Omitted for brevity, assumed correct) ---

    public String displayLongestMessage() {
        List<String> sentContents = allMessages.stream()
                .filter(msg -> "Sent".equals(msg.flag))
                .map(msg -> msg.messageContent)
                .collect(Collectors.toList());

        if (sentContents.isEmpty()) {
            return "No SENT messages available to find the longest message.";
        }

        String longestMessage = "";
        for (String msg : sentContents) {
            if (msg.length() > longestMessage.length()) {
                longestMessage = msg;
            }
        }
        return "Longest Message Found:\n" + longestMessage;
    }

    public String searchForMessageID(String messageID) {
        for (MessageObject msg : allMessages) {
            if (msg.messageID.equals(messageID)) {
                return String.format(" Message Found \nID: %s\nRecipient: %s\nHash: %s\nMessage: %s\nFlag: %s",
                        msg.messageID, msg.recipient, msg.messageHash, msg.messageContent, msg.flag);
            }
        }
        return "Message ID not found.";
    }

    public String searchAllMessagesToRecipient(String recipient) {
        StringBuilder sb = new StringBuilder(" Messages Sent/Stored for Recipient: " + recipient + " -\n");
        boolean found = false;

        for (MessageObject msg : allMessages) {
            if (msg.recipient.equals(recipient)) {
                found = true;
                sb.append(String.format("Flag: %s | ID: %s | Hash: %s | Message: %s\n",
                        msg.flag, msg.messageID, msg.messageHash, msg.messageContent));
            }
        }

        if (!found) {
            return "No messages found for that recipient.";
        }
        return sb.toString();
    }

    public String deleteMessageByHash(String messageHash) {
        MessageObject messageToDelete = null;
        for (MessageObject msg : allMessages) {
            if (msg.messageHash.equals(messageHash)) {
                messageToDelete = msg;
                break;
            }
        }

        if (messageToDelete != null) {
            allMessages.remove(messageToDelete);
            if ("Sent".equals(messageToDelete.flag) && totalMessagesSent > 0) {
                totalMessagesSent--;
            }
            saveMessagesToJson(); // ✅ Update JSON after deletion
            return String.format("Message with Hash %s (%s) successfully deleted.", messageHash, messageToDelete.flag);
        } else {
            return "Message Hash not found. No message deleted.";
        }
    }

    public String displayReport() {
        StringBuilder sb = new StringBuilder("--- Message System Report ---\n");

        sb.append(String.format("Total Messages Tracked (Sent/Stored/JSON): %d\n", allMessages.size()));
        sb.append(String.format("Total Disregarded Messages: %d\n", disregardedMessages.size()));
        sb.append(String.format("Total Messages Sent (via option 1): %d\n", totalMessagesSent));

        sb.append("\n- Tracking Data (Sent/Stored/JSON) -\n");
        if (allMessages.isEmpty()) {
            sb.append("No Sent, Stored, or JSON-loaded messages tracked.\n");
        } else {
            for (MessageObject msg : allMessages) {
                sb.append(String.format("%s | ID: %s | Hash: %s | Recipient: %s\n",
                        msg.flag, msg.messageID, msg.messageHash, msg.recipient));
            }
        }

        sb.append("\n--- Disregarded Message Content ---\n");
        if (disregardedMessages.isEmpty()) {
            sb.append("No messages were disregarded.\n");
        } else {
            for (String msg : disregardedMessages) {
                sb.append("-> ").append(msg, 0, Math.min(msg.length(), 40)).append("...\n");
            }
        }

        return sb.toString();
    }

    // --- JSON Load ---
    public void readJsonIntoArray() {
        String jsonFilePath = "StoredMessages.json";
        File jsonFile = new File(jsonFilePath);

        if (!jsonFile.exists()) {
            System.out.println("ℹ No existing StoredMessages.json found. Starting fresh.");
            return;
        }

        try (FileReader reader = new FileReader(jsonFilePath)) {
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<MessageData>>() {
            }.getType();
            List<MessageData> messagesFromJSON = gson.fromJson(reader, listType);

            if (messagesFromJSON != null && !messagesFromJSON.isEmpty()) {
                for (MessageData data : messagesFromJSON) {
                    if (checkRecipientCall(data.recipient) && checkMessageLength(data.message)) {
                        
                        // Preserve the caller's current context
                        String tempRecipient = this.currentRecipient;
                        String tempMessageContent = this.currentMessageContent;

                        // Set context temporarily to generate unique ID/Hash
                        setRecipient(data.recipient);
                        setMessageContent(data.message);
                        String generatedID = createMessageID();
                        String generatedHash = createMessageHash();
                        
                        // Add the message to the tracked list
                        allMessages.add(new MessageObject(data.recipient, data.message, data.flag, generatedID, generatedHash));

                        // Restore current context
                        this.currentRecipient = tempRecipient;
                        this.currentMessageContent = tempMessageContent;
                    }
                }
                // Update totalMessagesSent based on loaded 'Sent' messages
                totalMessagesSent = (int) allMessages.stream().filter(msg -> "Sent".equals(msg.flag)).count();
                
                System.out.println("✅ Loaded " + allMessages.size() + " messages from JSON. Sent count initialized to " + totalMessagesSent + ".");
            } else {
                System.out.println("ℹ JSON file is empty.");
            }
        } catch (IOException | JsonSyntaxException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Warning: Could not read 'StoredMessages.json'. Starting with empty message list.",
                    "JSON Load Warning",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    // --- JSON Save ---
    public void saveMessagesToJson() {
        String jsonFilePath = "StoredMessages.json";
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        List<MessageData> toSave = new ArrayList<>();
        // 🛠️ Only save messages explicitly marked as 'Sent' or 'Stored'
        for (MessageObject msg : allMessages.stream().filter(msg -> "Sent".equals(msg.flag) || "Stored".equals(msg.flag)).collect(Collectors.toList())) {
            MessageData data = new MessageData();
            data.recipient = msg.recipient;
            data.message = msg.messageContent;
            data.flag = msg.flag;
            toSave.add(data);
        }

        try (FileWriter writer = new FileWriter(jsonFilePath)) {
            gson.toJson(toSave, writer);
            System.out.println("💾 Messages saved successfully to " + jsonFilePath);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error saving messages to JSON file.",
                    "Save Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}