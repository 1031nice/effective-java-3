package me.donghun.item14;

public class Book implements Cloneable {

    String title;
    String content;

    public Book(String title, String content) {
        this.title = title;
        this.content = content;
    }

    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    public static void main(String[] args) throws CloneNotSupportedException {
        Book book = new Book("노인과 바다", "노인 승리!");
        Object clone = book.clone();
    }

}
