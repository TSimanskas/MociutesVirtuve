# Močiutės Receptai - Project Plan

## Project Goal

Build a Java Spring Boot website for grandma's recipes.

Visitors should be able to:
- Open the home page.
- Read the story behind the website.
- Browse recipes without logging in.
- Open a single recipe page and read the recipe details and comments.

Logged-in users can:
- Register an account.
- Log in.
- Add their own recipes.
- Upload recipe photos.
- Edit and delete their own recipes.
- Add comments to existing recipes.
- Edit and delete their own comments.
- Edit their profile name and profile photo.

## Current Status

The project has completed **Steps 1-6** and has started **Step 7 - Polish**.

The app now has public recipe browsing, authentication, logged-in recipe/comment actions, user profiles, profile image uploads, recipe image uploads, categories, and search.

## Step 1 - Dependencies

Status: Done

Spring Boot dependencies were added or verified:
- Spring Web MVC
- Thymeleaf
- Spring Data JPA
- Spring Security
- Validation
- H2 database
- PostgreSQL driver
- Lombok

## Step 2 - H2 Configuration

Status: Done

`src/main/resources/application.yaml` was configured for:
- H2 database
- H2 console
- JPA schema updates
- application name
- server port

`src/main/resources/application-postgres.yaml` was added for the PostgreSQL profile:
- database: `grandmas_recipes`
- username: `postgres`
- password: `admin`
- PostgreSQL runs on `localhost:5432`
- H2 remains available as the default easy testing database

## Step 3 - Entities and Repositories

Status: Done

Entities created:
- `User`
- `Role`
- `Recipe`
- `Comment`

Repositories created:
- `UserRepository`
- `RecipeRepository`
- `CommentRepository`

Important repository methods added:
- `UserRepository.findByUsername`
- `UserRepository.existsByUsername`
- `RecipeRepository.findAllByOrderByCreatedAtDesc`
- `RecipeRepository.findByAuthorOrderByCreatedAtDesc`
- `CommentRepository.findByRecipeOrderByCreatedAtAsc`

## Step 4 - Public Read-Only Pages

Status: Done

Done:
- `HomeController`
- `RecipeController`
- `/` home page
- `/recipes` usage/instructions page
- `/recipes/all` all-recipes page
- `/recipes/{id}` single recipe page
- public security rules for visitor pages
- shared image asset at `src/main/resources/static/images/mociutes-virtuve.png`
- `DataSeeder` with an admin user and three sample recipes
- recipe categories shown on the all-recipes page and single recipe page
- `/recipes/all` category sidebar with recipe counts and category filtering
- `/recipes/all` search bar for finding recipes by title

Current public routes:
- `/` - home page with image, story, and "Žiūrėti receptus" button
- `/recipes` - instructions page with left sidebar and "Visi receptai" button
- `/recipes/all` - list of all recipes
- `/recipes/{id}` - single recipe detail page

Seeded admin user:
- username: `admin`
- password: `Tomas186!`
- display name: `Tomas`
- role: `ADMIN`

Seeded recipes:
- Sausainiai su šokolado gabaliukais - category: `Desertai`
- Paprastas naminis pyragas - category: `Desertai`
- Lietuviški šalti barščiai - category: `Sriubos`

Available recipe categories:
- `Desertai`
- `Gėrimai`
- `Kepiniai`
- `Konservavimas`
- `Padažai`
- `Pagrindiniai patiekalai`
- `Pusryčiai`
- `Salotos`
- `Sriubos`
- `Užkandžiai`
- `Kita`

Note: categories are shown alphabetically, with `Kita` kept at the bottom.

After `DataSeeder`, test this visitor flow:

```text
/ -> /recipes -> /recipes/all -> /recipes/{id}
```

## Step 5 - Authentication

Status: Done

Done:
- registration page
- login page
- simplified registration with username, `Rodomas vardas`, password, and confirm password
- BCrypt password hashing
- custom user loading from database
- Spring Security login/logout wiring
- public pages remain public
- show logged-in user's profile icon/photo in the navigation
- improve logged-in/logged-out navigation links
- show `Atsijungti` for logged-in users

Current security rules:
- public: `/`, `/main`, `/recipes`, `/recipes/all`, `/recipes/{id}`, `/login`, `/register`, static assets
- logged-in only: add recipe, add comment, edit/delete own content, profile page

The temporary open `SecurityConfig` has been replaced with real login/register rules.

## Step 6 - Logged-In User Actions

Status: Done

Done:
- create recipe form
- save recipe endpoint
- optional recipe image upload
- add comment form
- save comment endpoint
- edit/delete own recipes
- edit/delete own comments
- admin can manage recipes/comments when needed
- user profile page with editable display name and uploaded profile image
- profile shows recipe and comment counts
- top navigation shows a clickable profile icon/photo instead of text
- profile image is selected by clicking the profile icon/photo
- recipe and comment author areas show `Rodomas vardas` and profile icon/photo
- recipe cards can be opened by clicking anywhere on the card

## Step 7 - Polish

Status: Started

Done:
- profile navigation was simplified to icon/photo only
- comments now have a white inner text box
- recipe cards are fully clickable
- categories are alphabetized with `Kita` at the bottom
- recipe search waits before submitting so users can keep typing
- uploaded profile and recipe images have size/type validation
- recipe images can be uploaded instead of pasted as links
- PostgreSQL local database profile was configured and tested
- PostgreSQL tables were recreated with recipe long text stored as normal `text` columns
- uploaded files under `/uploads/**` are public so recipe/profile images show for visitors
- noisy SQL debug logging was disabled for cleaner/faster local runs
- `DataSeeder` was simplified to avoid repeated full recipe queries during startup

Still to improve later:
- final styling cleanup
- full manual browser test pass
- more complete validation message review
- proper deployment setup

## Next Best Step

Run the app and do a full manual test:

```text
http://localhost:8080/
http://localhost:8080/recipes
http://localhost:8080/recipes/all
http://localhost:8080/recipes/1
http://localhost:8080/login
http://localhost:8080/register
http://localhost:8080/profile
```

For PostgreSQL testing, run:

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=postgres"
```

After testing, continue with remaining **Step 7 - Polish** work and deployment preparation.
