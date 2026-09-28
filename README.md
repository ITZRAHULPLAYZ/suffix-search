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
9. [Performance Benchmarks](#performance-benchmarks)
10. [Sample Output](#sample-output)
11. [Complexity Analysis](#complexity-analysis)

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

```bash
javac *.java
java Server
```

Open **http://localhost:8080** in your browser.

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

### Run

```bash
java SuffixSearchApp
```

---

## Performance Benchmarks

| Text Size (chars) | Preprocessing (ms) | Suffix Array Search, 100 queries (ms) | Brute-Force Search, 100 queries (ms) | Index Memory (MB) |
|---|---|---|---|---|
| 1,000 | 0.7 | 0.07 | 0.1 | 0.004 |
| 10,000 | 16.3 | 0.11 | 1.3 | 0.04 |
| 50,000 | 72.4 | 0.20 | 13.8 | 0.20 |
| 100,000 | 95.3 | 0.32 | 24.6 | 0.40 |

> Timings averaged over **3 measurement runs** after **2 JIT warm-up runs** for accuracy.

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


<div align="center">

**KL Deemed University · Department of CSE · DSA-3 · Team 24 · 2025–2026**

*Submitted under the guidance of **Dr. Swathi***

</div>
