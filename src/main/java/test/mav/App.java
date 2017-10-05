package test.mav;


import java.sql.SQLException;
import java.util.Scanner;

class App{
	
	public static void main(String[] args) {
		if(!prepare())return;
		String lastCommand;

		printHelp();
		do {
			lastCommand=getInput();
			if(!(Commands.performCommand(lastCommand)))break;
			
		}while(true);


		Sqlite.CloseDB();

	}
	
	private static void printHelp() {
		System.out.println("add book\r\n" + 
				"remove {book_name}\r\n" + 
				"edit {book_name}\r\n" +  
				"all : return a list of all books ordered by name\r\n" + 
				"exit -turn down");
	}

	private static String getInput() {
		Scanner in = new Scanner(System.in);
        String input = in.nextLine();
        return input;
	}
	
	private static boolean prepare() {
		if(!(Sqlite.connect())) {
			System.out.println("Error");
			return false;
		}
		try {
			Sqlite.CreateBooks();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return true;
	}
}