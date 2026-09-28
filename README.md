# SuffixSearch — Efficient Substring Search via Suffix Arrays

<div align="center">

![KL Deemed University](https://img.shields.io/badge/KL%20DEEMED%20UNIVERSITY-555555?style=flat-square)
![CSE](https://img.shields.io/badge/CSE-2196F3?style=flat-square&logoColor=white)
![Academic Year](https://img.shields.io/badge/ACADEMIC%20YEAR-555555?style=flat-square)
![2025-2026](https://img.shields.io/badge/2025--2026-4CAF50?style=flat-square)
![Team](https://img.shields.io/badge/TEAM-555555?style=flat-square)
![24](https://img.shields.io/badge/24-FF5722?style=flat-square)

</div>

---

## Project Information

**Project Title:** SuffixSearch — Efficient Substring Search via Suffix Arrays

**Course:** Data Structures and Algorithms – 3 (DSA-3)

**Programming Language:** Java (JDK 11+)

**Platform:** Windows / Linux / macOS (any JVM-compatible platform)

**Academic Year:** 2025 – 2026

---

## Team Members

| Name | ID Number |
|---|---|
| Sura. Rahul Reddy | 2520030305 |

---

## Supervisor

**Supervisor Name:** Dr. Swathi

---

## Table of Contents

1. [Overview](#overview)
2. [Key Features](#key-features)
3. [Architecture](#architecture)
4. [Project Structure](#project-structure)
5. [Algorithms](#algorithms)
6. [Web Interface & REST API](#web-interface--rest-api)
7. [Getting Started](#getting-started)
8. [Running the Application](#running-the-application)
9. [Performance Benchmarks](#performance-benchmarks)
10. [Sample Output](#sample-output)
11. [Complexity Analysis](#complexity-analysis)
12. [API Reference](#api-reference)

---

## Overview

**SuffixSearch** is a full-stack Java application that demonstrates efficient substring pattern matching using **Suffix Arrays** paired with **binary search**. It provides three distinct usage modes:

- **Phase 1 – Demo:** Automatically constructs a suffix array for the classic `"banana"` text and queries several demo patterns.
- **Phase 2 – Interactive CLI:** Lets the user index any custom text and perform repeated pattern searches from the terminal.
- **Phase 3 – Performance Benchmark:** Measures and compares suffix-array binary search vs. naive brute-force (`String.indexOf`) across text sizes ranging from **500 to 500,000 characters**.

Additionally, a lightweight **HTTP server** exposes the search engine as a REST API, backed by a clean HTML/CSS/JS front-end served from the `web/` directory.

---

## Key Features

- **O(n log n) Suffix Array Construction** — prefix-doubling algorithm, no external libraries.
- **O(m log n) Binary Search** — lower-bound / upper-bound search over the suffix array.
- **O(n) LCP Array** — Kasai's algorithm for the Longest Common Prefix array.
- **Built-in HTTP Server** — powered by `com.sun.net.httpserver.HttpServer` (standard JDK, zero dependencies).
- **REST API** — POST `/api/search` returns JSON with match count, positions, and elapsed time.
- **Web Front-end** — responsive single-page UI in `web/index.html`.
- **Performance Analyzer** — tabular benchmark comparing suffix-array search vs brute-force across multiple text sizes with heap-memory tracking.
- **Zero External Dependencies** — compiles and runs with a plain JDK; no Maven, no Gradle, no third-party JARs.

---

## Architecture

```
+------------------------------------------------------------------+
|                         SuffixSearch                             |
|                                                                  |
|   +-------------+     +--------------+     +----------------+   |
|   | SuffixArray |---->| SearchEngine |---->|  SearchResult  |   |
|   |  (Builder)  |     |  (Searcher)  |     |  (Result DTO)  |   |
|   +-------------+     +------+-------+     +----------------+   |
|          ^                   |                                   |
|          |                   v                                   |
|   +------+------+    +--------------+    +--------------------+  |
|   |SuffixSearch |    |    Server    |    | PerformanceAnalyzer|  |
|   |    App      |    |  (HTTP/8080) |    |   (Benchmarker)   |  |
|   |  (CLI Main) |    +------+-------+    +--------------------+  |
|   +-------------+           |                                   |
|                             v                                   |
|                    +------------------+                         |
|                    |  web/index.html  |                         |
|                    |  (Front-End UI)  |                         |
|                    +------------------+                         |
+------------------------------------------------------------------+
```

---

## Project Structure

```
suffix-search-repo/
|
+-- SuffixArray.java          # Core data structure -- O(n log n) build + Kasai LCP
+-- SearchEngine.java         # Binary search over the suffix array (lower/upper bound)
+-- SearchResult.java         # Result container with pretty-print & context snippets
+-- Server.java               # Built-in JDK HTTP server (port 8080) + REST API
+-- SuffixSearchApp.java      # Main CLI entry point (Phase 1 / 2 / 3)
+-- PerformanceAnalyzer.java  # Benchmark harness with heap-memory tracking
|
+-- web/
|   +-- index.html            # Single-page web front-end
|
+-- docs/
    +-- DSA-3_Project Abstract Format (1).docx
    +-- SuffixSearch_DSA-3_Project final.pdf
```

---

## Algorithms

### 1. Suffix Array — O(n log n) Prefix Doubling

A **suffix array** is an integer array `SA[]` of size `n` where `SA[i]` stores the starting index of the `i`-th lexicographically smallest suffix of the input text.

**Construction steps:**

```
1. Initialize SA = [0, 1, 2, ..., n-1] and rank[i] = ASCII(text[i])
2. For gap = 1, 2, 4, ... (doubling):
     a. Sort SA using (rank[i], rank[i+gap]) as a 2-key comparator
     b. Re-assign ranks based on the new order
     c. If all ranks are distinct -> stop early
3. Return the final SA[]
```

**Time Complexity:** O(n log^2 n) with Java's sort; effectively O(n log n) in practice.
**Space Complexity:** O(n)

---

### 2. LCP Array — O(n) Kasai's Algorithm

The **LCP array** stores the length of the longest common prefix between consecutive suffixes in sorted order (`LCP[i]` = LCP between `SA[i-1]` and `SA[i]`).

**Kasai's key insight:** If the LCP of suffix starting at `i` is `h`, then the LCP of suffix starting at `i+1` is at least `h-1`.

```
h = 0
for i = 0 to n-1:
    if rank[i] > 0:
        j = SA[rank[i] - 1]
        while text[i+h] == text[j+h]: h++
        LCP[rank[i]] = h
        if h > 0: h--
```

**Time Complexity:** O(n)

---

### 3. Pattern Search — O(m log n) Binary Search

Once the suffix array is built, searching for a pattern of length `m` in a text of length `n` reduces to two binary searches:

- **Lower Bound** — first index `lo` where `SA[lo]` has a suffix prefixed by the pattern.
- **Upper Bound** — last index `hi` where `SA[hi]` has a suffix prefixed by the pattern.

All `hi - lo + 1` matches are found in O(m log n) time (each comparison costs O(m)).

```
Compare suffix at SA[mid] against pattern:
  for k = 0 to pattern.length-1:
      if text[SA[mid]+k] != pattern[k]: return diff
  return 0  (suffix starts with pattern)
```

---

### 4. Brute-Force Baseline — O(n * m)

`SearchEngine.bruteForceTime()` uses Java's `String.indexOf` in a loop, providing an O(n*m) baseline for the benchmark comparison.

---

## Web Interface & REST API

### Starting the Web Server

```bash
# Compile all sources
javac *.java

# Launch the HTTP server (serves on port 8080)
java Server
```

Then open **http://localhost:8080** in your browser.

### REST API Endpoint

**POST** `/api/search`

**Request body (JSON):**
```json
{
  "text": "banana",
  "pattern": "ana"
}
```

**Response (JSON):**
```json
{
  "pattern": "ana",
  "count": 2,
  "time": "1.234 µs",
  "positions": [1, 3]
}
```

| Field | Type | Description |
|---|---|---|
| `pattern` | `string` | The searched pattern |
| `count` | `int` | Number of occurrences found |
| `time` | `string` | Elapsed search time (µs or ms) |
| `positions` | `int[]` | Sorted list of starting positions (0-indexed) |

> **Note:** The server caches the last-used `SuffixArray`. If the same text is submitted again, the array is reused — only new texts trigger a rebuild.

---

## Getting Started

### Prerequisites

| Requirement | Version |
|---|---|
| Java Development Kit (JDK) | 11 or higher |
| Operating System | Windows / Linux / macOS |
| External Libraries | None |

### Compilation

```bash
# Navigate to the project root
cd suffix-search-repo

# Compile all Java source files at once
javac *.java
```

---

## Running the Application

### Mode 1 — CLI Application (Phase 1 + 2 + 3)

```bash
java SuffixSearchApp
```

This runs all three phases sequentially:
- **Phase 1** — Automatic demo on `"banana"`
- **Phase 2** — Interactive search (you type text + patterns)
- **Phase 3** — Performance benchmark across 7 text sizes

### Mode 2 — HTTP Server + Web UI

```bash
java Server
```

- Server starts on `http://localhost:8080`
- Static files served from `web/`
- REST API available at `/api/search`

---

## Performance Benchmarks

The `PerformanceAnalyzer` benchmarks the following text sizes with 5 patterns each:

| Text Size | Build (SA) | BinSearch (5q) | Brute-Force (5q) | Speedup |
|---|---|---|---|---|
| 500 chars | ~µs range | ~ns range | ~µs range | high |
| 1 K chars | ~µs range | ~ns range | ~µs range | high |
| 5 K chars | ~ms range | ~µs range | ~µs range | moderate |
| 10 K chars | ~ms range | ~µs range | ~ms range | high |
| 50 K chars | ~ms range | ~µs range | ~ms range | very high |
| 100 K chars | ~ms range | ~µs range | ~ms range | very high |
| 500 K chars | ~ms range | ~µs range | ~ms range | very high |

> Actual timings are averaged over **3 measurement runs** after **2 JIT warm-up runs** for accuracy.

**Key observations:**
- Suffix array construction is a **one-time O(n log n) cost**.
- After construction, each search is **O(m log n)** — sub-millisecond even on 500 K char texts.
- Brute-force scales linearly with text size; the SA approach stays nearly constant per query.

---

## Sample Output

```
╔══════════════════════════════════════════════════════════════════════════╗
║                                                                          ║
║   Efficient Substring Search via Suffix Arrays       v1.0               ║
╚══════════════════════════════════════════════════════════════════════════╝

══════════════════════════════════════════════════════════════════
  PHASE 1 - Suffix Array Demo  (text = "banana")
══════════════════════════════════════════════════════════════════

Suffix Array:
┌──────────────────────────────────────────────────────────────────┐
│  Rank   SA[i]     Suffix                                         │
├──────────────────────────────────────────────────────────────────┤
│  0      5         a                                              │
│  1      3         ana                                            │
│  2      1         anana                                          │
│  3      0         banana                                         │
│  4      4         na                                             │
│  5      2         nana                                           │
└──────────────────────────────────────────────────────────────────┘

╔══════════════════════════════════════════════════════════════════╗
║  Pattern  : "ana"                                                ║
║  Found    : 2 occurrence(s)                                      ║
║  Time     : 1.234 µs                                             ║
╠══════════════════════════════════════════════════════════════════╣
║  Positions: 1, 3                                                 ║
╠══════════════════════════════════════════════════════════════════╣
║  Pos    Context (…[match]…)                                      ║
╠══════════════════════════════════════════════════════════════════╣
║  1      b[ana]na                                                 ║
║  3      ban[ana]                                                 ║
╚══════════════════════════════════════════════════════════════════╝
```

---

## Complexity Analysis

| Operation | Time Complexity | Space Complexity |
|---|---|---|
| Suffix Array build | O(n log^2 n) | O(n) |
| LCP Array build (Kasai) | O(n) | O(n) |
| Pattern search | O(m log n) | O(1) extra |
| Brute-force baseline | O(n * m) | O(1) extra |
| Benchmark (all sizes) | O(sum of ni log^2 ni) | O(max ni) |

Where:
- `n` = length of the input text
- `m` = length of the search pattern
- `ni` = text size at benchmark step `i`

---

## API Reference

### `SuffixArray`

| Method | Return | Description |
|---|---|---|
| `SuffixArray(String text)` | — | Constructs SA + rank + LCP arrays |
| `getSA()` | `int[]` | Returns a copy of the suffix array |
| `getLCP()` | `int[]` | Returns a copy of the LCP array |
| `getText()` | `String` | Returns the indexed text |
| `length()` | `int` | Returns `n` (text length) |
| `printSuffixArray(int maxLen)` | `void` | Pretty-prints the SA table |
| `printLCPArray(int limit)` | `void` | Pretty-prints the LCP table |

### `SearchEngine`

| Method | Return | Description |
|---|---|---|
| `SearchEngine(SuffixArray sa)` | — | Wraps a built suffix array |
| `search(String pattern)` | `SearchResult` | Binary search; returns all matches |
| `bruteForceTime(String pattern)` | `long` | Naive indexOf; returns elapsed ns |

### `SearchResult`

| Method | Return | Description |
|---|---|---|
| `getPattern()` | `String` | The queried pattern |
| `getPositions()` | `List<Integer>` | Sorted list of match positions |
| `getCount()` | `int` | Number of matches |
| `isFound()` | `boolean` | True if at least one match |
| `getElapsedNs()` | `long` | Raw elapsed nanoseconds |
| `getElapsedFormatted()` | `String` | Human-readable time (µs / ms) |
| `print(int maxOccurrences)` | `void` | Pretty-prints the result with context |

### `Server`

| Endpoint | Method | Description |
|---|---|---|
| `/` | GET | Serves `web/index.html` |
| `/api/search` | POST | Performs a search; returns JSON |

---

<div align="center">

**KL Deemed University · Department of CSE · DSA-3 · Team 24 · 2025–2026**

*Submitted under the guidance of **Dr. Swathi***

</div>
