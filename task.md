# Rating System Project

### Project Description:
The goal of the project is to provide an independent rating system for sellers of in-game items (CS:GO, FIFA, Dota, Team Fortress, etc.). The rating is based on comments submitted by users, which are thoroughly verified by trusted individuals. These ratings form the basis for the overall top sellers in various game categories.

### Roles on the Website:
The platform will have three roles: _Administrator_, _Seller_, and _Anonymous User_.

### User Scenarios:
1. _Seller Registration:_ A Seller visits the site and fills out a form to create their profile.
   The Administrator reviews the information and approves or declines the request.
2. _Submitting a Comment:_ An Anonymous User views a Seller's profile and leaves a comment.
   The Administrator verifies the comment and approves or declines it.
3. _Creating a Seller Profile via Comment (Comment + Registration):_ If an Anonymous User doesn't find the seller they want to Comment, they can provide additional information to create the Seller's profile. The Administrator reviews the submission and decides to approve or decline it.

### Core Functionality:
1. Creating Seller Profiles
2. Submitting Comments for Sellers
3. Calculating Seller Ratings
4. Compiling Overall Top Sellers Based on Ratings
5. Filtering by Games and Rating Ranges

### Registration and Authorization:
The registration process includes:
* The User enters the required registration details.
* The system generates a confirmation link (code), stores it in some Cache (Redis, for example), and sends it to the provided email.
* The confirmation codes have a 24-hour expiration period.
* Until the email is confirmed, any login attempts will result in an appropriate error message.

### Model:
#### 1. User Model (Seller or Administrator):
```
User {
    id:         Integer/UID,
    first_name: String,
    last_name:  String,
    password:   String,
    email:      String,
    created_at: Date,
    role:       Enum
}
```

#### Additionally Seller should have the ability to reset their password through the standard process:
* **POST**   _/auth/forgot_password:_ Submit _{email}_ to receive a reset code via email.
* **POST**   _/auth/reset:_ Submit _{code, new_password}_; the system verifies the code from Redis and, if valid, sets the new password.
* **GET**    _/auth/check_code:_ Verify the validity of the reset code and respond accordingly.

#### 2. Comment Model:
```
Comment {
    id:         Integer/UID,
    message:    Text,
    author_id:  Integer/UID,
    created_at: Date,
}
```

#### REST Endpoints for Comments:
* **POST**      _/users/:id/comments_: Add a comment linked to a user.
* **GET**       _/users/:id/comments_: List of seller's comments.
* **GET**       _/users/:id/comments/:id_: View a specific comment.
* **DELETE**    _/users/:id/comments/:id_: Delete a comment (only the author can delete).
* **PUT**       _/users/:id/comments_: Update a comment.

#### 3. Game Object Model:
```
GameObject {
    id:         Integer/UID,
    title:      String,
    text:       Text,
    user_id:    Integer/UID,
    created_at: Date,
    updated_at: Date
}
```

#### REST Endpoints for Game Objects:
* **PUT**       _/object/:id_: Edit an object (only the author can edit).
* **POST**      _/object_: Add a new object.
* **GET**       _/object_: Retrieve game objects.
* **DELETE**    _/object/:id_: Delete an object (only the author can delete).

### Testing:
The project requires setting up a testing environment and creating two unit-tests and two integration tests.

### Project Development Stages:
1. _Design Database Structure:_ Plan and create the database schema. Attach it to the project as an image or PDF file.
2. _Plan Project Architecture:_ Design the project's architecture, create the folder and file structure.
3. _Break Down Development Phases and Estimate Time:_ Divide the project into development phases
   and provide rough time estimates for each phase. Create a file named estimate.md and describe all phases and their durations.
4. _Developing_