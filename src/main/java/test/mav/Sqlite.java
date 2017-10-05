package test.mav;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class Sqlite {

	private final static String driverName = "org.sqlite.JDBC";
	private final static String connectionString = "jdbc:sqlite:src\\main\\resources\\library.db";

	private static Connection con;
	private static PreparedStatement stmt; 
	private static ResultSet resSet;


	public static boolean connect() {
		try {
			Class.forName(driverName);
			con=DriverManager.getConnection(connectionString);
			return true;

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return false;

		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
			return false;

		}
	}

	public static void CreateBooks() throws SQLException {
		stmt=con.prepareStatement("CREATE TABLE IF NOT EXISTS books (" + 
				"    books_ID integer PRIMARY KEY AUTOINCREMENT," + 
				"    author VARCHAR(100)," + 
				"    book_name VARCHAR(100)" + 
				")");
		stmt.executeUpdate();
		stmt.close();
	}
	
	public static boolean WriteDB(String author,String bookName)throws IllegalArgumentException{
		List<Book> books=ReadDB(author, bookName);
		if(books.size()>0)throw new IllegalArgumentException("Book already exist");
		
		try {
			stmt=con.prepareStatement("INSERT INTO books (author, book_name) VALUES (?, ?)");
			stmt.setString(1, author);
			stmt.setString(2, bookName);
			stmt.executeUpdate();
			stmt.close();
			return true;
		} catch (SQLException e) {
				
			System.out.println(e.getMessage());
			return false;
		}

	}
	
	public static List<Book> ReadDB(String author,String bookName){
		List<Book> res=new ArrayList<Book>();
		
		String statmentPart1=(bookName.length()==0)?"":"book_name = ? ";
		String statmentPart2=(author.length()==0)?"":"author = ? ";
		String and=(bookName.length()==0||author.length()==0)?"":"AND ";
		String where=(bookName.length()==0&&author.length()==0)?"":"WHERE ";
		
		try {
			String temp="SELECT * FROM books "+where+statmentPart1+and+statmentPart2+ "ORDER BY book_name";
			stmt=con.prepareStatement(temp);
			
			if(!(bookName.length()==0)) {
				stmt.setString(1, bookName);
				if(!(author.length()==0))stmt.setString(2, author);		}
			else {
				if(!(author.length()==0))stmt.setString(1, author);
			}
			
			resSet = stmt.executeQuery();
			res=convResSetToBookList();
			stmt.close();
			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
		return res;
	}

	public static List<Book> ReadDB() 	{
		return ReadDB("","");
	}

	public static void DeleteRow(String bookName)throws IllegalArgumentException, SQLException {
			List<Book> books=ReadDB("",bookName);

			if(books.size()==0)throw new IllegalArgumentException("Wrong book name");
			if(books.size()>1)throw new IllegalArgumentException("Many books");
			
			stmt = con.prepareStatement(
					"DELETE FROM books WHERE book_name=?");
			stmt.setString(1, bookName);
			stmt.executeUpdate();
			stmt.close();
	}
	
	public static void DeleteRow(String author, String bookName) throws IllegalArgumentException,SQLException {
		List<Book> books=ReadDB(author,bookName);

		if(books.size()==0)throw new IllegalArgumentException("Wrong book name");
		
		stmt = con.prepareStatement(
				"DELETE FROM books WHERE book_name=? AND author=?");
		stmt.setString(1, bookName);
		stmt.setString(2, author);
		stmt.executeUpdate();
		stmt.close();
	}
	
	public static void Update(String author,String oldBookName,String newBookName)throws  IllegalArgumentException, SQLException{
		if(ReadDB(author,oldBookName).size()==0)throw new IllegalArgumentException("Wrong book name or author");
		if(ReadDB(author,newBookName).size()==1)throw new IllegalArgumentException("Book already exist");
		
		stmt = con.prepareStatement(
				"UPDATE tv_series SET book_name = ? WHERE book_name = ? AND author=?");
		stmt.setString(1, newBookName);
		stmt.setString(2, oldBookName);
		stmt.setString(3, author);
		stmt.executeUpdate();
		stmt.close();
	}
	public static void Update(String oldBookName,String newBookName)throws  IllegalArgumentException, SQLException{
		List<Book> books=ReadDB("",oldBookName);
		if(books.size()==0)throw new IllegalArgumentException("Wrong book name");
		if(books.size()>1)throw new IllegalArgumentException("Many books");
		
		String author=books.get(0).getAuthor();
		if(ReadDB(author,newBookName).size()!=0)throw new IllegalArgumentException("Book already exist");
		
		stmt = con.prepareStatement(
				"UPDATE books SET book_name = ? WHERE book_name = ?");
		stmt.setString(1, newBookName);
		stmt.setString(2, oldBookName);
		stmt.executeUpdate();
		stmt.close();
	}
	
	public static void CloseDB()
	{
		try {
			con.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	private static List<Book> convResSetToBookList() throws SQLException{
		ArrayList<Book> res=new ArrayList<Book>();
		while(resSet.next()) {
			res.add(new Book(resSet.getString("author"),resSet.getString("book_name")));
		}
		return res;
	}
}

