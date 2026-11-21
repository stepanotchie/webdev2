# Pet Clinic REST API Documentation
 
Base URL: `http://localhost:8080`  
Content type: `application/json`
 
## Standard Error Response
 
Every error (400, 404, 405, 409, 500) uses the same shape:
 
```json
{
  "status": 400,
  "message": "Validation failed",
  "timestamp": 1795255200000,
  "errors": [
    { "field": "name", "message": "Name is required" }
  ]
}
```
 
| Field | Description |
|---|---|
| `status` | HTTP status code |
| `message` | Human-readable summary |
| `timestamp` | Time of the error in epoch milliseconds |
| `errors` | List of `{ field, message }` for validation failures; `null` for all other errors |
 
## CORS
 
All `/api/**` endpoints accept cross-origin requests from `http://localhost:3000` (methods `GET, POST, PUT, PATCH, DELETE, OPTIONS`, all headers, credentials allowed).
 
## Pet Object (`PetDTO`)
 
| Field | Type | Description |
|---|---|---|
| `id` | Long | Database ID |
| `name` | String | Pet name |
| `species` | String | e.g. `Dog`, `Cat` |
| `microChipNumber` | String | Unique microchip number |
| `ownerId` | Long | ID of the owner (`AppUser`) |
| `contactEmail` | String | Optional contact email for the pet's owner (`null` if not set) |
 
---
 
## 1. `GET /api/pets`
 
Returns a paginated, sortable and optionally filtered list of pets.
 
### Query Parameters
 
| Parameter | Type | Default | Description |
|---|---|---|---|
| `page` | integer | `0` | Page number (0-based). Negative values are treated as `0`. |
| `size` | integer | `20` | Items per page. Capped at **100** (values below 1 become 1). |
| `sort` | string | `id` | Sort field: `id`, `name`, `species`, `microChipNumber`. Also accepts `field,direction` (e.g. `name,asc`). |
| `direction` | string | `asc` | `asc` or `desc` |
| `species` | string | optional | Case-insensitive filter by species (e.g. `Dog`) |
 
### Response Codes
 
| Code | Meaning |
|---|---|
| `200 OK` | A `Page` of pets: `content`, `totalElements`, `totalPages`, `size`, `number`, `first`, `last`, `numberOfElements`, `empty` |
| `400 Bad Request` | Invalid `sort` field, invalid `direction`, or non-numeric `page`/`size` |
| `500 Internal Server Error` | Unexpected server error |
 
### Example Request
 
```bash
curl "http://localhost:8080/api/pets?page=0&size=5&sort=name,asc"
curl "http://localhost:8080/api/pets?species=Dog"
```
 
### Example Response (`200 OK`)
 
```json
{
  "content": [
    { "id": 1, "name": "Buddy", "species": "Dog", "microChipNumber": "MC-1001", "ownerId": 1, "contactEmail": null },
    { "id": 2, "name": "Luna", "species": "Cat", "microChipNumber": "MC-1002", "ownerId": 1, "contactEmail": null }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 5,
    "sort": { "empty": false, "sorted": true, "unsorted": false },
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "last": true,
  "totalElements": 2,
  "totalPages": 1,
  "first": true,
  "size": 5,
  "number": 0,
  "sort": { "empty": false, "sorted": true, "unsorted": false },
  "numberOfElements": 2,
  "empty": false
}
```
 
### Example Error (`400 Bad Request`) — `GET /api/pets?sort=bogus`
 
```json
{
  "status": 400,
  "message": "Invalid sort field 'bogus'. Allowed: [id, name, species, microChipNumber]",
  "timestamp": 1795255200000,
  "errors": null
}
```
 
---
 
## 2. `GET /api/pets/{id}`
 
Returns a single pet.
 
| Path parameter | Type | Required | Description |
|---|---|---|---|
| `id` | Long | Yes | Database ID of the pet |
 
### Response Codes
 
| Code | Meaning |
|---|---|
| `200 OK` | The matching `PetDTO` |
| `400 Bad Request` | `id` is not a number |
| `404 Not Found` | No pet with that `id` |
| `500 Internal Server Error` | Unexpected server error |
 
### Example Request
 
```bash
curl -i "http://localhost:8080/api/pets/1"
```
 
### Example Response (`200 OK`)
 
```json
{ "id": 1, "name": "Buddy", "species": "Dog", "microChipNumber": "MC-1001", "ownerId": 1, "contactEmail": null }
```
 
### Example Error (`404 Not Found`)
 
```json
{
  "status": 404,
  "message": "Pet not found with id: 999",
  "timestamp": 1795255200000,
  "errors": null
}
```
 
---
 
## 3. `POST /api/pets`
 
Creates a new pet for an existing owner.
 
### Request Body (`CreatePetRequest`)
 
| Field | Type | Required | Validation |
|---|---|---|---|
| `name` | String | Yes | `@NotBlank`, `@Size(min = 2, max = 100)` |
| `species` | String | Yes | `@NotBlank`, `@Size(max = 50)` |
| `microChipNumber` | String | Yes | `@NotBlank`, `@Size(max = 50)`, must be unique |
| `ownerId` | Long | Yes | `@NotNull`, `@Min(1)`, owner must exist |
| `contactEmail` | String | No | `@Email` |
 
### Response Codes
 
| Code | Meaning |
|---|---|
| `201 Created` | Pet created; body is the `PetDTO`; `Location` header is `/api/pets/{id}` |
| `400 Bad Request` | Validation failed (all failing fields in `errors`), malformed JSON, or the owner does not exist |
| `409 Conflict` | A pet with the same `microChipNumber` already exists |
| `500 Internal Server Error` | Unexpected server error |
 
### Example Request
 
```bash
curl -i -X POST http://localhost:8080/api/pets \
  -H "Content-Type: application/json" \
  -d '{"name":"Bella","species":"Dog","microChipNumber":"MC-2001","ownerId":1,"contactEmail":"owner@example.com"}'
```
 
### Example Response (`201 Created`)
 
```json
{ "id": 7, "name": "Bella", "species": "Dog", "microChipNumber": "MC-2001", "ownerId": 1, "contactEmail": "owner@example.com" }
```
 
### Example Error (`400 Bad Request`)
 
Request body: `{ "name": "", "microChipNumber": "", "contactEmail": "not-an-email" }`
 
```json
{
  "status": 400,
  "message": "Validation failed",
  "timestamp": 1795255200000,
  "errors": [
    { "field": "name", "message": "Name is required" },
    { "field": "name", "message": "Name must be between 2 and 100 characters" },
    { "field": "species", "message": "Species is required" },
    { "field": "microChipNumber", "message": "Microchip number is required" },
    { "field": "ownerId", "message": "Owner id is required" },
    { "field": "contactEmail", "message": "Invalid email format" }
  ]
}
```
 
### Example Error (`409 Conflict`)
 
```json
{
  "status": 409,
  "message": "A pet with microchip number MC-2001 already exists",
  "timestamp": 1795255200000,
  "errors": null
}
```
 
---
 
## 4. `PUT /api/pets/{id}`
 
Replaces all fields of an existing pet (and can move it to another owner).
 
### Request Body (`UpdatePetRequest`)
 
Same fields and validation as `POST /api/pets`: `name`, `species`, `microChipNumber` and `ownerId` are required; `contactEmail` is optional.
 
### Response Codes
 
| Code | Meaning |
|---|---|
| `200 OK` | The updated `PetDTO` |
| `400 Bad Request` | Validation failed, malformed JSON, or the owner does not exist |
| `404 Not Found` | No pet with that `id` |
| `409 Conflict` | `microChipNumber` belongs to a different pet |
| `500 Internal Server Error` | Unexpected server error |
 
### Example Request
 
```bash
curl -X PUT http://localhost:8080/api/pets/7 \
  -H "Content-Type: application/json" \
  -d '{"name":"Bella Jr","species":"Dog","microChipNumber":"MC-2001","ownerId":1,"contactEmail":"owner@example.com"}'
```
 
### Example Response (`200 OK`)
 
```json
{ "id": 7, "name": "Bella Jr", "species": "Dog", "microChipNumber": "MC-2001", "ownerId": 1, "contactEmail": "owner@example.com" }
```
 
---
 
## 5. `PATCH /api/pets/{id}`
 
Partially updates a pet: only the fields sent in the body are changed.
 
### Request Body (`PatchPetRequest`)
 
| Field | Type | Required | Validation |
|---|---|---|---|
| `name` | String | No | `@Size(min = 2, max = 100)` |
| `species` | String | No | `@Size(min = 1, max = 50)` |
| `microChipNumber` | String | No | `@Size(min = 1, max = 50)`, must be unique |
| `ownerId` | Long | No | `@Min(1)`, owner must exist |
| `contactEmail` | String | No | `@Email` |
 
### Response Codes
 
| Code | Meaning |
|---|---|
| `200 OK` | The patched `PetDTO` |
| `400 Bad Request` | A supplied field failed validation, malformed JSON, or the owner does not exist |
| `404 Not Found` | No pet with that `id` |
| `409 Conflict` | `microChipNumber` belongs to a different pet |
| `500 Internal Server Error` | Unexpected server error |
 
### Example Request
 
```bash
curl -X PATCH http://localhost:8080/api/pets/7 \
  -H "Content-Type: application/json" \
  -d '{"species":"Cat"}'
```
 
### Example Response (`200 OK`)
 
```json
{ "id": 7, "name": "Bella Jr", "species": "Cat", "microChipNumber": "MC-2001", "ownerId": 1, "contactEmail": "owner@example.com" }
```
 
---
 
## 6. `DELETE /api/pets/{id}`
 
Deletes a pet.
 
### Response Codes
 
| Code | Meaning |
|---|---|
| `204 No Content` | Pet deleted; empty body |
| `404 Not Found` | No pet with that `id` |
| `500 Internal Server Error` | Unexpected server error |
 
### Example Request
 
```bash
curl -i -X DELETE http://localhost:8080/api/pets/7
```
 
### Example Response
 
```
HTTP/1.1 204
```
 