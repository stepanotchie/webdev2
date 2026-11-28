// Public browser config: never put secrets here (anyone can read this file).
// ""  = same origin: the page and the API are both served by Spring Boot on :8080 (default).
// "http://localhost:8080" = use this when the page is served from another origin (e.g. :5500).
//   In that case the backend must list the page's origin in CORS_ALLOWED_ORIGINS.
window.APP_CONFIG = {
  API_BASE: ""
};
