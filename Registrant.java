import java.util.*;

public class Registrant {

  // Information from Report 1 - Event Registrations
  String firstName;
  String lastName;
  String dateRegistered;
  int userID;
  String subEvent;
  // Information from Report 2 - Registrant Information
  String address1;
  String address2;
  String city;
  String state;
  String zipCode;
  String phone;
  String emailAddress1;
  String emailAddress2;
  // Information from Report 3 - Custom Questions
  HashMap<Integer, String> customQuestions = new HashMap<>();

}
