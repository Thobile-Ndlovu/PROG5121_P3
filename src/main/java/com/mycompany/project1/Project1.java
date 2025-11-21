package com.mycompany.project1;

import javax.swing.JOptionPane;

public class Project1 {

    public static void main(String[] args) {

        JOptionPane.showMessageDialog(null, "Welcome to the Registration page!");

        // Registration Section (Uses Loops for Validation)
        String username = null;
        String password = null;
        String cellphoneNumber = null;
        String firstNameInput = null;
        String lastNameInput = null;

        // Loop for Username
        while (true) {
            username = JOptionPane.showInputDialog(null, "Enter your username (must contain '_' and be 1-5 characters long):");
            if (username == null) {
                JOptionPane.showMessageDialog(null, "Registration cancelled. Exiting.", "Exit", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            if (Login.checkUsername(username)) {
                break; // Exit loop if valid
            }
            JOptionPane.showMessageDialog(null, "Invalid username format. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Loop for Password
        while (true) {
            password = JOptionPane.showInputDialog(null, "Enter your password (min 8 characters, 1 uppercase, 1 digit, 1 special char):");
            if (password == null) {
                JOptionPane.showMessageDialog(null, "Registration cancelled. Exiting.", "Exit", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            if (Login.checkPasswordComplexity(password)) {
                break; // Exit loop if valid
            }
            JOptionPane.showMessageDialog(null, "Invalid password format. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Loop for Cellphone Number
        while (true) {
            cellphoneNumber = JOptionPane.showInputDialog(null, "Enter your South African cellphone number (e.g., +27721234567):");
            if (cellphoneNumber == null) {
                JOptionPane.showMessageDialog(null, "Registration cancelled. Exiting.", "Exit", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            if (Login.validateCellphoneNumber(cellphoneNumber)) {
                break; // Exit loop if valid
            }
            JOptionPane.showMessageDialog(null, "Invalid cellphone number format. Please try again (e.g., +27711234567).", "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Loop for First Name
        while (true) {
            firstNameInput = JOptionPane.showInputDialog(null, "Enter your first name:");
            if (firstNameInput == null) {
                JOptionPane.showMessageDialog(null, "Registration cancelled. Exiting.", "Exit", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            if (!firstNameInput.trim().isEmpty()) {
                break; // Exit loop if valid
            }
            JOptionPane.showMessageDialog(null, "First name cannot be empty. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Loop for Last Name
        while (true) {
            lastNameInput = JOptionPane.showInputDialog(null, "Enter your last name:");
            if (lastNameInput == null) {
                JOptionPane.showMessageDialog(null, "Registration cancelled. Exiting.", "Exit", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            if (!lastNameInput.trim().isEmpty()) {
                break; // Exit loop if valid
            }
            JOptionPane.showMessageDialog(null, "Last name cannot be empty. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }


        Login.setRegisteredUserDetails(username, password, firstNameInput, lastNameInput);
        JOptionPane.showMessageDialog(null, "Registration successful!");

        // --- Login Section ---
        JOptionPane.showMessageDialog(null, "Now, please log in with your new credentials.");
        String loginUsername = null;
        String loginPassword = null;
        boolean isLoginSuccessful = false;

        // Loop for Login
        while (!isLoginSuccessful) {
            loginUsername = JOptionPane.showInputDialog(null, "Enter your username to login:");
            if (loginUsername == null) return;

            loginPassword = JOptionPane.showInputDialog(null, "Enter your password to login:");
            if (loginPassword == null) return;

            isLoginSuccessful = Login.loginUser(loginUsername, loginPassword);
            String loginStatusMessage = Login.returnLoginStatus(isLoginSuccessful);
            JOptionPane.showMessageDialog(null, loginStatusMessage);

            if (!isLoginSuccessful) {
                int choice = JOptionPane.showConfirmDialog(null, "Login failed. Do you want to try again?", "Login Failed", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.NO_OPTION || choice == JOptionPane.CLOSED_OPTION) {
                    return;
                }
            }
        }

        // --- Application Menu Section (QuickChat) ---
        {
            JOptionPane.showMessageDialog(null, "Welcome to QuickChat.");

            int option = 0;
            Message messageApp = new Message(); // JSON messages loaded here in constructor

            do {
                String menuOptions = "Select an option:\n"
                        + "1) Send New Messages\n"
                        + "2) Array Operations (Search/Display/Delete)\n"
                        + "3) Quit";
                String optionStr = JOptionPane.showInputDialog(null, menuOptions);
                if (optionStr == null) break;

                try {
                    option = Integer.parseInt(optionStr);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Invalid option. Please enter a number 1-3.", "Error", JOptionPane.ERROR_MESSAGE);
                    continue;
                }

                switch (option) {
                    case 1:
                        // 🟢 FIX: Reintroduce prompt for number of messages
                        String numMessagesStr = JOptionPane.showInputDialog(null, "How many messages do you wish to enter?");
                        int numMessages = 0;
                        if (numMessagesStr != null) {
                            try {
                                numMessages = Integer.parseInt(numMessagesStr);
                            } catch (NumberFormatException e) {
                                JOptionPane.showMessageDialog(null, "Invalid input. Returning to main menu.", "Error", JOptionPane.ERROR_MESSAGE);
                                continue;
                            }
                        }
                        // 🟢 FIX: Pass the count to processMessages
                        processMessages(numMessages, messageApp);
                        // Execution returns here, so the main menu is shown next.
                        break;
                    case 2:
                        showArrayOperationsMenu(messageApp);
                        break;
                    case 3:
                        JOptionPane.showMessageDialog(null, "Exiting QuickChat. Goodbye!");
                        break;
                    default:
                        JOptionPane.showMessageDialog(null, "Invalid option. Please enter 1, 2, or 3.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } while (option != 3);
        }
    }

    // 🟢 FIX: Reverted method signature to accept the `count` parameter
    private static void processMessages(int count, Message messageApp) {

        if (count <= 0) {
            JOptionPane.showMessageDialog(null, "No messages to process. Returning to main menu.");
            return;
        }

        // 🟢 FIX: Use a for loop to process exactly 'count' number of messages
        for (int i = 0; i < count; i++) {
            JOptionPane.showMessageDialog(null, "Processing Message " + (i + 1) + " of " + count);

            // --- 1. Get and Validate Recipient (Loops) ---
            String recipient = null;
            while (true) {
                recipient = JOptionPane.showInputDialog(null, "Enter recipient's cell number for Message " + (i + 1) + ":\n(Format: +27711869300)");
                if (recipient == null) break; // Inner loop break
                
                if (messageApp.checkRecipientCall(recipient)) {
                    messageApp.setRecipient(recipient);
                    break;
                }
                JOptionPane.showMessageDialog(null, "Cell phone number is incorrectly formatted. Please try again.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
            if (recipient == null) continue; // Skip to next message if canceled on recipient

            // --- 2. Get and Validate Message Content (Loops) ---
            String content = null;
            while (true) {
                content = JOptionPane.showInputDialog(null, "Enter message content (max 250 characters):");
                if (content == null) break; // Inner loop break
                
                if (messageApp.checkMessageLength(content)) {
                    messageApp.setMessageContent(content);
                    break;
                }
                JOptionPane.showMessageDialog(null, "Message is too long or empty. Max 250 characters. Please try again.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
            if (content == null) continue; // Skip to next message if canceled on content
            
            // --- 3. Generate Auto-Generated Data ---
            String messageID = messageApp.createMessageID();
            String messageHash = messageApp.createMessageHash();

            // --- 4. Send Message Options ---
            String sendOption = JOptionPane.showInputDialog(null, "Message ID: " + messageID + "\nMessage Hash: " + messageHash + "\n\nSelect an option for Message " + (i + 1) + ":\n"
                    + "1) Send Message\n"
                    + "2) Disregard Message\n"
                    + "3) Store Message to send later\n"
                    + "4) ChatGPT (Placeholder)");

            if (sendOption == null) sendOption = "2";

            // Handle the selected option
            String result = messageApp.send(sendOption, messageID, messageHash);

            JOptionPane.showMessageDialog(null, result);

            // Display the full details if sent or stored
            if (result.startsWith("Message successfully sent.") || result.contains("stored")) {
                String displayDetails = "--- Message " + (i + 1) + " Details ---\n"
                        + "MessageID: " + messageID + "\n"
                        + "Message Hash: " + messageHash + "\n"
                        + "Recipient: " + recipient + "\n"
                        + "Message: " + content;
                JOptionPane.showMessageDialog(null, displayDetails);
            }
        }
        // The loop finishes after 'count' messages, and the method returns to the main menu.
    }

    private static void showArrayOperationsMenu(Message messageApp) {
        String menu = "Array Operations:\n"
                + "a) Display longest Sent Message\n"
                + "b) Search for Message ID\n"
                + "c) Search all messages sent to a Recipient\n"
                + "d) Delete a message using its Hash\n"
                + "e) Display Report (All Sent/Stored/Disregarded Messages)\n"
                + "f) Back to Main Menu";

        String choice = JOptionPane.showInputDialog(null, menu);
        if (choice == null) return;
        choice = choice.toLowerCase();

        switch (choice) {
            case "a":
                JOptionPane.showMessageDialog(null, messageApp.displayLongestMessage());
                break;
            case "b":
                String id = JOptionPane.showInputDialog(null, "Enter Message ID to search:");
                if (id != null) JOptionPane.showMessageDialog(null, messageApp.searchForMessageID(id));
                break;
            case "c":
                String recipient = JOptionPane.showInputDialog(null, "Enter Recipient to search (+27...):");
                if (recipient != null) JOptionPane.showMessageDialog(null, messageApp.searchAllMessagesToRecipient(recipient));
                break;
            case "d":
                String hash = JOptionPane.showInputDialog(null, "Enter Message Hash to delete (e.g., 87:WHERETIME):");
                if (hash != null) JOptionPane.showMessageDialog(null, messageApp.deleteMessageByHash(hash));
                break;
            case "e":
                JOptionPane.showMessageDialog(null, messageApp.displayReport());
                break;
            case "f":
            default:
                break;
        }
    }
}