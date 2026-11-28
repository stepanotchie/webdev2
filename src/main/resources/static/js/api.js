"use strict";

const API_BASE = (window.APP_CONFIG && window.APP_CONFIG.API_BASE) || "";

class ApiError extends Error {
  constructor(message, status, details) {
    super(message);
    this.name = "ApiError";
    this.status = status;   // 0 = the request never got an HTTP response
    this.details = details; // parsed JSON error body, if any
  }
}

// One place for every GET: handles network failure, HTTP errors and the backend's ErrorResponse shape.
async function getJson(path) {
  let response;
  try {
    response = await fetch(`${API_BASE}${path}`, { headers: { Accept: "application/json" } });
  } catch (cause) {
    // fetch() rejects for connection refused AND for CORS blocks; DevTools -> Network/Console tells them apart.
    // The visible message stays short and safe for users; the technical cause is in DevTools.
    throw new ApiError("Can't connect to the server. Please make sure it is running, then try again.", 0);
  }

  const type = response.headers.get("content-type") || "";
  const body = type.includes("application/json") ? await response.json() : null;

  if (!response.ok) {
    const message = body && body.message ? body.message : `Request failed (${response.status})`;
    throw new ApiError(message, response.status, body);
  }
  return body;
}

// Spring returns the page either flat ({content, number, totalPages, ...}) or nested under "page".
// Read both so the UI does not depend on one serialization mode.
function pageMeta(page) {
  const m = page.page || page;
  const number = m.number ?? 0;
  const totalPages = m.totalPages ?? 1;
  const totalElements = m.totalElements ?? page.content.length;
  return { number, totalPages, totalElements, first: number <= 0, last: number >= totalPages - 1 };
}