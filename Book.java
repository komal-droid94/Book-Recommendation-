
// Book.java - Represents a book with its attributes
class Book {
    private String title;
    private String author;
    private String genre;
    private double rating;
    private String[] tags;
    
    public Book(String title, String author, String genre, double rating, String[] tags) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.rating = rating;
        this.tags = tags;
    }
    
    // Getters
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getGenre() { return genre; }
    public double getRating() { return rating; }
    public String[] getTags() { return tags; }
    
    @Override
    public String toString() {
        return String.format("'%s' by %s (Rating: %.1f)", title, author, rating);
    }
}

// UserProfile.java - Stores user preferences
class UserProfile {
    private String[] favoriteAuthors;
    private String[] favoriteTags;
    private double minRating;
    
    public UserProfile(String[] favoriteAuthors, String[] favoriteTags, double minRating) {
        this.favoriteAuthors = favoriteAuthors;
        this.favoriteTags = favoriteTags;
        this.minRating = minRating;
    }
    
    public String[] getFavoriteAuthors() { return favoriteAuthors; }
    public String[] getFavoriteTags() { return favoriteTags; }
    public double getMinRating() { return minRating; }
}

// RecommendationEngine.java - Contains recommendation algorithms
class RecommendationEngine {
    private Book[] bookDatabase;
    
    public RecommendationEngine(Book[] bookDatabase) {
        this.bookDatabase = bookDatabase;
    }
    
    // Algorithm 1: Content-Based Filtering (tag matching)
    public Book[] contentBasedRecommendation(UserProfile user, int limit) {
        // Create array to store books with their scores
        BookScore[] scoredBooks = new BookScore[bookDatabase.length];
        int count = 0;
        
        // Calculate similarity score for each book
        for (Book book : bookDatabase) {
            double score = calculateSimilarityScore(book, user);
            
            // Only include books that match preferences and meet minimum rating
            if (score > 0 && book.getRating() >= user.getMinRating()) {
                scoredBooks[count] = new BookScore(book, score);
                count++;
            }
        }
        
        // Sort by score (highest first)
        sortByScore(scoredBooks, count);
        
        // Return top N books
        int resultSize = Math.min(limit, count);
        Book[] results = new Book[resultSize];
        for (int i = 0; i < resultSize; i++) {
            results[i] = scoredBooks[i].book;
        }
        
        return results;
    }
    
    // Calculate similarity score based on tag matching and author preference
    private double calculateSimilarityScore(Book book, UserProfile user) {
        double score = 0;
        String[] userTags = user.getFavoriteTags();
        String[] bookTags = book.getTags();
        
        // Count matching tags (1 point each)
        for (String userTag : userTags) {
            for (String bookTag : bookTags) {
                if (userTag.equalsIgnoreCase(bookTag)) {
                    score += 1.0;
                }
            }
        }
        
        // Bonus for favorite authors (3 points)
        for (String author : user.getFavoriteAuthors()) {
            if (book.getAuthor().equalsIgnoreCase(author)) {
                score += 3.0;
            }
        }
        
        return score;
    }
    
    // Sort books by score using bubble sort
    private void sortByScore(BookScore[] books, int size) {
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - i - 1; j++) {
                if (books[j].score < books[j + 1].score) {
                    // Swap
                    BookScore temp = books[j];
                    books[j] = books[j + 1];
                    books[j + 1] = temp;
                }
            }
        }
    }
    
    // Helper class to store book with its score
    private class BookScore {
        Book book;
        double score;
        
        BookScore(Book book, double score) {
            this.book = book;
            this.score = score;
        }
    }
    
    // Algorithm 2: Rating-Based Filtering (sorted by rating)
    public Book[] ratingBasedRecommendation(String genre, int limit) {
        // Filter books by genre
        Book[] genreBooks = filterByGenre(genre);
        
        // Sort by rating using bubble sort (descending order)
        bubbleSortByRating(genreBooks);
        
        // Return top N books
        int resultSize = Math.min(limit, genreBooks.length);
        Book[] topRated = new Book[resultSize];
        System.arraycopy(genreBooks, 0, topRated, 0, resultSize);
        
        return topRated;
    }
    
    // Filter books by genre
    private Book[] filterByGenre(String genre) {
        int count = 0;
        
        // Count matching books
        for (Book book : bookDatabase) {
            if (book.getGenre().equalsIgnoreCase(genre)) {
                count++;
            }
        }
        
        // Create filtered array
        Book[] filtered = new Book[count];
        int index = 0;
        for (Book book : bookDatabase) {
            if (book.getGenre().equalsIgnoreCase(genre)) {
                filtered[index++] = book;
            }
        }
        
        return filtered;
    }
    
    // Bubble sort algorithm (sorts in descending order by rating)
    private void bubbleSortByRating(Book[] books) {
        int n = books.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (books[j].getRating() < books[j + 1].getRating()) {
                    // Swap books
                    Book temp = books[j];
                    books[j] = books[j + 1];
                    books[j + 1] = temp;
                }
            }
        }
    }
}

// Main.java - Driver class to demonstrate the system
public class Main {
    public static void main(String[] args) {
        // Initialize book database
        Book[] bookDatabase = createBookDatabase();
        
        // Create recommendation engine
        RecommendationEngine engine = new RecommendationEngine(bookDatabase);
        
        // Create user profile
        UserProfile user = new UserProfile(
            new String[]{"Rachel Gillig", "Neil Gaiman"},
            new String[]{"magic", "dark", "romance"},
            4.0
        );
        
        System.out.println("=== DARK FANTASY BOOK RECOMMENDATION SYSTEM ===\n");
        
        // Algorithm 1: Content-Based Recommendation
        System.out.println("1. CONTENT-BASED RECOMMENDATIONS");
        System.out.println("   Based on your preferences:");
        System.out.println("   - Favorite Authors: Rachel Gillig, Neil Gaiman");
        System.out.println("   - Favorite Tags: magic, dark, romance");
        System.out.println("   - Minimum Rating: 4.0\n");
        
        Book[] contentRecs = engine.contentBasedRecommendation(user, 5);
        displayRecommendations(contentRecs);
        
        // Algorithm 2: Rating-Based Recommendation
        System.out.println("\n2. TOP-RATED DARK FANTASY BOOKS");
        System.out.println("   Highest rated books in Dark Fantasy genre:\n");
        
        Book[] ratingRecs = engine.ratingBasedRecommendation("Dark Fantasy", 5);
        displayRecommendations(ratingRecs);
        
        System.out.println("\n=== END OF RECOMMENDATIONS ===");
    }
    
    // Create sample book database
    private static Book[] createBookDatabase() {
        return new Book[] {
            // Rachel Gillig books
            new Book("One Dark Window", "Rachel Gillig", "Dark Fantasy", 4.2,
                new String[]{"magic", "romance", "dark", "fae", "cards"}),
            
            new Book("Two Twisted Crowns", "Rachel Gillig", "Dark Fantasy", 4.3,
                new String[]{"magic", "romance", "dark", "fae", "kingdom"}),
            
            // Neil Gaiman books
            new Book("American Gods", "Neil Gaiman", "Dark Fantasy", 4.1,
                new String[]{"mythology", "magic", "gods", "dark"}),
            
            new Book("The Ocean at the End of the Lane", "Neil Gaiman", "Dark Fantasy", 4.0,
                new String[]{"magic", "childhood", "dark", "mysterious"}),
            
            new Book("Neverwhere", "Neil Gaiman", "Dark Fantasy", 4.1,
                new String[]{"urban fantasy", "london", "dark", "adventure"}),
            
            // Other Dark Fantasy
            new Book("The Name of the Wind", "Patrick Rothfuss", "Dark Fantasy", 4.5,
                new String[]{"magic", "adventure", "academy", "revenge"}),
            
            new Book("The Lies of Locke Lamora", "Scott Lynch", "Dark Fantasy", 4.3,
                new String[]{"heist", "thieves", "dark", "witty"}),
            
            new Book("The Blade Itself", "Joe Abercrombie", "Dark Fantasy", 4.2,
                new String[]{"gritty", "war", "dark", "complex"}),
            
            new Book("Jonathan Strange & Mr Norrell", "Susanna Clarke", "Dark Fantasy", 4.0,
                new String[]{"magic", "historical", "england", "wizards"}),
            
            new Book("Piranesi", "Susanna Clarke", "Dark Fantasy", 4.2,
                new String[]{"mysterious", "labyrinth", "surreal", "magic"}),
            
            new Book("The Library at Mount Char", "Scott Hawkins", "Dark Fantasy", 4.0,
                new String[]{"dark", "mysterious", "gods", "brutal"}),
            
            new Book("The Poppy War", "R.F. Kuang", "Dark Fantasy", 4.1,
                new String[]{"war", "dark", "magic", "military"}),
            
            // Gothic Fiction
            new Book("The Vampire Chronicles", "Anne Rice", "Gothic Fiction", 4.0,
                new String[]{"vampires", "dark", "immortality", "gothic"}),
            
            new Book("Dracula", "Bram Stoker", "Gothic Fiction", 4.0,
                new String[]{"vampires", "gothic", "horror", "classic"}),
            
            new Book("Frankenstein", "Mary Shelley", "Gothic Fiction", 3.8,
                new String[]{"gothic", "horror", "science", "classic"})
        };
    }
    
    // Display recommendations in a formatted way
    private static void displayRecommendations(Book[] books) {
        if (books.length == 0) {
            System.out.println("   No recommendations found.");
            return;
        }
        
        for (int i = 0; i < books.length; i++) {
            System.out.println("   " + (i + 1) + ". " + books[i]);
        }
    }
}