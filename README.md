# SuffixSearch - Efficient Substring Search via Suffix Arrays

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

**Project Title:** SuffixSearch - Efficient Substring Search via Suffix Arrays

**Course:** Data Structures and Algorithms - 3 (DSA-3)

**Programming Language:** Java (JDK 11+)

**Platform:** Windows / Linux / macOS

**Academic Year:** 2025 - 2026

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
6. [Web Interface](#web-interface)
7. [Getting Started](#getting-started)
8. [Performance Benchmarks](#performance-benchmarks)
9. [Sample Output](#sample-output)
10. [Complexity Analysis](#complexity-analysis)

---

## Overview

SuffixSearch is a Java application that implements efficient substring pattern matching using Suffix Arrays with binary search. It has three modes:

- **Demo:** Builds a suffix array on the text "banana" and runs sample queries.
- **Interactive CLI:** User can enter any text and search patterns interactively.
- **Benchmark:** Compares suffix array search against brute-force (String.indexOf) across text sizes from 500 to 500,000 characters.

A built-in HTTP server also exposes the search as a web app served from the `web/` folder.

---

## Key Features

- Suffix array construction using prefix doubling in O(n log n) time
- LCP array built using Kasai's algorithm in O(n) time
- Pattern search using binary search (lower bound + upper bound) in O(m log n) time
- Brute-force baseline using String.indexOf for performance comparison
- Benchmarks across text sizes from 1,000 to 100,000 characters with heap memory tracking
- Context-aware result display showing surrounding characters for each match

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
|                    +------------------+                         |
+------------------------------------------------------------------+
```

---

## Project Structure

```
suffix-search-repo/
|
+-- SuffixArray.java          # Suffix array + LCP array construction
+-- SearchEngine.java         # Binary search over the suffix array
+-- SearchResult.java         # Holds and displays search results
+-- Server.java               # HTTP server on port 8080
+-- SuffixSearchApp.java      # Main entry point (CLI)
+-- PerformanceAnalyzer.java  # Benchmark harness
|
+-- web/
|   +-- index.html
|
+-- docs/
    +-- DSA-3_Project Abstract Format (1).docx
    +-- SuffixSearch_DSA-3_Project final.pdf
```

---

## Algorithms

### 1. Suffix Array - O(n log n) Prefix Doubling

A suffix array is an integer array `SA[]` where `SA[i]` is the starting index of the i-th smallest suffix of the text.

```
1. Initialize SA = [0, 1, ..., n-1], rank[i] = ASCII(text[i])
2. For gap = 1, 2, 4, ...:
     a. Sort SA by (rank[i], rank[i+gap])
     b. Re-rank based on sorted order
     c. Stop if all ranks are unique
3. Return SA[]
```

Time: O(n log^2 n) | Space: O(n)

---

### 2. LCP Array - O(n) Kasai's Algorithm

Stores the longest common prefix length between consecutive sorted suffixes.

```
h = 0
for i = 0 to n-1:
    if rank[i] > 0:
        j = SA[rank[i] - 1]
        while text[i+h] == text[j+h]: h++
        LCP[rank[i]] = h
        if h > 0: h--
```

Time: O(n)

---

### 3. Pattern Search - O(m log n) Binary Search

Finds all occurrences of a pattern using two binary searches (lower bound and upper bound) over the suffix array.

```
For each SA[mid], compare suffix against pattern character by character.
Lower bound -> first match position
Upper bound -> last match position
```

---

### 4. Brute-Force Baseline - O(n * m)

Uses `String.indexOf` in a loop. Used only to compare performance against the suffix array approach.

---

## Web Interface

```bash
javac *.java
java Server
```

Open `http://localhost:8080` in your browser.

---

## Getting Started

### Requirements

| Requirement | Version |
|---|---|
| Java Development Kit (JDK) | 11 or higher |
| Operating System | Windows / Linux / macOS |

### Compile

```bash
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

---

## Sample Output

```
╔══════════════════════════════════════════════════════════════════════════╗
║   Efficient Substring Search via Suffix Arrays       v1.0               ║
╚══════════════════════════════════════════════════════════════════════════╝

Suffix Array (text = "banana"):
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
║  1      b[ana]na                                                 ║
║  3      ban[ana]                                                 ║
╚══════════════════════════════════════════════════════════════════╝
```

---

## Complexity Analysis

| Operation | Time Complexity | Space Complexity |
|---|---|---|
| Suffix Array build | O(n log^2 n) | O(n) |
| LCP Array build | O(n) | O(n) |
| Pattern search | O(m log n) | O(1) |
| Brute-force | O(n * m) | O(1) |

- `n` = length of the input text
- `m` = length of the search pattern

---

<div align="center">

**KL Deemed University · Department of CSE · DSA-3 · Team 24 · 2025-2026**

*Submitted under the guidance of Dr. Swathi*

</div>
