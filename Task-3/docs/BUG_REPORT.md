# Bug and Debugging Report

Five controlled bugs were injected one at a time into a **scratch copy** of the source, never into the submitted code.
For each bug the full test suite (78 tests) was run, the failures were recorded, and then the correct code was run again.
Raw output for every bug is in [`debugging/logs/`](debugging/logs/).

> **How these runs were executed:** the sandbox used to prepare this project could not download JUnit from Maven Central,
> so the unmodified test sources were run with a small local JUnit-compatible runner instead of Maven/Surefire.
> The numbers below are real results of that runner. Please also run `mvn clean test` on your own machine and compare.

## Debugging strategy used for every bug

1. Read the names of the failing tests: they say *which behaviour* broke.
2. Read the assertion message (expected vs actual) to see *how* it broke.
3. Find the one method that implements that behaviour and re-read its condition.
4. Make the smallest change that fixes the condition, then re-run the **whole** suite to check nothing else broke.

## Summary

| # | Bug | File | Tests failing with bug | Tests passing after fix |
|---|-----|------|-----------------------|-------------------------|
| 1 | ISBN search uses `startsWith` instead of `equals` | `BookInventory.indexOf` | 1 | 78 / 78 |
| 2 | Delete removes first book instead of the matching one | `BookInventory.deleteBook` | 4 | 78 / 78 |
| 3 | Update modifies first book instead of the matching one | `BookInventory.updateBook` | 5 | 78 / 78 |
| 4 | Duplicate check off by one (`> 0` instead of `>= 0`) | `BookInventory.addBook` | 4 | 78 / 78 |
| 5 | Future publication year accepted (`> 9999`) | `BookValidator.validateYear` | 3 | 78 / 78 |

## Bug 1 - Incorrect ISBN search condition

* **Buggy code:** `getIsbn().startsWith(normalizedIsbn)`
* **Incorrect behaviour:** searching for the 10-digit text `9780134685` finds the 13-digit book `9780134685991`.
* **Detecting test:** `ReadTests.searchWithPartialIsbn_doesNotMatch`
* **Actual result with bug:** `expected: <false> but was: <true>`
* **Root cause:** a prefix comparison was used where an exact match is required.
* **Fix:** `getIsbn().equals(normalizedIsbn)`

## Bug 2 - Incorrect deletion logic

* **Buggy code:** `return books.remove(0);`
* **Incorrect behaviour:** deleting any ISBN removes the first book in the list.
* **Detecting tests:** `deleteExistingBook_removesIt`, `deletedBook_nolongerExists`, `deleteLeavesUnrelatedBooksUnchanged`, `multipleConsecutiveCrudOperations_leaveConsistentState`
* **Actual result with bug:** e.g. `expected: <9780132350884> but was: <9780134685991>`
* **Root cause:** the index found by `indexOf` was computed but a hard-coded `0` was used.
* **Fix:** `return books.remove(index);`

## Bug 3 - Update modifies the wrong book

* **Buggy code:** `Book book = books.get(0);`
* **Incorrect behaviour:** updating any ISBN changes the first book.
* **Detecting tests:** `updateAuthor_isStored`, `updatePublicationYear_isStored`, `updateModifiesOnlyTheTargetBook`, `multipleConsecutiveCrudOperations_leaveConsistentState`, `InventoryAppTest.fullSessionOfConsecutiveOperations_worksEndToEnd`
* **Actual result with bug:** e.g. `expected: <Uncle Bob> but was: <Robert C. Martin>`
* **Root cause:** same pattern as bug 2 - the computed `index` was ignored.
* **Fix:** `Book book = books.get(index);`

## Bug 4 - Duplicate ISBN validation failure

* **Buggy code:** `if (indexOf(book.getIsbn()) > 0)`
* **Incorrect behaviour:** a duplicate of the book stored at position 0 is silently accepted (only duplicates of later books are caught).
* **Detecting tests:** `addBookWithExistingIsbn_throwsDuplicateAndKeepsOriginal`, `addBookWithSameIsbnInDifferentFormat_isDuplicate`, `failedOperationsDoNotCorruptLaterOperations`, `InventoryAppTest.addBookWithDuplicateIsbn_showsError`
* **Actual result with bug:** `Expected DuplicateBookException to be thrown, but nothing was thrown.`
* **Root cause:** `indexOf` returns `-1` for "not found", so "found" means `>= 0`; index `0` is a valid hit.
* **Fix:** `if (indexOf(book.getIsbn()) >= 0)`

## Bug 5 - Invalid publication year accepted

* **Buggy code:** `if (year > 9999)`
* **Incorrect behaviour:** books published in the future (e.g. next year) are accepted.
* **Detecting tests:** `validateYear_upperBoundary_currentYearOkNextYearRejected`, `yearBoundaryValues_areAcceptedAtLimitsAndRejectedBeyond`, `addBookWithFutureYear_isRejected`
* **Actual result with bug:** `Expected InvalidBookException to be thrown, but nothing was thrown.`
* **Root cause:** the upper bound was replaced with an unrealistic constant instead of the current year.
* **Fix:** `if (year > maxYear())`

## Final state

The submitted source contains only the corrected code. None of the injected changes exist in `src/`.
