package com.nsoft.github.data.domain

import com.nsoft.github.data.domain.OperationResultTest.Companion
import com.nsoft.github.domain.OperationResult
import com.nsoft.github.domain.Outcome
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class OutcomeTest {

    companion object {
        private const val TEST_VALUE = 1
    }

    @Test
    fun when_calling_getResult_on_successful_result_then_it_gives_obtained_result() {
        // Given a Outcome instance
        val opResult: Outcome<Int, Exception> = Outcome.successfulOutcome(TEST_VALUE)

        // When we call getResult()
        val result = opResult.getResult()

        // Then we obtain the result
        assertThat(result).isEqualTo(TEST_VALUE)
    }

    @Test(expected = IllegalStateException::class)
    fun when_calling_getResult_on_unsuccessful_result_then_it_throws() {
        // Given a OperationResult instance
        val opResult: Outcome<Int, Exception> = Outcome.unsuccessfulOutcome(Exception())

        // When we call getResult()
        val result = opResult.getResult()

        // This is where exception should be thrown
        // so nothing to really assert here...
    }

    @Test(expected = IllegalStateException::class)
    fun when_calling_getError_on_successful_result_then_it_throws() {
        // Given a Outcome instance
        val opResult: Outcome<Int, Exception> = Outcome.successfulOutcome(TEST_VALUE)

        // When we call getError()
        val result = opResult.getError()

        // This is where exception should be thrown
        // so nothing to really assert here...
    }

    @Test
    fun when_calling_getError_on_unsuccessful_result_then_it_returns_obtained_error() {
        // Given a OperationResult instance
        val testException = Exception("Test exception")
        val opResult: Outcome<Int, Exception> = Outcome.unsuccessfulOutcome(testException)

        // When we call getError()
        val result = opResult.getError()

        // Then assert that obtained result is the one we're expecting
        assertThat(result).isEqualTo(testException)
        assertThat(result.message).isEqualTo(testException.message)
    }
}