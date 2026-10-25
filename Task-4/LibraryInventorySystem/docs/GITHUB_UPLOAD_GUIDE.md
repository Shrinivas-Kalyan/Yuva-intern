# Uploading the Week 4 project to GitHub

1. Sign in at https://github.com and click **+ > New repository** (top-right).
2. Repository name: `library-inventory-week4-refactoring` (any clear name is fine). Choose **Public**.
   Leave "Add a README" **unchecked** (this project already has one). Click **Create repository**.
3. Unzip `Week4_Code_Refactoring_Optimization.zip` on your computer.
4. On the new repository page click **uploading an existing file**, then drag in the *contents* of the extracted
   `LibraryInventorySystem` folder (`src`, `docs`, `benchmark`, `pom.xml`, `README.md`, `.gitignore`).
   Keep the folder structure; GitHub keeps folders when you drag them in.
5. In "Commit changes" type a message such as `Week 4: code refactoring and optimization` and click **Commit changes**.
6. Open the repository home page and check that `README.md` is shown below the file list.
7. Copy the address from the browser bar (for example `https://github.com/Shrinivas-Kalyan/<your-repo-name>`).
   This is your Week 4 repository URL for the submission form.

Command-line alternative (inside the extracted folder):

```bash
git init
git add .
git commit -m "Week 4: code refactoring and optimization"
git branch -M main
git remote add origin https://github.com/Shrinivas-Kalyan/<your-repo-name>.git
git push -u origin main
```
