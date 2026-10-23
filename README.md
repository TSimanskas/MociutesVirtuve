# Močiutės Receptai

A family recipe site. Anyone can browse recipes. A signed-in user can add recipes, photos, and comments, and edit their own profile.

**Live demo:** [mociutesvirtuve.onrender.com](https://mociutesvirtuve.onrender.com)

Sign in with `admin` / `changeme`.

The free Render plan sleeps when nobody is using the site. The first open can take about a minute. The screen is in Lithuanian.

## How to look around

1. Open the site. On the first page, choose **Užeikite į močiutės virtuvę**.
2. On the left, choose **Visi receptai**. Three recipes are already there: cookies, a simple cake, and Lithuanian cold beetroot soup.
3. Filter by category, or type in the search box. Open a card to read ingredients, steps, and comments.
4. Choose **Prisijungti** at the top right. Enter `admin` and `changeme`.
5. After sign-in, the left side shows **Naujas receptas**. The top right shows your profile and **Atsijungti**.
6. Open one of the seeded recipes. The admin account can edit or delete it, and can leave a comment.
7. Open the profile icon to change the display name and profile photo.

Anyone can create their own account from **Prisijungti**, then **Nauja paskyra**. A normal account can change only its own recipes and comments. The `admin` account can change any of them.

Visitors can read recipes without signing in. Adding a recipe or a comment asks for a login.

## What it does

- Public recipe list, categories, and search
- Recipe pages with ingredients, instructions, and comments
- Register, sign in, and sign out
- Create, edit, and delete your own recipes, including a photo
- Comment on a recipe, and edit or delete your own comments
- Profile name and profile photo
- An admin account that can manage every recipe and comment

## Stack

- **App:** Java 17, Spring Boot 4, Spring Security, Thymeleaf, JPA
- **Data:** PostgreSQL on Neon for the live site, H2 for local `dev`
- **Files:** recipe and profile photos in an `uploads` folder

## Run locally

```bash
./mvnw spring-boot:run
```

Open http://localhost:8080. Local `dev` uses an in-memory H2 database and the same admin account: `admin` / `changeme`.

The live site uses the `postgres` profile and Neon. Set `SPRING_PROFILES_ACTIVE=postgres` and `DATABASE_URL_UNPOOLED` for that. Do not put the database password in this file.

## Tests

```bash
./mvnw test
```
