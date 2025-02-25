# NSoft Take Home App 
for listing Git repos by using the GitHub APIs

## Project Guidelines

This project uses the "git flow" (https://nvie.com/posts/a-successful-git-branching-model/) and `master` branch will contain only the latest publicly released source.
Everything else will be done on the `develop` branch, with feature branches pulled from it and squash-merged back into it.

Besides using gitflow, it also employes "semantic versioning" (https://semver.org/) with the following schema: `MAJOR.MINOR.PATCH.HOTFIX` or `MAJOR.MINOR.RELEASE.HOTFIX`. 

After a release has been prepared on the e.g. `next-release-1.0.0.0` branch, with everything that has to be done for it and the branch has been merged back into `develop`,
REGULARLY (so, not squash&merge) merge `develop` into `master`, build the build from master (or let CI/CD do it), **annotated** tag `master` as `release-X.Y.Z.Q`.
Then regularly merge `master` back into `develop` to sync them, then carry on with the work as usual.

### The general idea

The short story is, the project relies heavily on DI + Compose + CLEAN in MVVM fashion, where ViewModels are Presenters and Composables are actual Screens.
Screens call presenter's methods when some input happens, followed by the presenter executing some usecase and passes it to one of it's LiveData/StateFlow streams.

Business Logic is decoupled from the UI itself by both the presenter, but moreso by the `UseCase` which is (or rather, **should be**) the only way to perform some
particular usecase and interaction with any of our components (such as `DataSource`s through the `Repository` utilizing it)


The idea is for everything to be decoupled from the UI, including the presentation logic. So that we may retain the presentation logic and change the underlying
UI (`Screen`s) as project requirements change; since the UI for a particular screen is much more likely to change than the actual presentation logic for that screen.

When an "action" was performed on the screen itself, do let the Presenter know what happened, and then let the Presenter (`ViewModel`) decide what information it will
relay back to it's screen via one of it's streams. 

Our most common two streams are the `errorStream` and `navigationStream`, both of which convey necessary information to the `Screen` itself so it can display the error
and convey it to the user after which it needs to call the `errorHandled()` method; or handling it's specific `NavigationEvent` by making the `NavHostController` navigate
in it's own specific way to a particular `NavigationRoute.getRouteName` and calling `doneNavigating()` on the presenter.

The errors to be handled by the screen all come from it's Presenter. It is up to the presentation logic layer to decide how a particular error coming from a `UseCase`
should be handled, or if it should be handled at all. After all, the User doesn't need to know if we received an empty response from the server and bother him
with an alert dialog saying "EMPTY RESPONSE ENCOUNTERED !!!". However, we do use a generic "whoops, something went wrong" in all unlikely error scenarios that *can* happen.

### Code style and guidelines/manual

Roughly speaking, all of our `*NavigationEvent`, `*Error`, or particular `Exception`s coming from `UseCase`s are very friendly towards `when() { ... }` statements.
Try to use `when()` as much as possible. Try to use `.exhaustive` on `when(something) { ... }.exhaustive` blocks as much as possible, because that little extension
**guarantees** that when a new value/element/class is added to whatever the `something` was, we will catch it at compile-time and be forced to handle it accordingly.

Try to abuse the compile-time safety and enforcing of the type system put in place, because everything is strongly typed and there are no `Any` usages anywhere.
In cases where "anything" is literally a common usecase, instead of using `Any?` we are using marker interfaces (or `abstract class`es) instead, such as the the
`RequestParams` or `ResponseDomainData`. 

By the way - all of our `domain` objects that are received via API extend `ResponseDomainData`, and the ones that are created/used locally do not.

Apply `@Inject` to every constructor to automatically add that class to the dependency injection graph **unless** there is reason for it not being in the graph,
or `@Binds`ing it to the interface that it's implementing (in which case it should still have an `@Inject` constructor though).

Manually `@Provide` only what has to be manually constructed and provided, and feel free to check `ApiModule` itself to see how many of those things there are.

Heavily utilize the DI framework and make it do all the heavy-lifting for you, if superclasses (or other utility classes) aren't already doing most of it.


## General use-case how-tos and frequently revisited questions

### Implementing a new API call

The overall idea is to implement a new subclass of `NetworkingUseCase`, with it's corresponding `ApiCall` network resource `@Provide`d in the `AppModule.NetworkModule`
and it's usecase parameters which need to subclass `UseCaseParams`. Then, you will need corresponding `RequestAdapter` which will transform the usecase parametes into
an actual networking request to be sent (has to be a concrete subclass of `RequestParams`), as well as a corresponding `ResponseAdapter` which will transform the received
JSON response into an actual domain class (which has to be a concrete subclass of `ResponseDomainData`), as well as adding a new method to the `ApiService` class, which
is where the relative endpoint is also declared. Since we are targetting two different base URLs (with two different retrofit clients), the concept of "auth" and "api"
endpoints happened; the AUTH one (so far) is in charge of obtaining the token, while the API one is in charge of all other API calls.

After you have the `ApiCall<SomeRequestParams, SomeDomainClass>` provided and injected into your new `SomeNewApiUseCase`, pass the networking resource to the superclass
constructor.

Then, feel free to inject anything else you might need for that usecase into the constructor as well. Then, implement the abstract `provideParams`, `onSuccess`, `onFailure` and `onError`
methods and return what has to be returned from them - the actual "network request" params from `provideParams()`; and concrete usecase results wrapped in `Outcome` -
 because all `NetworkingUseCase`s are limited to returning only them - besides doing what ever else logic you want to perform by the in a success / fail / error scenario.

It is worth mentioning that we enforce certain expectations/considerations on the following terms:
#### success
A successfully received non-empty successful response, typically in the (response) code range of 200-300. In case the response is non-existant (null), this case shortcircuits to `onError()`
#### failure
A succesfully received response, typically outside of the (response) code range of 200-300.
#### error
Anything else; e.g. if some exception is thrown by Retrofit or literally anything else outside of our control. Only two exceptions to this definition are the `ApiException.NoInternetException`
and `ApiException.EmptyResponse` exceptions which we manually and deliberatelly forward to `onError` if/when they happen.

### On `UseCase`s in general

When in doubt, always feel free to consult the source-code of either the `LoginUseCase` or `LoginWithInternetCheckUseCase` as they will demonstrate all necessary ingredients
which you can then follow individually to piece together a working solution.

Do have in mind the following: **try not to** put too much logic into a single usecase. Try to keep things simple, as usecases can be easily composited among themselves.
So having a UseCase that does X, Y, Z, Q, W, E, R, T is not better than having a usecase that does Q, another usecase that does Y, Z, and other usecases to do the rest of the
work, which you can then all compose into the original XYZQWERT usecase alot easier and more testable than if you were to do it all in one usecase. You will also get more 
reusable usecases that way, but do not stress over the fact that if you make usecases that are only used only once in some specific situation, and never again.


## Project Structure


### Data Layer (`data/`):
Responsible for handling data operations, including local (e.g., Room database) and remote (e.g., REST APIs) data sources. This layer includes:

#### `repository/`: Implementations of the repository interfaces.
(See `domain/repository` as well, since these are implementations of those interfaces)
#### `local/`: Local data sources (e.g., database).
#### `remote/`: Remote data sources (e.g., API services), request and response adapters, and networking parameters
#### `model/`: Data models.

### Domain Layer (`domain/`):
Contains the core business logic and entities. This layer includes:

#### `exception/`: Holds exceptions that can happen during API execution or certain `(Networking)UseCase` execution.
Very different from `domain/model/***ErrorState` classes
#### `repository/`: Interfaces for repositories.
(See `data/repository` as well, since these are the interfaces for those implementations)
#### `usecase/`: Use cases (interactors) that encapsulate a specific piece of business logic.
#### `model/`: Domain models.
#### `navigation/`: Navigation models

### Presentation Layer (`presentation/`):
Responsible for handling the UI and user interactions. This layer includes:

#### `ui/`: UI components like activities, fragments, and composable functions.
#### `viewmodel/`: ViewModel classes to manage the UI-related data.

### DI or Dependency-Injection Layer (`di/`): Dependency injection setup, such as Dagger/Hilt modules.

### Util (`util/`): Utility classes and helper/extension functions.


We opted to not do feature-based packaging (packaging per-feature) since none of this code is going to be shared with other projects. 


## Notable classes

Some of the base classes in the repository are the preferred way of doing things as project development moves forward, so that piece
of the not-quite-architecture-but-more-of-a-design-decision development guidelines/manual are in order:

### From `data/`

- `data/remote/calls/ApiCall<NetworkParams : RequestParams, DomainClass : ResponseDomainData>`: An important class, wrapping Retrofit
calls (well, not really) in our class that also carries it's `ResponseAdapter` and `RequestAdapter` with it, the main building block
of every `NetworkingUseCase`. Obviously, the main relevant method is the `getCall()` which returns the `ApiService` (or `AuthService`) method to invoke.
Formerly called ApiWrapperCall (in case some dangling javadoc is left not updated).
This has evolved to prevent `ApiCall` instantiation and offers two subclasses - `NormalApiCall` and `QueriedApiCall`
`NormalApiCall` is for API calls with JSON payload.
`QueriedApiCall` is for API calls that put in query parameters in the endpoint itself.

- `data/remote/calls/ApiCalls`: Just a file containing API "names" for their `ApiCall`s provided via the `AppModule`

- `data/remote/failureextractors/`: Implementations of all FailureExtractors go here, but nothing really notable about them

- `data/remote/params/RequestParams`: A "marker" class, to be applied on every class that is to be used as parameters to Networking calls and requests.
At this moment, `RequestAdapter`s are limited to accepting *only* RequestParams subclasses as their parameters that will be sent to the Request.

### From `domain/`

- `domain/OperationResult<Data>`: A class used for "success with data or failed" cases. It's not quite an `Optional`, but works similarly,
because only successful `OperationResult`s have data obtained via `getResult()`. **MUST** check return value of `isSuccessful()` before
attempting to `getResult()` since trying to call `getResult()` on failed `OperationResult`s will throw an `IllegalStateException`.
Superior to default Kotlin's `Result` since this one doesn't require an Exception to fail.
Created with `OperationResult.successfulResult()` and `OperationResult.unsuccessfulResult()`

- `domain/Outcome<Result, Error>`: A class used to represent the outcome of an operation that can either be successful or failed, carrying 
information about the exception or error that caused it to fail. Somewhat of an ideological extension of OperationResult. 
**MUST** check return value of `isSuccessful()` before attempting to `getResult()` since trying to call `getResult()` on failed 
`Outcome`s will throw an `IllegalStateException`, and same holds true for `getError()` as well - calling it on successful `Outcome`s throws.
Created with `Outcome.successfulOutcome()` and `Outcome.unsuccessfulOutcome()`

- `domain/FailureReasonExtractor<ErrorType>`: Base class for all FailureReasonExtractors to decouple error extraction logic from their usecases
Their implementations reside in `/data/remote/failureextractors/`

- `domain/navigation/NavigationRoutes`: An enum class holding all navigation routes and their names. Sample usage looks like:
```navController.navigate(NavigationRoutes.LOGIN_SCREEN.getRouteName())```

- `domain/navigation/***NavigationEvent`: A bunch of enum classes holding each presenter's "navigation event" to be picked up by it's screen,
and then navigating to a contextual `NavigationRoute`

- `domain/usecase/UseCase<Params: UseCaseParams, UseCaseResult>`: A base class for all of our usecases, which take some usecase parameters, or
`EmptyParams` if the usecase needs no parameters. Subclasses should only override `execute()` and callers should call that, and will be
notified via callback once the usecase completes.

- `domain/usecase/NetworkingUseCase<Params: UseCaseParams, NetworkParams: RequestParams, DomainClass: ResponseDomainData, ExceptionClass: Exception`:
The base class for all usecases whose main purpose is to make a networking call, parse it's response and do something with it's result and return it, 
or extract errors from it and return that to the caller. Fully parametrized, and sits on top of a very important and very relevant `ApiCall`

- `domain/model/ResponseDomainData`: A "marker" class, to be applied on every domain object that is extractable from API responses. At this moment,
`ResponseAdapter`s are limited to extracting *only* ResponseDomainData subclasses so this is good knowing beforehand.

- `domain/model/Signal`: A value-less value. Could be used for converting `void` methods into reactive methods by making them return this when they're done.
Useful for when we need *something* to let us know that something, e.g. some UseCase has finished, but which produces no actual result. Works very decently
in `OperationResult<Signal>` situations.

- `domain/model/***ErrorState`: A bunch of enum classes holding each presenter's "error state", to be displayed on it's screen. 

### From `presentation/`

- `presentation/viewmodel/BaseViewModel<NavigationEventClass, ErrorStateClsas`: A base class for all Presenters containing the most common boilerplate code, 
containing the typically used `navigationStream<NavigationEventClass>` that the screen listens to for navigation events, and `errorStream<ErrorStateClass>` that
the screen listens to for error state events. Contains two important methods, `doneNavigating()` and `errorHandled()`

### From `util/`

- `MyLogger`: The Timber-wrapped Logger around the android's `Log` which is parametrized to log or not, and is the single point of indirection for all logging.
Contains the `MyLoggerOKHttpLogger` logger, which is installed in `OkHttp` as a `HttpLoggingInterceptor.Logger`, and is using the enclosing class for logging.

- `Extensions.kt`: Most notable extension so far is `.exhaustive()` which forces all `when(...)` expressions to evaluate at compile-time and offer 
generating all possible cases. Super useful for e.g. handling API and Outcome related errors. Do use it extensivelly.
