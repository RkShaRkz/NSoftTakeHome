package com.nsoft.github.data.domain.usecase

import com.nsoft.github.domain.Outcome
import com.nsoft.github.domain.exception.ApiException
import com.nsoft.github.testreplacements.NetworkingUseCaseForTesting
import com.nsoft.github.testreplacements.TestDomainClass
import com.nsoft.github.testreplacements.TestNetworkParams
import com.nsoft.github.testreplacements.TestResponseAdapter
import com.nsoft.github.testreplacements.TestUseCaseParams
import com.nsoft.github.testreplacements.createAnnonymousMockCall
import com.nsoft.github.testreplacements.createAnnonymousNetworkingUseCaseForTesting
import com.nsoft.github.testreplacements.createAnnonymousTestApiCall
import com.google.common.truth.Truth.assertThat
import okhttp3.ResponseBody
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.spy
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Call

@RunWith(MockitoJUnitRunner.StrictStubs::class)
class NetworkUseCaseTest {

    companion object {
        val SAMPLE = 1
    }

    @Test
    fun when_execute_is_called_and_there_is_no_internet_then_the_call_is_not_executed_and_onError_is_called_with_NoInternetException() {
        // Prepare the NetworkingUseCase
        val networkUseCase = createAnnonymousNetworkingUseCaseForTesting(
            callToUse = createAnnonymousTestApiCall(
                call = NetworkingUseCaseForTesting.DEFAULT_MOCK_CALLER_INTERFACE,
                responseAdapterConvertedResponse = TestDomainClass(0) //doesn't matter, it won't reach response adapter
            ),
        )
        val spyNetworkUseCase = spy(networkUseCase)
        // Set it to not check for internet and just 'return' false for it (otherwise we run into problems with NetworkUtils)
        doReturn(false).`when`(spyNetworkUseCase).hasInternetAvailable()

        // Invoke the usecase
        val result: Outcome<TestDomainClass, Exception> = spyNetworkUseCase.execute(TestUseCaseParams(SAMPLE))

        // Validate that we got an unsuccessful Outcome containing "NoInternet"
        assertThat(result).isInstanceOf(Outcome.FailedOutcome::class.java)
        assertThat(result.isSuccessful()).isFalse()
        assertThat(result.getError()).isEqualTo(ApiException.NoInternetException)

        // Verify that we checked for internet once and called onError() with the test exception
        verify(spyNetworkUseCase, times(1)).hasInternetAvailable()
        verify(spyNetworkUseCase, times(1)).onError(ApiException.NoInternetException)
    }

    @Test
    fun when_execute_is_called_and_there_is_internet_then_provideParams_is_called_with_params_from_execute() {
        val executionParams = TestUseCaseParams(SAMPLE)
        // Prepare the NetworkingUseCase
        val networkUseCase = createAnnonymousNetworkingUseCaseForTesting(
            callToUse = createAnnonymousTestApiCall(
                call = NetworkingUseCaseForTesting.DEFAULT_MOCK_CALLER_INTERFACE,
                responseAdapterConvertedResponse = TestDomainClass(0) //doesn't matter, we're not checking this in this test
            ),
        )
        val spyNetworkUseCase = spy(networkUseCase)
        // Set it to not check for internet and just 'return' true for it (otherwise we run into problems with NetworkUtils)
        doReturn(true).`when`(spyNetworkUseCase).hasInternetAvailable()

        // Invoke the usecase
        val result: Outcome<TestDomainClass, Exception> = spyNetworkUseCase.execute(executionParams)

        verify(spyNetworkUseCase, times(1)).provideParams(executionParams)
    }

    @Test
    fun when_execute_is_called_and_there_is_internet_then_RequestAdapter_converts_adapted_params_from_provideParams() {
        val executionParams = TestUseCaseParams(SAMPLE)
        // Prepare the NetworkingUseCase
        val networkUseCase = createAnnonymousNetworkingUseCaseForTesting(
            callToUse = createAnnonymousTestApiCall(
                call = NetworkingUseCaseForTesting.DEFAULT_MOCK_CALLER_INTERFACE,
                responseAdapterConvertedResponse = TestDomainClass(0) //doesn't matter, we're not checking for this in this test
            ),
        )
        val spyNetworkUseCase = spy(networkUseCase)
        // Set it to not check for internet and just 'return' true for it (otherwise we run into problems with NetworkUtils)
        doReturn(true).`when`(spyNetworkUseCase).hasInternetAvailable()

        // Invoke the usecase
        val result: Outcome<TestDomainClass, Exception> = spyNetworkUseCase.execute(executionParams)

        verify(spyNetworkUseCase, times(1)).provideParams(executionParams)
    }

/*
  // CANNOT VERIFY PRIVATE METHODS OF SUPERCLASS, SO LETS JUST VERIFY THE PUBLIC ONES
  // unfortunatelly, this test doesn't make much sense :/
    @Test
    fun when_execute_is_called_and_there_is_internet_then_call_is_executed_and_if_response_is_successful_then_handleSuccess_is_called_and_calls_into_onSuccess__with_response() { }
 */

    @Test
    fun when_handleSuccess_is_called_then_ResponseAdapter_converts_json_and_calls_into_onSuccess_with_result_and_raw_json() {
        val executionParams = TestUseCaseParams(SAMPLE)
        val responseValue = 13
        val responseContent = "this is my response"
        val responseDomainData = TestDomainClass(responseValue)
        val successfulResponse = Outcome.successfulOutcome(responseDomainData)

        // The response adapter we'll spy on
        val responseAdapter = TestResponseAdapter(responseDomainData)
        val spyAdapter = spy(responseAdapter)

        // Prepare the NetworkingUseCase
        val networkUseCase = createAnnonymousNetworkingUseCaseForTesting(
            createAnnonymousTestApiCall(
                call = { headers, values ->
                    createAnnonymousMockCall(
                        responseBodyContent = responseContent,
                        stubResponse = true
                    )
                },
                responseAdapter = spyAdapter
            ),
            providedNetworkParams = TestNetworkParams(responseValue),
            successResponse = successfulResponse
        )
        val spyNetworkUseCase = spy(networkUseCase)
        // Set it to not check for internet and just 'return' true for it (otherwise we run into problems with NetworkUtils)
        doReturn(true).`when`(spyNetworkUseCase).hasInternetAvailable()

        // Invoke the usecase
        val result: Outcome<TestDomainClass, Exception> = spyNetworkUseCase.execute(executionParams)

        // Since we're not testing the Request/Response adapter, we will not validate what we *get* from ResponseAdapter.convert()
        // It's completely out of scope for this test

        verify(spyAdapter, times(1)).convert(responseContent)
        verify(spyNetworkUseCase, times(1)).onSuccess(executionParams, responseDomainData, responseContent)
    }

    @Test
    fun when_handleSuccess_is_called_and_response_body_is_null_or_raw_json_is_null_then_handleError_is_called_which_calls_into_onError_with_EmptyResponse_exception() {
        // We cannot verify handleSuccess() but we can verify that we do get the EmptyResponse in onError() when everything "goes fine"
        val executionParams = TestUseCaseParams(SAMPLE)
        val responseValue = 13
        val responseContent = null  //THE IMPORANT BIT
        val responseDomainData = TestDomainClass(responseValue)
        val successfulResponse = Outcome.successfulOutcome(responseDomainData)

        // The response adapter we'll spy on
        val responseAdapter = TestResponseAdapter(responseDomainData)
        val spyAdapter = spy(responseAdapter)

        // Prepare the NetworkingUseCase
        val networkUseCase = createAnnonymousNetworkingUseCaseForTesting(
            createAnnonymousTestApiCall(
                call = { headers, values ->
                    createAnnonymousMockCall(
                        responseBodyContent = responseContent,
                        stubResponse = true
                    )
                },
                responseAdapter = spyAdapter
            ),
            providedNetworkParams = TestNetworkParams(responseValue),
            successResponse = successfulResponse
        )
        val spyNetworkUseCase = spy(networkUseCase)
        // Set it to not check for internet and just 'return' true for it (otherwise we run into problems with NetworkUtils)
        doReturn(true).`when`(spyNetworkUseCase).hasInternetAvailable()

        // Invoke the usecase
        val result: Outcome<TestDomainClass, Exception> = spyNetworkUseCase.execute(executionParams)

        // Verify call to onError and that adapter did absolutely nothing
        verifyNoInteractions(spyAdapter)
        verify(spyNetworkUseCase, times(1)).onError(ApiException.EmptyResponse)
    }

    @Test
    fun when_execute_is_called_and_there_is_internet_then_call_is_executed_and_if_response_is_not_successful_then_handleFailure_is_called_with_response() {
        // We cannot verify handleFailure() but we can verify that we do call into onFailure with the response
        val executionParams = TestUseCaseParams(SAMPLE)
        val responseContent = "some unnecessary nonnull error content"
        val responseError = Exception("This is me, mario!")
        val errorResponseCode = 1337    //because why not be leet
        val failedResponse: Outcome<TestDomainClass, Exception> = Outcome.unsuccessfulOutcome(responseError)

        // Prepare the NetworkingUseCase
        val networkUseCase = createAnnonymousNetworkingUseCaseForTesting(
            createAnnonymousTestApiCall(
                call = { headers, values ->
                    createAnnonymousMockCall(
                        responseBodyContent = responseContent,
                        successfulResponse = false,  //THE IMPORTANT BIT
                        responseCode = errorResponseCode,
                        stubResponse = true
                    )
                },
            ),
            failureResponse = failedResponse
        )
        val spyNetworkUseCase = spy(networkUseCase)
        // Set it to not check for internet and just 'return' true for it (otherwise we run into problems with NetworkUtils)
        doReturn(true).`when`(spyNetworkUseCase).hasInternetAvailable()

        // Invoke the usecase
        val result: Outcome<TestDomainClass, Exception> = spyNetworkUseCase.execute(executionParams)

        // CANNOT VERIFY PRIVATE METHODS OF SUPERCLASS, SO LETS JUST VERIFY THE PUBLIC ONES
//        verify(spyNetworkUseCase, times(1)).handleFailure(responseContent)
        verify(spyNetworkUseCase, times(1)).onFailure(executionParams, errorResponseCode, responseContent)
        assertThat(result).isInstanceOf(Outcome.FailedOutcome::class.java)
        assertThat(result.isSuccessful()).isFalse()
        assertThat(result.getError()).isEqualTo(responseError)
    }

/*
  // CANNOT VERIFY PRIVATE METHODS OF SUPERCLASS, SO LETS JUST VERIFY THE PUBLIC ONES
  // unfortunatelly, this test doesn't make much sense since it would be an identical copy of the one above.
  // the idea is to verify that handleFailure() and onFailure() are called with identical instances of things but...

    @Test
    fun when_handleFailure_is_called_then_response_parameters_are_forwarded_to_onFailure() {    }
 */

    @Test
    fun when_execute_is_called_and_there_is_internet_then_call_is_executed_and_if_exception_happens_then_handleError_is_called_with_exception_that_happened() {
        // We cannot verify handleError() but we can verify that we do call into onError with the exception
        // and the way to test this, is by making the *call* itself throw when we call execute() on it.
        val executionParams = TestUseCaseParams(SAMPLE)
        val responseContent = "irrelevant giberrish"
        val callException = RuntimeException("ho ho ho this is serious")

        val mockedCall: Call<ResponseBody> = createAnnonymousMockCall(
            responseBodyContent = responseContent,
            successfulResponse = true
        )
        // Setup the mock to throw when we execute() it
        doThrow(callException).`when`(mockedCall).execute()

        // Prepare the NetworkingUseCase
        val networkUseCase = createAnnonymousNetworkingUseCaseForTesting(
            createAnnonymousTestApiCall(
                call = { headers, values ->
                    mockedCall
                },
            ),
            errorResponse = Outcome.unsuccessfulOutcome(callException)  // Unfortunatelly, this needs to be set as well...
        )
        val spyNetworkUseCase = spy(networkUseCase)
        // Set it to not check for internet and just 'return' true for it (otherwise we run into problems with NetworkUtils)
        doReturn(true).`when`(spyNetworkUseCase).hasInternetAvailable()

        // Invoke the usecase
        val result: Outcome<TestDomainClass, Exception> = spyNetworkUseCase.execute(executionParams)

        // CANNOT VERIFY PRIVATE METHODS OF SUPERCLASS, SO LETS JUST VERIFY THE PUBLIC ONES
//        verify(spyNetworkUseCase, times(1)).handleError(responseContent)
        verify(spyNetworkUseCase, times(1)).onError(callException)
        assertThat(result).isInstanceOf(Outcome.FailedOutcome::class.java)
        assertThat(result.isSuccessful()).isFalse()
        assertThat(result.getError()).isEqualTo(callException)
    }

/*
  // CANNOT VERIFY PRIVATE METHODS OF SUPERCLASS, SO LETS JUST VERIFY THE PUBLIC ONES
  // unfortunatelly, this test doesn't make much sense since it would be an identical copy of the one above.
  // the idea is to verify that handleError() and onError() are called with identical instances of things but...

    @Test
    fun when_handleError_is_called_then_error_is_forwarded_to_onError() {    }
 */
}