# Real Estate Rental Platform

## Project Overview

The **Real Estate Rental Platform** is a full-stack web application designed for the property
rental process. The application allows users to browse and filter available properties, view
detailed listings with photo galleries and reviews, and submit rental requests for a chosen
period. Additionally, it provides administrative functionalities for managing properties,
seasonal pricing, rental requests, and reviews.

## Key Features

- **User Management** – Users can register and log in. Passwords are hashed with BCrypt and a JWT
  token is issued on authentication. A user's role determines what actions they can perform within
  the system.
- **Property Management** – Administrators can create, update, and delete properties, assign a
  property type and amenities, and manage each property's photo gallery.
- **Dynamic Search & Filtering** – Users can search and filter properties by city, type, price
  range, and minimum number of rooms. Criteria are composed dynamically, so only the supplied
  filters reach the query.
- **Pagination** – The public listing is paginated (three properties per page by default), sorted
  so that the most recently added properties appear first, with filters preserved across pages.
- **Rental Requests** – Users can submit a rental request for a selected period, view their request
  history, and cancel their own pending requests. Requests transition through different statuses
  (`SENT`, `ACCEPTED`, `REJECTED`, `CANCELLED`), and accepting a request marks the property as
  rented.
- **Seasonal Pricing & Price Calculation** – Administrators can define seasons with a different
  monthly price. The total price is calculated per day, applying the seasonal rate to days that
  fall inside a season, so periods that partially overlap a season are combined correctly.
  Overlapping seasons for the same property are rejected.
- **Reviews & Moderation** – Users can rate a property from 1 to 5 and leave a comment. Reviews
  become publicly visible only after an administrator approves them, and the average rating is
  computed from approved reviews only.
- **Photo Gallery** – Each property has an image gallery with a designated cover image. Clicking a
  thumbnail opens a full-screen lightbox with keyboard and click navigation.
- **Role-Based Access** – Browsing the offer is public, submitting requests and reviews requires
  authentication, while property management, seasonal pricing, request handling, and review
  moderation are restricted to administrators.
- **Request Validation & Error Handling** – Incoming payloads are validated on the server, and all
  exceptions are translated into a consistent error response by a single centralized handler.
- **API Documentation** – The REST API is documented with Swagger/OpenAPI, including a Bearer JWT
  security scheme so protected endpoints can be tried directly from the interface.
- **Containerization** – The database, backend, and frontend are containerized and orchestrated
  with Docker Compose, so the entire stack starts with a single command.
- **Testing** – Unit tests cover the service layer and price calculation with mocked dependencies,
  while integration tests exercise the real HTTP flow for authentication, authorization, and
  pagination against an in-memory database.

## Technologies Used

- **Backend:** Java 17 (`Spring Boot`), `Spring Data JPA` / Hibernate for the relational data
  model, `Spring Security` with `JWT` and BCrypt password hashing, and Bean Validation for request
  validation.
- **Database:** `PostgreSQL` for data storage, with `H2` in-memory used by the test profile.
- **Frontend:** `React` with `Vite` for an interactive user experience, `React Router` for routing,
  and `Axios` for API calls.
- **Testing:** `JUnit`, `Mockito`, and `MockMvc`.
- **Documentation:** `Swagger` / `OpenAPI`.
- **Infrastructure:** `Docker` and `Docker Compose`, with `nginx` serving the frontend build.
