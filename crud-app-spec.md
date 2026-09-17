# CRUD App Spec — Recipe Manager

## Project Description
Recipe Manager is a single-resource CRUD web application for managing a personal or
small-business recipe collection (relevant to a freelance baking operation — tracking
recipes for breads, cakes, brownies, cookies, cupcakes, and muffins). Users can create
new recipes, view the full collection, update existing recipe details, and delete
recipes that are no longer needed. Each recipe records its name, category, prep time,
number of servings, and a short description.

## Functional Requirements

- REQ-1: The system shall allow a user to create a new recipe by submitting a name,
  category, preparation time (in minutes), number of servings, and an optional
  description through a web form.
- REQ-2: The system shall display a list of all recipes currently in the collection,
  showing each recipe's name, category, prep time, servings, and available actions
  (edit, delete).
- REQ-3: The system shall allow a user to update the details of an existing recipe by
  selecting it from the list and submitting a pre-populated edit form.
- REQ-4: The system shall allow a user to delete a recipe from the collection, after
  which the recipe no longer appears in the list view.
- REQ-5: The system shall validate all recipe form submissions and re-display the form
  with inline error messages when validation fails (e.g., missing name, invalid prep
  time), without saving invalid data.
