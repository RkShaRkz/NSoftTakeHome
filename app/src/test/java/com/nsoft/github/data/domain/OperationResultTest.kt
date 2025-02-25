package com.nsoft.github.data.domain

import com.nsoft.github.domain.OperationResult
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class OperationResultTest {

    companion object {
        private const val TEST_VALUE = 1
    }

    @Test
    fun when_calling_getResult_on_successful_result_then_it_gives_obtained_result() {
        // Given a OperationResult instance
        val opResult: OperationResult<Int> = OperationResult.successfulResult(TEST_VALUE)

        // When we call getResult()
        val result = opResult.getResult()

        // Then we obtain the result
        assertThat(result).isEqualTo(TEST_VALUE)
    }

    @Test(expected = IllegalStateException::class)
    fun when_calling_getResult_on_unsuccessful_result_then_it_throws() {
        // Given a OperationResult instance
        val opResult: OperationResult<Int> = OperationResult.unsuccessfulResult()

        // When we call getResult()
        val result = opResult.getResult()

        // This is where exception should be thrown
        // so nothing to really assert here...
    }
}