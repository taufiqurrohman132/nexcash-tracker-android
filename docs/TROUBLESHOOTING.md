# Troubleshooting - NexCashApplication

## 1. Build Issues
### Hilt/KSP Errors
*   **Issue**: `Hilt_DashboardViewModel_Factory.java` not found or compilation errors in generated code.
*   **Solution**: Run `Build -> Clean Project` then `Build -> Rebuild Project`. Ensure the KSP version matches your Kotlin version.

### Missing Resources
*   **Issue**: `bg_balance_card` or other drawables not recognized.
*   **Solution**: Check `app/src/main/res/drawable/` and ensure naming is strictly lowercase with underscores.

## 2. Runtime Issues
### Database Migrations
*   **Issue**: App crashes after modifying `Entity` classes.
*   **Solution**: During development, you can use `.fallbackToDestructiveMigration()` in `DatabaseModule.kt`. For production, proper Room migrations must be written.

### UI Rendering
*   **Issue**: Compose Preview not loading.
*   **Solution**: Ensure the Preview has a `NexCashTheme` wrapper and all Hilt-injected ViewModels are provided via fake data or `CompositionLocal`.

### Coroutine Cancellation
*   **Issue**: ViewModels not stopping background tasks or UI not reacting to navigation events after a `try-catch` block.
*   **Solution**: When catching `Exception` inside a coroutine, always rethrow `CancellationException`. Swallowing it prevents the coroutine from canceling properly.
    ```kotlin
    try {
        // ...
    } catch (e: CancellationException) {
        throw e // Mandatory
    } catch (e: Exception) {
        // Handle other errors
    }
    ```

## 3. Data & Navigation Issues
### Type-Safe Navigation
*   **Issue**: `IllegalArgumentException` or crash when navigating with custom classes/enums in `Route`.
*   **Solution**: Ensure all custom types passed as arguments in `Route` data classes are annotated with `@Serializable`. Enums must also be annotated if used in routes.

### Enum Mapping (Mappers)
*   **Issue**: Crash with `No enum constant ...` when loading data from the database.
*   **Solution**: Check `toDomain()` mappers. If using `TypeCategory.valueOf(type)`, ensure the string in the database matches the Enum name exactly (check casing). Consider using a safe mapping with a default value.

### DataStore Serialization
*   **Issue**: Errors when saving `List` or complex objects to `PreferencesRepository`.
*   **Solution**: Use `ListSerializer(T.serializer())` when calling `saveList` or `getList`. Ensure `T` is `@Serializable`.

### Unimplemented Features (TODOs)
*   **Issue**: App crashes with `kotlin.NotImplementedError: An operation is not implemented.`
*   **Solution**: Several methods in `RepositoryImpl` and event handlers in `ViewModels` are currently marked with `TODO()`. Check the stack trace to identify the missing implementation.
