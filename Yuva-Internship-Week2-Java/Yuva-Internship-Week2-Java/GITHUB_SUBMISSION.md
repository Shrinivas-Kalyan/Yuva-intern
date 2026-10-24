# GitHub Submission Guide

**Repository name:** `Yuva-Internship-Week2-Java`

**Repository description (paste into GitHub):**
> Week 2 Yuva Internship task: a Java command-line Library Book Inventory Management System with CRUD operations, input validation and exception handling, using ArrayList for in-memory storage.

**Suggested topics:** `java`, `crud`, `cli-application`, `arraylist`, `internship`

## Steps

1. Sign in at https://github.com and click **New repository**.
2. Set the name to `Yuva-Internship-Week2-Java`, paste the description above, choose **Public**, and leave "Add a README", ".gitignore" and "license" **unchecked** (the project already has them).
3. Click **Create repository** and copy the repository URL, for example `https://github.com/your-username/Yuva-Internship-Week2-Java.git`.
4. Open a terminal inside the project folder and run:

```
git init
git add .
git commit -m "Completed Week 2 Java Library Management System"
git branch -M main
git remote add origin YOUR_GITHUB_REPOSITORY_URL
git push -u origin main
```

### Where to replace `YOUR_GITHUB_REPOSITORY_URL`

In the `git remote add origin` command, replace `YOUR_GITHUB_REPOSITORY_URL` with the URL copied in step 3. Example:

```
git remote add origin https://github.com/your-username/Yuva-Internship-Week2-Java.git
```

If Git asks for a name and email first, run:

```
git config --global user.name "Your Name"
git config --global user.email "you@example.com"
```

GitHub no longer accepts account passwords for pushes; use a Personal Access Token or GitHub Desktop/the browser sign-in prompt when asked.

## Before you submit

- Refresh the repository page and confirm `src/`, `README.md`, `.gitignore` and the Word report are visible.
- Copy the repository URL into your internship form, and upload the `.docx` report.
