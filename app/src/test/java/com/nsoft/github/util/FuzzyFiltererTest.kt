package com.nsoft.github.util

import com.google.common.truth.Truth
import org.junit.Before
import org.junit.Test
//import kotlin.test.assertEquals

class FuzzyFiltererTest {
    private lateinit var fuzzyFilterer: FuzzyFilterer

    @Before
    fun setup() {
        fuzzyFilterer = FuzzyFilterer()
    }

    @Test
    fun `test sqiare against square`() {
        val actualName = "square"
        val testName = "sqiare"
        val result = fuzzyFilterer.matchesFuzzySearch(actualName, testName)

        Truth.assertThat(result).isEqualTo(true)
    }

    @Test
    fun testLevenshteinDistanceExactMatch() {
        val s1 = "kitten"
        val s2 = "kitten"
        val expectedDistance = 0
        val result = fuzzyFilterer.levenshteinDistance(s1, s2)
//        assertEquals(expectedDistance, result, "The Levenshtein distance between identical strings should be 0")
        Truth.assertThat(result).isEqualTo(expectedDistance)
    }

    @Test
    fun testLevenshteinDistanceSingleEdit() {
        val s1 = "kitten"
        val s2 = "sitten"
        val expectedDistance = 1
        val result = fuzzyFilterer.levenshteinDistance(s1, s2)
//        assertEquals(expectedDistance, result, "The Levenshtein distance should be 1 for a single character change")
        Truth.assertThat(result).isEqualTo(expectedDistance)
    }

    @Test
    fun testLevenshteinDistanceWithInsertions() {
        val s1 = "kitten"
        val s2 = "kittenz"
        val expectedDistance = 1
        val result = fuzzyFilterer.levenshteinDistance(s1, s2)
//        assertEquals(expectedDistance, result, "The Levenshtein distance should be 1 for an added character")
        Truth.assertThat(result).isEqualTo(expectedDistance)
    }

    @Test
    fun testLevenshteinDistanceWithDeletions() {
        val s1 = "kitten"
        val s2 = "kitt"
        val expectedDistance = 2
        val result = fuzzyFilterer.levenshteinDistance(s1, s2)
//        assertEquals(expectedDistance, result, "The Levenshtein distance should be 2 for two deleted characters")
        Truth.assertThat(result).isEqualTo(expectedDistance)
    }

    @Test
    fun testLevenshteinDistanceWithMultipleChanges() {
        val s1 = "kitten"
        val s2 = "sitting"
        val expectedDistance = 3
        val result = fuzzyFilterer.levenshteinDistance(s1, s2)
//        assertEquals(expectedDistance, result, "The Levenshtein distance should be 3 for multiple changes")
        Truth.assertThat(result).isEqualTo(expectedDistance)
    }

    @Test
    fun testLevenshteinDistanceEmptyString() {
        val s1 = ""
        val s2 = "kitten"
        val expectedDistance = s2.length
        val result = fuzzyFilterer.levenshteinDistance(s1, s2)
//        assertEquals(expectedDistance, result, "The Levenshtein distance should be the length of the second string when the first is empty")
        Truth.assertThat(result).isEqualTo(expectedDistance)
    }

    @Test
    fun testLevenshteinDistanceBothEmpty() {
        val s1 = ""
        val s2 = ""
        val expectedDistance = 0
        val result = fuzzyFilterer.levenshteinDistance(s1, s2)
//        assertEquals(expectedDistance, result, "The Levenshtein distance should be 0 when both strings are empty")
        Truth.assertThat(result).isEqualTo(expectedDistance)
    }

    @Test
    fun testLevenshteinDistance_myCase1() {
        val s1 = "dummy-pdf_2"
        val s2 = "dumy"
        val expectedDistance = 7
        val result = fuzzyFilterer.levenshteinDistance(s1, s2)
//        assertEquals(expectedDistance, result, "The Levenshtein distance should be 0 when both strings are empty")
        Truth.assertThat(result).isEqualTo(expectedDistance)
    }

    @Test
    fun testLevenshteinDistance_myCase2() {
        val s1 = "dummy-pdf_2"
        val s2 = "dumypdf"
        val expectedDistance = 4
        val result = fuzzyFilterer.levenshteinDistance(s1, s2)
//        assertEquals(expectedDistance, result, "The Levenshtein distance should be 0 when both strings are empty")
        Truth.assertThat(result).isEqualTo(expectedDistance)
    }

    @Test
    fun testLevenshteinDistance_myCase3() {
        val s1 = "dummy-pdf_2"
        val s2 = "dumy-pdf"
        val expectedDistance = 3
        val result = fuzzyFilterer.levenshteinDistance(s1, s2)
//        assertEquals(expectedDistance, result, "The Levenshtein distance should be 0 when both strings are empty")
        Truth.assertThat(result).isEqualTo(expectedDistance)
    }
}
