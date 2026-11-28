"use strict";

const PAGE_SIZE = 5;
const $ = (selector) => document.querySelector(selector);

const state = {
  pets: { page: 0, species: "", sort: "id", direction: "asc" },
  owners: { page: 0 },
  vaccines: { page: 0 },
};

/* ---------- small DOM helpers (all user/API text goes through textContent) ---------- */

function cell(text, tag = "td") {
  const node = document.createElement(tag);
  node.textContent = text ?? "—";
  return node;
}

function setStatus(node, message, kind = "") {
  node.textContent = message;
  node.dataset.kind = kind;
  node.hidden = !message;
}

function fillRows(tbody, rows, makeCells) {
  tbody.replaceChildren();
  for (const row of rows) {
    const tr = document.createElement("tr");
    tr.append(...makeCells(row));
    tbody.append(tr);
  }
}

function renderPager(container, page, goTo) {
  container.replaceChildren();
  const meta = pageMeta(page);
  if (meta.totalPages <= 1) return;

  const prev = document.createElement("button");
  prev.type = "button";
  prev.className = "secondary";
  prev.textContent = "Previous";
  prev.disabled = meta.first;
  prev.addEventListener("click", () => goTo(meta.number - 1));

  const label = document.createElement("span");
  label.textContent = `Page ${meta.number + 1} of ${meta.totalPages} (${meta.totalElements} total)`;

  const next = document.createElement("button");
  next.type = "button";
  next.className = "secondary";
  next.textContent = "Next";
  next.disabled = meta.last;
  next.addEventListener("click", () => goTo(meta.number + 1));

  container.append(prev, label, next);
}

/* Shared loader for the three list views: loading -> empty / error / rows. */
async function loadList({ path, body, status, pager, emptyMessage, makeCells, goTo }) {
  body.replaceChildren();
  pager.replaceChildren();
  setStatus(status, "Loading…", "info");
  try {
    const page = await getJson(path);
    const rows = page.content;
    if (!Array.isArray(rows)) throw new Error("Unexpected list response");
    fillRows(body, rows, makeCells);
    setStatus(status, rows.length ? "" : emptyMessage, rows.length ? "" : "empty");
    renderPager(pager, page, goTo);
  } catch (error) {
    setStatus(status, error.message, "error");
  }
}

/* ---------- Pets ---------- */

function loadPets() {
  const s = state.pets;
  const params = new URLSearchParams({ page: s.page, size: PAGE_SIZE, sort: s.sort, direction: s.direction });
  if (s.species) params.set("species", s.species);

  return loadList({
    path: `/api/pets?${params}`,
    body: $("#pets-body"),
    status: $("#pets-status"),
    pager: $("#pets-pager"),
    emptyMessage: s.species ? `No pets found for species "${s.species}".` : "No pets yet.",
    makeCells: (pet) => {
      const details = document.createElement("button");
      details.type = "button";
      details.className = "secondary";
      details.textContent = "Details";
      details.setAttribute("aria-label", `Show details for ${pet.name}`);
      details.addEventListener("click", () => showPet(pet.id));
      const actions = document.createElement("td");
      actions.append(details);
      return [cell(pet.id), cell(pet.name), cell(pet.species), cell(pet.microChipNumber), cell(pet.ownerId), actions];
    },
    goTo: (page) => { state.pets.page = page; loadPets(); },
  });
}

async function showPet(rawId) {
  const status = $("#detail-status");
  const body = $("#detail-body");
  body.hidden = true;

  const id = String(rawId).trim();
  if (!/^[1-9][0-9]*$/.test(id)) {
    setStatus(status, "Enter a whole number greater than 0.", "error");
    return;
  }

  setStatus(status, "Loading pet…", "info");
  try {
    const pet = await getJson(`/api/pets/${id}`);
    // Related data: the owner record and this pet's vaccination history.
    const [owner, history] = await Promise.all([
      getJson(`/api/owners/${pet.ownerId}`),
      getJson(`/api/pets/${pet.id}/vaccinations`),
    ]);

    const fields = [
      ["ID", pet.id],
      ["Name", pet.name],
      ["Species", pet.species],
      ["Microchip", pet.microChipNumber],
      ["Contact email", pet.contactEmail],
      ["Owner", `${owner.username} (ID ${owner.id})`],
      ["Owner email", owner.email],
    ];
    const dl = $("#detail-fields");
    dl.replaceChildren();
    for (const [label, value] of fields) {
      const wrap = document.createElement("div");
      const dt = document.createElement("dt");
      dt.textContent = label;
      const dd = document.createElement("dd");
      dd.textContent = value ?? "—";
      wrap.append(dt, dd);
      dl.append(wrap);
    }

    const table = $("#history-table");
    const historyStatus = $("#history-status");
    fillRows($("#history-body"), history, (r) => [cell(r.vaccineName), cell(r.doseNumber), cell(r.dateGiven)]);
    table.hidden = history.length === 0;
    setStatus(historyStatus, history.length ? "" : "No vaccinations recorded for this pet.", history.length ? "" : "empty");

    setStatus(status, "", "");
    body.hidden = false;
  } catch (error) {
    const message = error.status === 404 ? `Pet #${id} was not found.` : error.message;
    setStatus(status, message, "error");
  }
}

/* ---------- Owners & Vaccines ---------- */

function loadOwners() {
  return loadList({
    path: `/api/owners?page=${state.owners.page}&size=${PAGE_SIZE}`,
    body: $("#owners-body"),
    status: $("#owners-status"),
    pager: $("#owners-pager"),
    emptyMessage: "No owners yet.",
    makeCells: (o) => [cell(o.id), cell(o.username), cell(o.email), cell(o.createdAt)],
    goTo: (page) => { state.owners.page = page; loadOwners(); },
  });
}

function loadVaccines() {
  return loadList({
    path: `/api/vaccines?page=${state.vaccines.page}&size=${PAGE_SIZE}`,
    body: $("#vaccines-body"),
    status: $("#vaccines-status"),
    pager: $("#vaccines-pager"),
    emptyMessage: "No vaccines yet.",
    makeCells: (v) => [cell(v.id), cell(v.name), cell(v.stockDoses === 0 ? "0 (out of stock)" : v.stockDoses)],
    goTo: (page) => { state.vaccines.page = page; loadVaccines(); },
  });
}

/* ---------- Tabs & wiring ---------- */

const loaders = { pets: loadPets, owners: loadOwners, vaccines: loadVaccines };

function selectTab(name) {
  for (const key of Object.keys(loaders)) {
    const active = key === name;
    $(`#tab-${key}`).setAttribute("aria-selected", String(active));
    $(`#panel-${key}`).hidden = !active;
  }
  loaders[name]();
}

document.querySelectorAll(".tab").forEach((tab) => {
  tab.addEventListener("click", () => selectTab(tab.id.replace("tab-", "")));
});

$("#pet-filter").addEventListener("submit", (event) => {
  event.preventDefault();
  state.pets.species = $("#filter-species").value.trim();
  state.pets.sort = $("#filter-sort").value;
  state.pets.direction = $("#filter-direction").value;
  state.pets.page = 0; // a new filter always starts from the first page
  loadPets();
});

$("#filter-clear").addEventListener("click", () => {
  $("#pet-filter").reset();
  state.pets = { page: 0, species: "", sort: "id", direction: "asc" };
  loadPets();
});

$("#lookup-form").addEventListener("submit", (event) => {
  event.preventDefault();
  showPet($("#lookup-id").value);
});

$("#api-info").textContent = `API: ${API_BASE || "same origin (" + location.origin + ")"}`;
selectTab("pets");
