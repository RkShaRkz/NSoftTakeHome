package com.nsoft.github.data.local

import android.content.Context
import android.content.SharedPreferences
import com.nsoft.github.testreplacements.MockableTestEditor
import com.nsoft.github.testreplacements.SharedPreferencesForTesting
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.lenient
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SharedPrefDataSourceTest {

    @Mock
    private lateinit var mockContext: Context

    private lateinit var sharedPrefsForTesting: SharedPreferences

    private lateinit var dataSource: SharedPrefDataSourceImpl

    private companion object {
        const val TEST_KEY = "test_key"
        const val TEST_VALUE = "test_value"
        const val DEFAULT_VALUE = "default"
    }

    private fun createAnnonymousSharedPrefsForTesting(): SharedPreferences {
        return SharedPreferencesForTesting()
    }

    @Before
    fun setup() {
        sharedPrefsForTesting = createAnnonymousSharedPrefsForTesting()

        // SharedPreferencesForTesting creation
        `when`(mockContext.getSharedPreferences(anyString(), anyInt()))
            .thenReturn(sharedPrefsForTesting)

        dataSource = SharedPrefDataSourceImpl(mockContext)
    }

    @Test
    fun getData_withValidKey_returnsStoredValue() {
        // Given
        sharedPrefsForTesting
            .edit()
            .putString(TEST_KEY, TEST_VALUE)
            .commit()

        // When
        val result = dataSource.getData(TEST_KEY, DEFAULT_VALUE)

        // Then
        assertThat(result).isEqualTo(TEST_VALUE)
    }

    @Test
    fun getData_withMissingKey_returnsDefaultValue() {
        // Given
        // Nothing here, we have a blank SharedPreferencesForTesting

        // When
        val result = dataSource.getData(TEST_KEY, DEFAULT_VALUE)

        // Then
        assertThat(result).isEqualTo(DEFAULT_VALUE)
    }

    @Test(expected = IllegalArgumentException::class)
    fun getData_withNullKey_throwsException() {
        dataSource.getData(null, DEFAULT_VALUE)
    }

    @Test
    fun putData_withValidKey_savesValueAndReturnsTrue() {
        // Given
        sharedPrefsForTesting = createAnnonymousSharedPrefsForTesting()
        val spiedSharedPrefs = spy(sharedPrefsForTesting)
        val mockableEditor =
            MockableTestEditor(sharedPrefsForTesting as SharedPreferencesForTesting)
        val spiedEditor = spy(mockableEditor)
        `when`(spiedSharedPrefs.edit()).thenReturn(spiedEditor)
        `when`(mockContext.getSharedPreferences(anyString(), anyInt()))
            .thenReturn(spiedSharedPrefs)

        dataSource = SharedPrefDataSourceImpl(mockContext)

        // When
        val result = dataSource.putData(TEST_KEY, TEST_VALUE)

        // Then
        verify(spiedEditor).putString(TEST_KEY, TEST_VALUE)
        verify(spiedEditor).commit()
        assertThat(result).isTrue()
    }

    @Test
    fun putData_withNullKey_returnsTrue() {
        // Given
        sharedPrefsForTesting = createAnnonymousSharedPrefsForTesting()
        val spiedSharedPrefs = spy(sharedPrefsForTesting)
        val mockableEditor =
            MockableTestEditor(sharedPrefsForTesting as SharedPreferencesForTesting)
        val spiedEditor = spy(mockableEditor)
        // Since the shared prefs aren't even being interacted with due to key being null, we need to use lenient()
        lenient().`when`(spiedSharedPrefs.edit()).thenReturn(spiedEditor)
        lenient().`when`(
            mockContext
                .getSharedPreferences(anyString(), anyInt())
        )
            .thenReturn(spiedSharedPrefs)

        dataSource = SharedPrefDataSourceImpl(mockContext)

        // When
        val result = dataSource.putData(null, TEST_VALUE)

        // Then
        verifyNoInteractions(spiedEditor)
        assertThat(result).isTrue()
    }

    @Test
    fun deleteData_withValidKey_removesValueAndReturnsTrue() {
        // Given
        sharedPrefsForTesting = createAnnonymousSharedPrefsForTesting()
        val spiedSharedPrefs = spy(sharedPrefsForTesting)
        val mockableEditor =
            MockableTestEditor(sharedPrefsForTesting as SharedPreferencesForTesting)
        val spiedEditor = spy(mockableEditor)
        `when`(spiedSharedPrefs.edit()).thenReturn(spiedEditor)
        `when`(mockContext.getSharedPreferences(anyString(), anyInt()))
            .thenReturn(spiedSharedPrefs)

        dataSource = SharedPrefDataSourceImpl(mockContext)

        // When
        val result = dataSource.deleteData(TEST_KEY)

        // Then
        verify(spiedEditor).remove(TEST_KEY)
        verify(spiedEditor).commit()
        assertThat(result).isTrue()
    }

    @Test(expected = IllegalArgumentException::class)
    fun deleteData_withNullKey_throwsException() {
        dataSource.deleteData(null)
    }

    @Test
    fun sharedPreferences_initializedWithCorrectParameters() {
        // Verify SharedPreferences initialization
        verify(mockContext).getSharedPreferences(
            SharedPrefDataSourceImpl.SHARED_PREFS_NAME,
            SharedPrefDataSourceImpl.SHARED_PREFS_MODE
        )
    }
}