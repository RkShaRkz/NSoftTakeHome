package com.nsoft.github.util

import com.nsoft.github.domain.model.GitRepository
import java.io.File
import javax.inject.Inject

/**
 * Class that performs fuzzy filtering by using a mix of [String.contains] and Levenshtein distance.
 *
 * @see matchesFuzzySearch
 * @see levenshteinDistance
 */
class FuzzyFilterer @Inject constructor(

) {
    /**
     * Function that calculates the Levenshtein distance between two strings. This is useful for finding out 'approximate' or fuzzy matches
     * which aren't strictly tied to a true/false value but degree of truth which can be mapped to fuzzy results.
     *
     * @param str1 First string
     * @param str2 Second string
     * @return the 'distance' between [str1] and [str2] or rather, how similar the two terms are
     */
    fun levenshteinDistance(str1: String, str2: String): Int {
        val m = str1.length
        val n = str2.length

        val dp = Array(m + 1) { IntArray(n + 1) }

        for (i in 0..m) {
            for (j in 0..n) {
                dp[i][j] = when {
                    i == 0 -> j
                    j == 0 -> i
                    else -> {
                        val cost = if (str1[i - 1] == str2[j - 1]) 0 else 1
                        minOf(dp[i - 1][j] + 1, dp[i][j - 1] + 1, dp[i - 1][j - 1] + cost)
                    }
                }
            }
        }
        return dp[m][n]
    }

    /**
     * Function that checks whether a file named [name] matches our fuzzy [searchTerm] by having it's
     * levenshtein distance smaller than [ACCEPTABLE_LEVENSHTEIN_DISTANCE] (which defaults to 4)
     *
     * If the file's name is within acceptable levenshtein distance of our search term, it meets the fuzzy search criteria
     *
     * @param name the name of the thing we're checking to be included in the fuzzy search results
     * @param searchTerm the fuzzy search term to match
     * @param ACCEPTABLE_LEVENSHTEIN_DISTANCE the acceptable levenshtein distance. Defaults to 4.
     * @return whether the name matches the fuzzy search term or not
     *
     * @see levenshteinDistance
     */
    fun matchesFuzzySearch(name: String, searchTerm: String, ACCEPTABLE_LEVENSHTEIN_DISTANCE: Int = 4): Boolean {
        return if (searchTerm.isBlank()) {
            true
        } else {
            name.contains(searchTerm, ignoreCase = true)
                    || levenshteinDistance(name.lowercase(), searchTerm.lowercase()) <= ACCEPTABLE_LEVENSHTEIN_DISTANCE
        }
    }
}
