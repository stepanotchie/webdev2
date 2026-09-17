# Lab 4: Server-Rendered CRUD Interface — Answers

**Package:** `com.stephanie.webdev2`
**Port:** `8082` (set in `application.properties`)

## Task 1 — Product List Template

`templates/products.html` shows the full product list from `GET /products` using `th:each`. Each product's name and price is shown using `th:text`. A message ("No products available.") is shown only when the list is empty, using `th:if="${products.size() == 0}"`.

The data comes from `ProductRepository`. It is filled with 6 products in two categories (Electronics, Accessories) when the app starts, through `ProductService`.

## Task 2 — Product Detail Template

`templates/product-detail.html` handles `GET /products/{id}`. It shows the product's name and price, and also shows `product.category.name` to display the category. `ProductController#productDetail` finds the product by ID. If it is not found, it throws an `IllegalArgumentException`.

## Task 3 — Shared Navigation Fragment

`templates/fragments/navbar.html` has a `th:fragment="navbar"` block with links to the product list and the create form. This is added into `products.html`, `product-detail.html`, and `product-form.html` using `th:replace="~{fragments/navbar :: navbar}"`. This way, the same nav shows on every page, and the code is not repeated.

## Task 4 — Create Form

`templates/product-form.html` binds to a `Product` object with `th:object="${product}"`. The inputs for `name`, `price`, and `category.name` use `th:field`. The form sends data to `POST /products`. When it works, `ProductController#createProduct` saves the product using `ProductService` and sends the user back to `/products`.

## Task 5 — Binding Errors on the Create Form

`createProduct` takes in `@Valid @ModelAttribute("product") Product product`, right after that comes `BindingResult result`. When `result.hasErrors()` is true, it shows `"product-form"` again instead of saving.

There are two ways an error can happen on the `price` field:
- **Non-numeric input** (like `"abc"`) causes a type error right away, because `price` is a `double`.
- **Non-positive input** (like `"-50"` or `"0"`) is caught by the `@Positive(message = "must be greater than 0")` rule on `Product.price`, checked through `@Valid`.

Both kinds of errors show up in the same place, `th:errors="*{price}"` in `product-form.html`. Whichever error happens is shown next to the field, and the form shows the values the user already typed.
