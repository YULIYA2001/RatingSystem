# Rating System Project

## Description:
The goal of the project is to provide an independent rating system for sellers of in-game items (CS:GO, FIFA, Dota, Team Fortress, etc.). The rating is based on comments submitted by users, which are thoroughly verified by trusted individuals. These ratings form the basis for the overall top sellers in various game categories.

+ [Full Task Description](task.md)
+ Final [Database schema](db-schema/db.png), draft [schema](db-schema/db-schema-draft-v1.png)
+ [Time estimation](estimate.md)


## Stack:
- **Backend**: Spring Boot 3.x, Spring Security, Spring Data JPA
- **Database**: PostgreSQL
- **Cache**: Redis
- **Authentication**: JWT
- **Testing**: JUnit 5, Mockito
- **Build Tool**: Maven
- **Java**: 25

## Start

## Installation

#### 1. Clone the repository:
```
git clone https://github.com/YULIYA2001/RatingSystem
cd RatingSystem
```

#### 2. Create database and user:
```postgresql
CREATE DATABASE db_name;
CREATE USER user_name WITH PASSWORD 'user_password';
GRANT ALL PRIVILEGES ON DATABASE db_name TO user_name;
```

> \c rating_system postgres
```postgresql
GRANT ALL PRIVILEGES ON SCHEMA public TO user_name;
```

#### 3. Start Redis, fill database.properties and security.properties
```properties
# database.properties
db.url=
db.username=
db.password=
# security.properties
jwt.secret=
mail.support-email=
mail.app-password=
```

#### 4. Run the application:
```
./mvnw spring-boot:run
```

The application will start on http://localhost:8080

## API Endpoints
### Authentication

### Sellers
- **POST /sellers** - Create seller profile 
```json
{
  "nickname": "seller nick",
  "description": "seller description"
}
```
  
- **GET /sellers** - Get list of sellers with filtering (Status param reasonable only for ADMIN)
```
?status=APPROVED&minRating=3.5&maxRating=5&gameId=20&page=0&size=2
```

- **GET /sellers/top-best** - Get best sellers' profiles
```
?topCount=5
```

- **POST /sellers/{id}/approve** - Approve sellers' profile

- **POST /sellers/{id}/reject** - Reject sellers' profile

#### Comments
- **POST /comments** - Create comment with new seller profile (profile creation via comment)
```json
{
  "commentDto": {
    "message": "Test comment",
    "ratingMark": 5
  },
  "sellerDto": {
    "nickname": "Created ViaComment seller nick",
    "description": "Created ViaComment seller description"
  }
}
```

- **GET /comments** - Get list of comments (for ADMIN only)
```
?status=REJECTED&verifiedSeller=false
```

- **POST /comments/approve** - Approve one or several comments (by ids)
```json
[1, 3, 4, 15]
```

- **POST /comments/reject** - Reject one or several comments (by ids)
```json
[1, 3, 4, 15]
```

#### Comments for seller
- **POST /seller/:sellerId/comments** - Add a comment linked to a seller
```json
{
  "message": "Test comment",
  "ratingMark": 5
}
```

- **GET /seller/:sellerId/comments** - List of seller's comments (Status param reasonable only for ADMIN)
```
?status=APPROVED
```

- **GET /seller/:sellerId/comments/:id** - View a specific comment _(all users can see "APPROVED", admin can see "ALL", registered user can see "ALL he owns")_

- **PUT /seller/:sellerId/comments/** - Update a comment (only the author can update)
```json
{
  "id": 11,
  "message": "Test comment",
  "ratingMark": 1
}
```

- **DELETE /seller/:sellerId/comments/:id** - Delete a comment (only the author can delete)




#### GameObjects
- **POST /object** - Add a new object
```json
// With new game creation
{
  "title": "test game object",
  "description": "test game object description",
  "game": {
    "name": "test game",
    "description": "test game description"
  }
}

// With existing game
{
  "title": "test game object",
  "description": "test game object description",
  "game": {
    "id": 1
  }
}
```

- **GET /object** - Retrieve game objects

- **GET /object/games** - Retrieve games

- **PUT /object/{id}** - Edit an object (only the author can edit)
```json
// With new game creation (change game for new one)
{
    "title": "test game object",
    "description": "test game object description",
    "game": {
        "name": "test game",
        "description": "test game description"
    }
}

// Change game for existing one
{
    "title": "test game object",
    "description": "test game object description",
    "game": {
        "id": 1
    }
}
```

- **DELETE /object/{id}** - Delete an object (only the author can delete)


### Project Structure
```
src/
|-- main/
|   |-- java/by/ratingsystem/
|   |   |-- config/
|   |   |-- controller/
|   |   |-- dto/
|   |   |   |-- auth/
|   |   |   |-- comment/
|   |   |   |-- gameobject/
|   |   |   |-- seller/
|   |   |   |-- user/
|   |   |-- exception/
|   |   |   |-- handler/
|   |   |-- model/
|   |   |   |-- base/
|   |   |   |-- enums/
|   |   |-- repository/
|   |   |-- security/jwt/        
|   |   |-- service/       
|   |   |-- specification/         
|   |   |-- RatingSystemApplication.java
|   |-- resources/
|       |-- application.yml
|       |-- schema.sql
|       |-- data.sql
|       |-- database.properties
|       |-- security.properties
|-- test/
|   |-- java/by/ratingsystem/
|   |   |-- integration/
|   |   |   |-- controller
|   |   |   |-- service
|   |   |-- unit/service/
|   |   |-- RatingSystemApplicationTests.java
```

### Default Users
After first run (if using _data.sql_):
- _Admin:_ admin@gmail.com / admin
- _Seller:_ seller@gmail.com / seller
- _Seller2:_ seller2@gmail.com / seller