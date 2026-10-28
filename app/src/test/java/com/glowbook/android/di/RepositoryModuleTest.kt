package com.glowbook.android.di

import com.glowbook.android.data.datasource.AppointmentsCancelledDataSource
import com.glowbook.android.data.datasource.AppointmentsCancelledDataSourceImpl
import com.glowbook.android.data.datasource.AppointmentsDataSource
import com.glowbook.android.data.datasource.AppointmentsDataSourceImpl
import com.glowbook.android.data.datasource.AppointmentsHistoryDataSource
import com.glowbook.android.data.datasource.AppointmentsHistoryDataSourceImpl
import com.glowbook.android.data.datasource.AppointmentsUpNextDataSource
import com.glowbook.android.data.datasource.AppointmentsUpNextDataSourceImpl
import com.glowbook.android.data.datasource.CategoriesDataSource
import com.glowbook.android.data.datasource.CategoriesDataSourceImpl
import com.glowbook.android.data.datasource.CreateAppointmentDataSource
import com.glowbook.android.data.datasource.CreateAppointmentDataSourceImpl
import com.glowbook.android.data.datasource.ProfessionalsDataSource
import com.glowbook.android.data.datasource.ProfessionalsDataSourceImpl
import com.glowbook.android.data.datasource.ServicesDataSource
import com.glowbook.android.data.datasource.ServicesDataSourceImpl
import com.glowbook.android.data.remote.CategoriesApiServices
import com.glowbook.android.data.remote.CreateAppointmentApiService
import com.glowbook.android.data.remote.ProfessionalsApiService
import com.glowbook.android.data.remote.ServicesApiService
import com.glowbook.android.data.remote.model.CategoriesResponses
import com.glowbook.android.data.remote.model.CreateAppointmentRequest
import com.glowbook.android.data.remote.model.ProfessionalWithServices
import com.glowbook.android.data.repository.AppointmentsCancelledRepositoryImpl
import com.glowbook.android.data.repository.AppointmentsHistoryRepositoryImpl
import com.glowbook.android.data.repository.AppointmentsUpNextRepositoryImpl
import com.glowbook.android.data.repository.CategoriesRepositoryImpl
import com.glowbook.android.data.repository.CreateAppointmentRepositoryImpl
import com.glowbook.android.data.repository.ProfessionalCalendarRepositoryImpl
import com.glowbook.android.data.repository.ProfessionalServicesRepositoryImpl
import com.glowbook.android.data.repository.ProfessionalsRepositoryImpl
import com.glowbook.android.data.repository.professional
import com.glowbook.android.data.repository.service
import com.glowbook.android.domain.repository.AppointmentsCancelledRepository
import com.glowbook.android.domain.repository.AppointmentsHistoryRepository
import com.glowbook.android.domain.repository.AppointmentsUpNextRepository
import com.glowbook.android.domain.repository.CategoriesRepository
import com.glowbook.android.domain.repository.CreateAppointmentRepository
import com.glowbook.android.domain.repository.ProfessionalCalendarRepository
import com.glowbook.android.domain.repository.ProfessionalServicesRepository
import com.glowbook.android.domain.repository.ProfessionalsRepository
import com.glowbook.android.utils.ApiState
import com.glowbook.android.utils.DefaultDispatcherProvider
import com.glowbook.android.utils.DispatchersProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockkClass
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import javax.inject.Inject
import javax.inject.Singleton

class RepositoryModuleTest {

    private val expectedBindings: Map<Class<*>, Class<*>> = mapOf(
        ProfessionalsRepository::class.java to ProfessionalsRepositoryImpl::class.java,
        CategoriesRepository::class.java to CategoriesRepositoryImpl::class.java,
        AppointmentsUpNextRepository::class.java to AppointmentsUpNextRepositoryImpl::class.java,
        AppointmentsHistoryRepository::class.java to AppointmentsHistoryRepositoryImpl::class.java,
        AppointmentsCancelledRepository::class.java to AppointmentsCancelledRepositoryImpl::class.java,
        ProfessionalServicesRepository::class.java to ProfessionalServicesRepositoryImpl::class.java,
        ProfessionalCalendarRepository::class.java to ProfessionalCalendarRepositoryImpl::class.java,
        CreateAppointmentRepository::class.java to CreateAppointmentRepositoryImpl::class.java,
        ProfessionalsDataSource::class.java to ProfessionalsDataSourceImpl::class.java,
        CategoriesDataSource::class.java to CategoriesDataSourceImpl::class.java,
        AppointmentsUpNextDataSource::class.java to AppointmentsUpNextDataSourceImpl::class.java,
        AppointmentsHistoryDataSource::class.java to AppointmentsHistoryDataSourceImpl::class.java,
        AppointmentsCancelledDataSource::class.java to AppointmentsCancelledDataSourceImpl::class.java,
        ServicesDataSource::class.java to ServicesDataSourceImpl::class.java,
        AppointmentsDataSource::class.java to AppointmentsDataSourceImpl::class.java,
        CreateAppointmentDataSource::class.java to CreateAppointmentDataSourceImpl::class.java,
        DispatchersProvider::class.java to DefaultDispatcherProvider::class.java
    )

    private val bindsMethods: List<Method> =
        RepositoryModule::class.java.declaredMethods.filter { Modifier.isAbstract(it.modifiers) }

    /** interface -> implementation, read from the module's @Binds methods. */
    private val moduleBindings: Map<Class<*>, Class<*>> =
        bindsMethods.associate { it.returnType to it.parameterTypes.single() }

    /** Types NetworkModule/AppModule provide (the boundary below RepositoryModule). */
    private val providedTypes: Set<Class<*>> =
        (NetworkModule::class.java.declaredMethods + AppModule::class.java.declaredMethods)
            .filter { it.isAnnotationPresent(Provides::class.java) }
            .map { it.returnType }
            .toSet()

    // ---- Module contract -------------------------------------------------------------

    @Test
    fun `module is an abstract dagger module`() {
        assertTrue(RepositoryModule::class.java.isAnnotationPresent(Module::class.java))
        assertTrue(Modifier.isAbstract(RepositoryModule::class.java.modifiers))
    }

    @Test
    fun `binds exactly the expected interfaces to their implementations`() {
        assertEquals(expectedBindings, moduleBindings)
    }

    @Test
    fun `every binding is a singleton Binds method with one parameter`() {
        bindsMethods.forEach { method ->
            assertTrue("${method.name} must be @Binds", method.isAnnotationPresent(Binds::class.java))
            assertTrue("${method.name} must be @Singleton", method.isAnnotationPresent(Singleton::class.java))
            assertEquals("${method.name} must take one impl", 1, method.parameterCount)
        }
    }

    @Test
    fun `each interface is bound only once`() {
        val boundTypes = bindsMethods.map { it.returnType }

        assertEquals(boundTypes.size, boundTypes.toSet().size)
    }

    @Test
    fun `every implementation is a concrete class of its interface`() {
        moduleBindings.forEach { (type, impl) ->
            assertTrue("${impl.simpleName} must be an interface impl", type.isInterface)
            assertTrue("${impl.simpleName} must implement ${type.simpleName}", type.isAssignableFrom(impl))
            assertTrue("${impl.simpleName} must be concrete", !impl.isInterface && !Modifier.isAbstract(impl.modifiers))
        }
    }

    @Test
    fun `every implementation has an Inject constructor`() {
        moduleBindings.values.forEach { impl ->
            assertNotNull("${impl.simpleName} needs @Inject constructor", injectConstructorOf(impl))
        }
    }

    @Test
    fun `binding method names match the bound type`() {
        bindsMethods.forEach { method ->
            val type = method.returnType.simpleName
            assertTrue(
                "${method.name} should mention $type",
                method.name == "bind$type" || method.name == "provide$type"
            )
        }
    }

    // ---- Graph resolution --------------------------------------------------------------

    private lateinit var mocks: MutableMap<Class<*>, Any>

    @Before
    fun setUp() {
        mocks = mutableMapOf()
    }

    private fun injectConstructorOf(type: Class<*>) =
        type.constructors.singleOrNull { it.isAnnotationPresent(Inject::class.java) }

    /**
     * Tiny injector: bound types follow RepositoryModule's @Binds, implementations are built
     * through their @Inject constructor, and types provided by NetworkModule/AppModule become mocks.
     */
    private fun resolve(type: Class<*>, path: List<Class<*>> = emptyList()): Any {
        check(type !in path) { "Dependency cycle: ${(path + type).joinToString(" -> ") { it.simpleName }}" }
        moduleBindings[type]?.let { return resolve(it, path + type) }
        if (type in providedTypes) return mocks.getOrPut(type) { mockkClass(type.kotlin, relaxed = true) }
        val constructor = checkNotNull(injectConstructorOf(type)) {
            "No binding for ${type.simpleName} (needed by ${path.lastOrNull()?.simpleName})"
        }
        val args = constructor.parameterTypes.map { resolve(it, path + type) }
        return constructor.newInstance(*args.toTypedArray())
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> instanceOf(type: Class<T>): T = resolve(type, emptyList()) as T

    @Test
    fun `every bound interface resolves to its implementation`() {
        expectedBindings.forEach { (type, impl) ->
            assertEquals(impl, resolve(type, emptyList()).javaClass)
        }
    }

    @Test
    fun `graph only reaches network module for api services`() {
        expectedBindings.keys.forEach { resolve(it, emptyList()) }

        assertTrue(mocks.isNotEmpty())
        mocks.keys.forEach { type ->
            assertTrue("${type.simpleName} should be a remote api service", type.packageName == "com.glowbook.android.data.remote")
        }
    }

    @Test
    fun `categories repository works through the bound graph`() = runTest {
        val repository = instanceOf(CategoriesRepository::class.java)
        val api = mocks.getValue(CategoriesApiServices::class.java) as CategoriesApiServices
        coEvery { api.categories(any(), any()) } returns Response.success(listOf(CategoriesResponses(1, "Cabelo")))

        val result = repository.fetchCategories().toList().single()

        assertEquals(ApiState.Success(listOf(CategoriesResponses(1, "Cabelo"))), result)
    }

    @Test
    fun `professional services repository combines two bound data sources`() = runTest {
        val repository = instanceOf(ProfessionalServicesRepository::class.java)
        val professionalsApi = mocks.getValue(ProfessionalsApiService::class.java) as ProfessionalsApiService
        val servicesApi = mocks.getValue(ServicesApiService::class.java) as ServicesApiService
        coEvery { professionalsApi.profissionalById(any(), "eq.1") } returns Response.success(listOf(professional(1)))
        coEvery { servicesApi.services(any(), any(), any(), any()) } returns Response.success(listOf(service(1)))

        val result = repository.fetchProfessionalServices(1).toList().single()

        assertEquals(ApiState.Success(ProfessionalWithServices(professional(1), listOf(service(1)))), result)
    }

    @Test
    fun `create appointment repository reaches create appointment api`() = runTest {
        val repository = instanceOf(CreateAppointmentRepository::class.java)
        val api = mocks.getValue(CreateAppointmentApiService::class.java) as CreateAppointmentApiService
        val request = CreateAppointmentRequest("2026-10-30", "14:00:00", 1, listOf(1, 2))
        coEvery { api.createAppointment(request) } returns Response.success(42L)

        val result = repository.createAppointment(request).toList().single()

        assertEquals(ApiState.Success(42L), result)
        coVerify(exactly = 1) { api.createAppointment(request) }
    }

    @Test
    fun `dispatchers provider binding gives real io dispatcher`() {
        val provider = instanceOf(DispatchersProvider::class.java)

        assertEquals(kotlinx.coroutines.Dispatchers.IO, provider.io())
    }
}
