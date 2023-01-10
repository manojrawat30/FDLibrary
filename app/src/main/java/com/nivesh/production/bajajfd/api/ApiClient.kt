package com.nivesh.production.bajajfd.api

import com.nivesh.production.bajajfd.BajajApplication
import com.nivesh.production.bajajfd.interfaces.ApiInterface
import com.nivesh.production.bajajfd.util.Constants.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.KeyStore
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

class ApiClient {
    val context = BajajApplication.appContext

    companion object {
        private val client by lazy {
            //lazy means  we only initialize this here once
            val logging = HttpLoggingInterceptor()
            //loggingInterceptor use for  see making request and for see what responses are
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            //see the body of  response
            //create client for  retrofit

            val trustManagerFactory: TrustManagerFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            trustManagerFactory.init(null as KeyStore?)
            val trustManagers: Array<TrustManager> = trustManagerFactory.getTrustManagers()
            check(!(trustManagers.size != 1 || trustManagers[0] !is X509TrustManager)) {
                "Unexpected default trust managers:" + Arrays.toString(
                    trustManagers
                )
            }
            val trustManager: X509TrustManager = trustManagers[0] as X509TrustManager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, arrayOf(trustManager), null)
            val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .sslSocketFactory(sslSocketFactory, trustManager)
                .retryOnConnectionFailure(true)
                .callTimeout(2, TimeUnit.MINUTES)
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()

            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }
        val getApiClient: ApiInterface by lazy {
            client.create(ApiInterface::class.java)
        }
    }
}




