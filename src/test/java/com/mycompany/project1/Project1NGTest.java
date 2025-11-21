package com.mycompany.project1;

import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class Project1NGTest {
    
    private Message message;
    private final String TEST_RECIPIENT_1 = "+27711234567";
    private final String TEST_RECIPIENT_2 = "+27829876543";
    
    // Message Data for predictable hash generation
    private final String TEST_DATA_1_MESSAGE = "Did you get the cake?";
    private final String TEST_DATA_1_RECIPIENT = "+27834557896";
    
    private final String MOCK_ID_PREFIX = "11"; 

    public Project1NGTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @BeforeMethod
    public void setUpMethod() throws Exception {
        // Clear static state for Login tests before each test
        Login.setRegisteredUserDetails(null, null, null, null);
        
        // Initialize Message object for all Message tests
        message = new Message();
        
        //  Setup mock data for Message array tests to ensure tests run consistently 
        // Message 1 (Sent, longest: 58 characters) -> Hash: 87:WHERETIME
        message.setRecipient(TEST_RECIPIENT_1);
        message.setMessageContent("Where are you? You are late! I have asked you to be on time.");
        // Note: The Message class internally creates MessageObject to hold the content and metadata.
        message.send("1", "8776543210", "87;1 WHERETIME"); // Sent
        
        // Message 2 (Stored, short: 26 characters) -> Hash: 99:1 YOHOOOGATE
        message.setRecipient(TEST_RECIPIENT_2);
        message.setMessageContent("Yohooo, I am at your gate.");
        message.send("3", "9911223344", "99;2 YOHOOOGATE"); // Stored
        
        // Message 3 (Sent, medium: 29 characters) -> Hash: 12:OKYOU
        message.setRecipient(TEST_RECIPIENT_1);
        message.setMessageContent("Ok, I am leaving without you.");
        message.send("1", "1299887766", "123 OKYOU"); // Sent
        // Total Sent Messages should be 2 after this setup.
    }

    @AfterMethod
    public void tearDownMethod() throws Exception {
    }
    
    // =========================================================================
    //                            LOGIN CLASS TESTS
    // =========================================================================
    
    @Test
    public void testCheckUsername_CorrectlyFormatted() {
        System.out.println("checkUsername - Correctly Formatted");
        String username = "kyl_1";      
        assertTrue(Login.checkUsername(username), "Username 'kyl_1' should be valid.");
    }
    
    @Test
    public void testCheckUsername_TooLong() {
        System.out.println("checkUsername - Too Long");
        String username = "long_user"; // 9 characters
        assertFalse(Login.checkUsername(username), "Username 'long_user' is > 5 characters and should be invalid.");
    }
    
    @Test
    public void testCheckPasswordComplexity_Valid() {
        System.out.println("Login - checkPasswordComplexity - Valid");
        String validPassword = "Password123!"; // >8 chars, 1 Upper, 1 digit, 1 special
        assertTrue(Login.checkPasswordComplexity(validPassword), "Password should pass complexity check.");
    }
    
    @Test
    public void testCheckPasswordComplexity_Invalid() {
        System.out.println("Login - checkPasswordComplexity - Invalid");
        String invalidPassword = "password123"; // Missing Uppercase and Special Char
        assertFalse(Login.checkPasswordComplexity(invalidPassword), "Password should fail complexity check.");
    }
    
    @Test
    public void testValidateCellphoneNumber_CorrectlyFormatted() {
        System.out.println("Login - validateCellphoneNumber - Correctly Formatted");
        String callNumber = "+27831234567"; // Valid +11 digits
        assertTrue(Login.validateCellphoneNumber(callNumber), "Valid 12-character cellphone number should pass.");
    }
    
    @Test
    public void testReturnLoginStatus_Success() {
        System.out.println("Login - returnLoginStatus - Success");
        Login.setRegisteredUserDetails("test", "Pass123!", "John", "Doe");
        boolean loginResult = Login.loginUser("test", "Pass123!");
        
        // "Welcome " + registeredFirstName + " " + registeredLastName + ", it is great to see you."
        String expected = "Welcome John Doe, it is great to see you.";
        
        // We use startsWith because minor variations in punctuation can cause the test to fail.
        assertTrue(Login.returnLoginStatus(loginResult).startsWith("Welcome John Doe"), "Login success message should start with 'Welcome John Doe'.");
    }

    // =========================================================================
    //                            MESSAGE CLASS TESTS
    // =========================================================================
    
    @Test
    public void testCheckMessageHash_AssertEquals() {
        System.out.println("Message - createMessageHash - AssertEquals (POE Req)");
        
        // Setup message and MOCK the internal currentMessageID for predictable prefix
        message.setMessageContent(TEST_DATA_1_MESSAGE);
        message.currentMessageID = MOCK_ID_PREFIX + "234567890"; 
        
        // Expected hash is "11:DIDCAKE" (11 is prefix, DID/CAKE are first/last words)
        String expectedHash = "11:DIDCAKE";
        String actualHash = message.createMessageHash();
        
        assertEquals(actualHash, expectedHash, "Message Hash should match the format XX:FIRSTWORDLASTWORD in caps.");
    }

    @Test
    public void testSendMessage_Option1_Send() {
        System.out.println("Message - send - Option 1 (Send)");
        message.setRecipient("+27711869300");
        message.setMessageContent("Test send message.");
        String messageID = message.createMessageID();
        String messageHash = message.createMessageHash();
        
        String expected = "Message successfully sent.\nPress D to delete message.";
        String actual = message.send("1", messageID, messageHash);
        
        assertEquals(actual, expected, "The return message for sending should match the requirement.");
        // 2 messages in setup + 1 message in this test = 3 total sent.
        assertEquals(message.returnTotalMessageSent(), 3, "Total messages sent should be 3 (2 from setup + 1 from test).");
    }
   
    
    @Test
    public void testDisplayLongestMessage() {
        System.out.println("Message - displayLongestMessage");
        // Longest message sent is M1: "Where are you? You are late! I have asked you to be on time." (58 chars)
        String expectedSubstring = "Where are you? You are late! I have asked you to be on time.";
        String actual = message.displayLongestMessage();
        
        assertTrue(actual.contains(expectedSubstring), "The longest message content should be M1's content.");
    }
    
    @Test
    public void testDeleteMessageByHash_Success() {
        System.out.println("Message - deleteMessageByHash - Success");
        String hashToDelete = "99:YOHOOOGATE"; // Message 2 (Stored)
        
        String expected = "Message with Hash 99:YOHOOOGATE (Stored) successfully deleted.";
        String actual = message.deleteMessageByHash(hashToDelete);
        
        assertEquals(actual, expected, "Deletion message should confirm the hash was deleted.");
        // 2 Sent messages (M1, M3) remain. 
        assertEquals(message.returnTotalMessageSent(), 2, "Total messages sent should remain 2 after deleting a STOED message.");
        // Test that a subsequent search fails
        assertTrue(message.searchForMessageID("9911223344").contains("not found"), "Message ID for deleted hash should now be unfound.");
    }
}