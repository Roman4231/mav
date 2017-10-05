package test.mav;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Commands {

	private static boolean performCommandRes;

	public static boolean performCommand(String lastCommand) {
		performCommandRes=true;
		switch(getCommand(lastCommand)) {
		case "add": 
			addCommand(lastCommand);
			break;
		case "remove": 
			removeCommand(lastCommand);
			break;
		case "edit": 
			editCommand(lastCommand);
			break;
		case "all": 
			allCommand(lastCommand);
			break;
		case "exit":
			performCommandRes=false;
			break;
		default:
			System.out.println("Undefined command");
		};
		return performCommandRes;
	}

	private static void allCommand(String lastCommand) {
		List<Book> books=Sqlite.ReadDB();
		System .out.println("Our books :");
		for(Book i : books){
			System.out.println("\t"+i.getAuthor()+ " “"+i.getBookName()+"”");
		}
	}

	private static void editCommand(String lastCommand) {
		int firstSpace=lastCommand.indexOf(' ');
		String oldBookName=lastCommand.substring(firstSpace+1, lastCommand.length());

		System.out.println("Write new name");
		Scanner in = new Scanner(System.in);
		String newBookName = in.nextLine();
		
		try {
			Sqlite.Update(oldBookName, newBookName);
		} catch (IllegalArgumentException e) {
			if(e.getMessage().equals("Many books")) {
				editCommandManyBooks(oldBookName,newBookName);
			}else {
				System.out.println(e.getMessage());
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}


	private static void editCommandManyBooks(String oldBookName, String newBookName) {
		List<Book> books=Sqlite.ReadDB("",oldBookName);
		
		int rightBook=chooseBook(books);
		
		try {
			Sqlite.Update(books.get(rightBook).getAuthor(), oldBookName, newBookName);
		} catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	private static void removeCommand(String lastCommand) {
		int firstSpace=lastCommand.indexOf(' ');
		String bookName=lastCommand.substring(firstSpace+1, lastCommand.length());
		
		if(!bookNameCheck(bookName)) {
			bookName=bookName.substring(1, bookName.length()-1);
		};
		
		try {
			Sqlite.DeleteRow(bookName);
			System.out.println("book "+"“"+bookName+"” was removed");
		} catch (IllegalArgumentException e) {
			if(e.getMessage().equals("Many books")) {
				removeCommandManyBooks(lastCommand);
			}else {
				System.out.println(e.getMessage());
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}



	private static boolean bookNameCheck(String bookName) {
		if(bookName.startsWith("\"")||bookName.startsWith("“"))return false;
		return true;
	}

	private static void removeCommandManyBooks(String lastCommand) {
		int firstSpace=lastCommand.indexOf(' ');
		String bookName=lastCommand.substring(firstSpace+1, lastCommand.length());

		List<Book> books=Sqlite.ReadDB("",bookName);
		int rightBook=chooseBook(books);

		try {
			Sqlite.DeleteRow(books.get(rightBook).getAuthor(), bookName);
		} catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());
		} catch (SQLException e) {
			System.out.println(e.getMessage());			
		}
	}

	private static int chooseBook(List<Book> books) {
		System.out.println("We have few books with such name please choose one by typing a number of book:");
		for(int i=0;i<books.size();i++) {
			System.out.println("\t"+(i+1)+") "+books.get(i).getAuthor()+ " “"+books.get(i).getBookName()+"”");
		}
		Scanner in = new Scanner(System.in);
		int input = in.nextInt();
		return input-1;
	}

	public static String getCommand(String command) {
		int firstSpace=command.indexOf(' ');
		if(firstSpace<=0)return command;
		return command.substring(0, firstSpace);
	}

	private static void addCommand(String lastCommand) {
		Book book;
		try {
			book=findBook(lastCommand);
		}catch(IllegalArgumentException e) {
			System.out.println(e.getMessage());
			return;
		}
		
		try {
			Sqlite.WriteDB(book.getAuthor(), book.getBookName());

			System.out.println("book "+book.getAuthor()+ " “"+book.getBookName()+"” was added");
		}catch(IllegalArgumentException e){
			System.out.println(e.getMessage());
		}
	}

	private static Book findBook(String command)throws IllegalArgumentException{
		Book res=new Book(); 

		int bookNameStart=command.indexOf('“');
		int bookNameEnd=command.lastIndexOf('”');
		
		if(bookNameStart==-1) {
			bookNameStart=command.indexOf('"');
			bookNameEnd=command.lastIndexOf('"');
			if(bookNameStart==-1)throw new IllegalArgumentException("Wrong message format");
		}
		int firstSpace=command.indexOf(' ');
		res.setBookName(command.substring(bookNameStart+1, bookNameEnd));
		res.setAuthor(command.substring(firstSpace+1,bookNameStart-1));

		return res;
	}


}
