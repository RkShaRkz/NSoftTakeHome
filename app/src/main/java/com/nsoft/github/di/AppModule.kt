package com.nsoft.github.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.nsoft.github.data.local.room.DATABASE_NAME
import com.nsoft.github.data.local.room.Database
import com.nsoft.github.data.local.room.dao.CollaboratorsDao
import com.nsoft.github.data.local.room.dao.FavoriteRepositoryDao
import com.nsoft.github.data.local.room.dao.GitRepositoryDao
import com.nsoft.github.data.remote.ApiService
import com.nsoft.github.data.remote.RetrofitClient
import com.nsoft.github.data.remote.adapters.get_collaborators.GetCollaboratorsRequestAdapter
import com.nsoft.github.data.remote.adapters.get_collaborators.GetCollaboratorsResponseAdapter
import com.nsoft.github.data.remote.adapters.get_repositories.GetRepositoriesRequestAdapter
import com.nsoft.github.data.remote.adapters.get_repositories.GetRepositoriesResponseAdapter
import com.nsoft.github.data.remote.adapters.repository_details.GetRepositoryDetailsRequestAdapter
import com.nsoft.github.data.remote.adapters.repository_details.GetRepositoryDetailsResponseAdapter
import com.nsoft.github.data.remote.calls.ApiCall
import com.nsoft.github.data.remote.calls.ApiCalls
import com.nsoft.github.data.remote.calls.LiteralUrlApiCall
import com.nsoft.github.data.remote.calls.PathApiCall
import com.nsoft.github.data.remote.calls.QueriedApiCall
import com.nsoft.github.data.remote.params.get_collaborators.GetCollaboratorsRequestParams
import com.nsoft.github.data.remote.params.get_repositories.GetRepositoriesRequestParams
import com.nsoft.github.data.remote.params.repository_details.GetRepositoryDetailsRequestParams
import com.nsoft.github.data.repository.GitCollaboratorsRepositoryImpl
import com.nsoft.github.data.repository.GitRepositoriesRepositoryImpl
import com.nsoft.github.data.repository.TransitionalDataRepositoryImpl
import com.nsoft.github.domain.model.GitCollaboratorList
import com.nsoft.github.domain.model.GitRepositoriesList
import com.nsoft.github.domain.model.RepositoryDetails
import com.nsoft.github.domain.repository.GitCollaboratorsRepository
import com.nsoft.github.domain.repository.GitRepositoriesRepository
import com.nsoft.github.domain.repository.TransitionalDataRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.migration.DisableInstallInCheck
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module(includes = [BindingModule::class, NetworkModule::class, RoomModule::class])
@InstallIn(SingletonComponent::class)
object AppModule {
    // We have transitioned from 'abstract class' since this module will be the "providing" module
    // which will only contain concrete @Provides methods in it, and thus - cannot be abstract

    @Provides
    @Singleton
    fun provideApplicationContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create()
    }

}

// we don't want to be bothered because it *should* be installed alongside the AppModule
// since it will be included in it's graph
@DisableInstallInCheck
@Module
abstract class BindingModule {
    // This module will remain 'abstract' since it will contain only abstract @Binding modules
    // which will be mixed-in with concrete @Provides methods via an include.

    @Binds
    @Singleton
    abstract fun bindGitRepository(
        gitRepositoryImpl: GitRepositoriesRepositoryImpl
    ): GitRepositoriesRepository

    @Binds
    @Singleton
    abstract fun bindClickedGitRepoRepository(
        transitionalDataRepositoryImpl: TransitionalDataRepositoryImpl
    ): TransitionalDataRepository

    @Binds
    @Singleton
    abstract fun bindGitCollaboratorsRepository(
        gitCollaboratorsRepositoryImpl: GitCollaboratorsRepositoryImpl
    ): GitCollaboratorsRepository
}

@DisableInstallInCheck
@Module
object NetworkModule {
    const val API_CLIENT = "api_client"

    @Provides
    @Singleton
    @Named(API_CLIENT)
    fun provideApiRetrofit(): Retrofit {
        return RetrofitClient.initializeApiClient(RetrofitClient.API_BASE_URL)
    }

    @Provides
    @Singleton
    fun provideApiService(@Named(API_CLIENT) retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    /**
     * Wrapped Retrofit calls part
     *
     * this is the portion of the DI module where we will provide ApiCallWrappers for all retrofit calls
     * that correspond to named API calls, with their corresponding RequestAdapters and ResponseAdapters
     *
     * NOTE: Pay attention whether the API call should target the AUTH endpoints or API endpoints.
     * See [ApiService] and [AuthService] and more importantly [RetrofitClient.AUTH_BASE_URL] and [RetrofitClient.API_BASE_URL]
     */

    //TODO implement github calls here
    @Provides
    @Named(ApiCalls.SEARCH_REPOSITORIES)
    @Singleton
    fun provideGetRepositoriesCall(
        apiService: ApiService,
        requestAdapter: GetRepositoriesRequestAdapter,
        responseAdapter: GetRepositoriesResponseAdapter
    ): ApiCall<GetRepositoriesRequestParams, GitRepositoriesList> {
        return QueriedApiCall(
            apiService::getRepositories,
            requestAdapter,
            responseAdapter
        )
    }

    @Provides
    @Named(ApiCalls.REPOSITORY_DETAILS)
    @Singleton
    fun provideGetRepositoryDetailsCall(
        apiService: ApiService,
        requestAdapter: GetRepositoryDetailsRequestAdapter,
        responseAdapter: GetRepositoryDetailsResponseAdapter
    ): ApiCall<GetRepositoryDetailsRequestParams, RepositoryDetails> {
        return PathApiCall.TwoPathElementsApiCall(
            apiService::getRepositoryDetails,
            requestAdapter,
            responseAdapter
        )
    }

    @Provides
    @Named(ApiCalls.GET_COLLABORATORS)
    @Singleton
    fun provideGetCollaboratorsFromRepositoryCall(
        apiService: ApiService,
        requestAdapter: GetCollaboratorsRequestAdapter,
        responseAdapter: GetCollaboratorsResponseAdapter
    ): ApiCall<GetCollaboratorsRequestParams, GitCollaboratorList> {
        return LiteralUrlApiCall(
            apiService::getCollaboratorsFromRepository,
            requestAdapter,
            responseAdapter
        )
    }
}

@DisableInstallInCheck
@Module
object RoomModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): Database {
        return Room.databaseBuilder(
            context = context,
            klass = Database::class.java,
            name = DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideGitRepositoryDao(database: Database): GitRepositoryDao {
        return database.gitRepositoryDao()
    }

    @Provides
    @Singleton
    fun provideFavoritesRepositoryDao(database: Database): FavoriteRepositoryDao {
        return database.favoriteRepositoryDao()
    }

    @Provides
    @Singleton
    fun provideCollaboratorsDao(database: Database): CollaboratorsDao {
        return database.collaboratorsDao()
    }
}
