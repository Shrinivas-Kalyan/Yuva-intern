# Testing Checklist

Run the application (`java -cp out LibraryManagementSystem`) and complete each test. Tick the box and note the actual result yourself.

| # | Test | Steps | Expected Result | Pass? |
|---|---|---|---|---|
| 1 | Empty inventory | Start the app, choose **2** | "No books are available in the inventory." | [ ] |
| 2 | Add a book | Choose **1**; title `Effective Java`, author `Joshua Bloch`, ISBN `9780134685991`, year `2018` | "Book added successfully!" with Book ID 1 | [ ] |
| 3 | View all books | Choose **2** after test 2 | Book is listed with ID, title, author, ISBN, year; "Total books: 1" | [ ] |
| 4 | Search for a book | Choose **5**, enter `9780134685991` | "Book found:" followed by the details | [ ] |
| 5 | Search – not found | Choose **5**, enter `1111111111` | "Book not found..." message | [ ] |
| 6 | Update a book | Choose **3**, ISBN `9780134685991`, new author `J. Bloch`, new year `2019` | "Book updated successfully!"; option 2 shows new values | [ ] |
| 7 | Update – not found | Choose **3**, enter `1111111111` | "Book not found..." message | [ ] |
| 8 | Duplicate ISBN | Choose **1**, use ISBN `9780134685991` again (or `978-0-13-468599-1`) | "Error: A book with ISBN ... already exists." Book is not added | [ ] |
| 9 | Invalid publication year | Choose **1**, year `3000` (or `500`) | "Error: Publication year must be between 1000 and <current year>." Book is not added | [ ] |
| 10 | Non-numeric year | Choose **1**, enter year `abc` | "Invalid input. Please enter a whole number." and asks again | [ ] |
| 11 | Empty title | Choose **1**, press Enter for title | "Title cannot be empty. Please try again." and asks again | [ ] |
| 12 | Invalid ISBN | Choose **1**, ISBN `123` | "Invalid ISBN..." and asks again | [ ] |
| 13 | Invalid menu number | Enter `9` at the menu | "Invalid choice. Please enter a number from 1 to 6." Menu shows again | [ ] |
| 14 | Invalid menu text | Enter `xyz` at the menu | "Invalid input. Please enter a whole number." Menu shows again | [ ] |
| 15 | Delete – cancel | Choose **4**, valid ISBN, answer `n` | "Deletion cancelled." Book still exists | [ ] |
| 16 | Delete a book | Choose **4**, valid ISBN, answer `y` | "Book deleted successfully!"; option 2 no longer lists it | [ ] |
| 17 | Delete – not found | Choose **4**, enter an ISBN that is not stored | "Book not found..." message | [ ] |
| 18 | Exit | Choose **6** | Goodbye message; program ends | [ ] |
