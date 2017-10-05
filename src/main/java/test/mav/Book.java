package test.mav;

public class Book {
	private String author;
	private String bookName;
	
	public Book() {
	}
	
	public Book(String author,String bookName) {
		this.setAuthor(author);
		this.setBookName(bookName);
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}
	
	public boolean isFull() {
		if(author.length()==0||bookName.length()==0)return false;
		return true;
	}
	
}
