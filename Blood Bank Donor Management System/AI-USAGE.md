# AI Tool Usage Log
**Project:** Blood Bank & Donor Management System (JFS-18)

This document tracks all code, architecture, or design segments generated or assisted by AI tools (such as GitHub Copilot, ChatGPT, or Claude), including prompt inputs, generated output, human review, and refactoring applied.

---

### Entry 1: DonationCamp Entity & Constructor Chaining
* **Date:** 2026-10-16
* **AI Tool:** GitHub Copilot / Gemini
* **Prompt Given:**
  > "Generate a new DonationCamp domain model extending BaseEntity with campId, campName, venue, campDate, estimatedDonors, and status."
* **Code Generated:** Initial class with fields, getters/setters, and basic constructor.
* **Human Review & Refinement:**
    * *Discovered Flaw:* The generated class did not validate inputs (allowed negative donors and null dates) and did not automatically invoke `super("CMP-" + (++campCounter))`.
    * *Correction Applied:* Added defensive checks (`Objects.requireNonNull`, positive count assertions) and ensured it matches the `BaseEntity` id-extractor pattern cleanly.

---

### Entry 2: In-Memory Generic Repository Pattern
* **Date:** 2026-10-14
* **AI Tool:** GitHub Copilot
* **Prompt Given:**
  > "Implement a generic CrudRepository<T, ID> interface and ConcurrentHashMap-backed InMemoryRepository without raw type warnings."
* **Code Generated:** Generic class accepting `Function<T, ID>` extractor and basic CRUD operations.
* **Human Review & Refinement:**
    * *Discovered Flaw:* `findAll()` originally returned `storage.values()`, exposing internal map state to concurrent modification and mutation outside the repository.
    * *Correction Applied:* Wrapped the return value in `Collections.unmodifiableList(new ArrayList<>(storage.values()))` to preserve defensive immutability.

---

### Entry 3: FR7 & FR8 Aggregation & Date Filtering
* **Date:** 2026-10-16
* **AI Tool:** GitHub Copilot
* **Prompt Given:**
  > "Write two methods for a console app: one flagging blood units expiring in the next 3 days, and one grouping available stock by blood group and component type using Java Streams."
* **Code Generated:** Java Stream pipeline using `Collectors.groupingBy()` and `ChronoUnit.DAYS.between()`.
* **Human Review & Refinement:**
    * *Discovered Flaw:* Copilot did not filter out `ISSUED` or `EXPIRED` units in FR7, resulting in already issued units triggering false-positive expiry alerts.
    * *Correction Applied:* Added an explicit predicate `.filter(u -> "AVAILABLE".equalsIgnoreCase(u.getStatus()))` to both FR7 and FR8 pipelines.

---
