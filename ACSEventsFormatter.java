import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import com.opencsv.CSVReader;

// Usage:
// javac -cp '.:opencsv-3.9.jar' ACSEventsFormatter.java
// java -cp '.:opencsv-3.9.jar' ACSEventsFormatter 1EventRegistrations.csv 2RegistrantInformation.csv 3CustomQuestions.csv

public class ACSEventsFormatter {

	public static void main(String args[]) throws IOException {

		int[] customQuestionIDs = { 389112,389113,389114,389115,389116,389117,389118,389119,389120,389122,389123,389121,389124,388950,389038,389039,389040,389041,389042,389043,389044,389045,389046 };
		HashMap<Integer, String> customQuestions = new HashMap<>();
		customQuestions.put(389112, "School");
		customQuestions.put(389113, "Grade");
		customQuestions.put(389114, "Email Address");
		customQuestions.put(389115, "Friend to Hang Out With");
		customQuestions.put(389116, "Home Phone");
		customQuestions.put(389117, "Cell Phone");
		customQuestions.put(389118, "Food Allergies / Medical Needs");
		customQuestions.put(389119, "Animal Allergies");
		customQuestions.put(389120, "Other Animal Allergies");
		customQuestions.put(389121, "Parent/Legal Guardian Phone");
		customQuestions.put(389122, "Permission");
		customQuestions.put(389123, "Parent/Legal Guardian's Full Name");
		customQuestions.put(389124, "Today's Date");
		customQuestions.put(388950, "Guest I'd like to bring");
		customQuestions.put(389038, "Construction Skill Level");
		customQuestions.put(389039, "Concrete Skill Level");
		customQuestions.put(389040, "Drywall Skill Level");
		customQuestions.put(389041, "Prainting Skill Level");
		customQuestions.put(389042, "Plumbing Skill Level");
		customQuestions.put(389043, "Electrical Skill Level");
		customQuestions.put(389044, "Indoor Clean-Up Skill Level");
		customQuestions.put(389045, "Outdoor Yard Work Skill Level");
		customQuestions.put(389046, "Other Special Skills");

		String eventRegistrationsFile = args[0];
		String registrantInformationFile = args[1];
		String customQuestionsFile = args[2];

		CSVReader eventRegistrationsReader = new CSVReader(new FileReader(eventRegistrationsFile));
		CSVReader registrantInformationReader = new CSVReader(new FileReader(registrantInformationFile));
		CSVReader customQuestionsReader = new CSVReader(new FileReader(customQuestionsFile));

		ArrayList<Registrant> registrations = new ArrayList<Registrant>();

		// Process Event Registrations CSV

		List<String[]> eventRegistrationsList = eventRegistrationsReader.readAll();
		List<String[]> registrantInformationList = registrantInformationReader.readAll();
		List<String[]> customQuestionsList = customQuestionsReader.readAll();

		// Convert to 2D array
		String[][] eventRegistrationsDataArr = new String[eventRegistrationsList.size()][];
		eventRegistrationsDataArr = eventRegistrationsList.toArray(eventRegistrationsDataArr);
		// Convert to 2D array
		String[][] registrantInformationDataArr = new String[registrantInformationList.size()][];
		registrantInformationDataArr = registrantInformationList.toArray(registrantInformationDataArr);
		// Convert to 2D array
		String[][] customQuestionsDataArr = new String[customQuestionsList.size()][];
		customQuestionsDataArr = customQuestionsList.toArray(customQuestionsDataArr);


		for(int i=1; i<eventRegistrationsDataArr.length; i++) { // starting at 1 skips the date row
			String subEvent = eventRegistrationsDataArr[i][3]; // subEvent
			if(!subEvent.equals("")) { // Only use the entries with a sub event
				Registrant reg = new Registrant();
				registrations.add(reg);
				reg.userID = Integer.parseInt(eventRegistrationsDataArr[i][6]);
				reg.firstName = eventRegistrationsDataArr[i][7];
				reg.lastName = eventRegistrationsDataArr[i][8];
				reg.dateRegistered = eventRegistrationsDataArr[i][5];
				reg.subEvent = subEvent;

				// Registrant Information
				for(int j=1; j<registrantInformationDataArr.length; j++) {
					if(Integer.parseInt(registrantInformationDataArr[j][2]) == reg.userID) {
						reg.address1 = registrantInformationDataArr[j][7];
						reg.address2 = registrantInformationDataArr[j][8];
						reg.city = registrantInformationDataArr[j][9];
						reg.state = registrantInformationDataArr[j][10];
						reg.zipCode = registrantInformationDataArr[j][11];
						reg.phone = registrantInformationDataArr[j][12];
						if(registrantInformationDataArr[j][13].contains(";")) {
							String[] emailAddresses = registrantInformationDataArr[j][13].split("; ");
							reg.emailAddress1 = emailAddresses[0];
							reg.emailAddress2 = emailAddresses[1];
						} else {
							reg.emailAddress1 = registrantInformationDataArr[j][13];
							reg.emailAddress2 = "";
						}
					}
				}

				// Custom Questions
				for(int j=1; j<customQuestionsDataArr.length; j++) {
					if(Integer.parseInt(customQuestionsDataArr[j][4]) == reg.userID) {
						reg.customQuestions.put(Integer.parseInt(customQuestionsDataArr[j][11]), customQuestionsDataArr[j][10]);
					}
				}


			}
		}

		// Write out a processed CSV

		PrintWriter writer = new PrintWriter("ProcessedRegistrations.csv", "UTF-8");

		// Header
		writer.print("Sub Event, First Name, Last Name, Date Registered, Address 1, Address 2, City, State, Zip Code, Phone, Email 1, Email 2, ");
		for(int i=0; i<customQuestionIDs.length; i++) {
			writer.print(customQuestions.get(customQuestionIDs[i]) + ", ");
		}
		writer.println();

		// Each Registrations
		for(int i=0; i<registrations.size(); i++) {

			Registrant reg = registrations.get(i);
			writer.print(reg.subEvent + ", " + reg.firstName + ", " + reg.lastName + ", " + reg.dateRegistered + ", " + reg.address1 + ", " + reg.address2 + ", " + reg.city + ", " + reg.state);
			writer.print(", " + reg.zipCode + ", " + reg.phone + ", " + reg.emailAddress1 + ", " + reg.emailAddress2 + ", ");
			for(int j=0; j<customQuestionIDs.length; j++) {
				String answer = reg.customQuestions.get(customQuestionIDs[j]);
				if(answer == null) {
					answer = "";
				} else {
					if(answer.contains(",")) {
						answer = answer.replace(',',';'); // Replace commas in fields with a semicolon so Excel isn't confused by the comma
					}
				}
				writer.print(answer + ", ");
			}
			writer.println();

		}

		eventRegistrationsReader.close();
		registrantInformationReader.close();
		customQuestionsReader.close();
		writer.close();

	}

}
