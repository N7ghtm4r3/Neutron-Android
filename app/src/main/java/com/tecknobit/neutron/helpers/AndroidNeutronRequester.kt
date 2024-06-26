package com.tecknobit.neutron.helpers

import android.annotation.SuppressLint
import com.tecknobit.apimanager.annotations.RequestPath
import com.tecknobit.apimanager.apis.APIRequest.RequestMethod.POST
import com.tecknobit.apimanager.apis.sockets.SocketManager.StandardResponseCode.GENERIC_RESPONSE
import com.tecknobit.equinox.Requester
import com.tecknobit.neutroncore.helpers.Endpoints
import com.tecknobit.neutroncore.helpers.NeutronRequester
import com.tecknobit.neutroncore.records.User.PROFILE_PIC_KEY
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import okhttp3.Headers.Companion.toHeaders
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * The **AndroidNeutronRequester** class is useful to communicate with the Neutron's backend
 *
 * @param host: the host where is running the Neutron's backend
 * @param userId: the user identifier
 * @param userToken: the user token
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see Requester
 * @see NeutronRequester
 */
class AndroidNeutronRequester(
    host: String,
    userId: String? = null,
    userToken: String? = null
) : NeutronRequester(
    host = host,
    userId = userId,
    userToken = userToken
) {

    /**
     * Function to execute the request to change the profile pic of the user
     *
     * @param profilePic: the profile pic chosen by the user to set as the new profile pic
     *
     * @return the result of the request as [JSONObject]
     */
    @RequestPath(path = "/api/v1/users/{id}/changeProfilePic", method = POST)
    override fun changeProfilePic(profilePic: File): JSONObject {
        val body = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart(
                PROFILE_PIC_KEY,
                profilePic.name,
                profilePic.readBytes().toRequestBody("*/*".toMediaType())
            )
            .build()
        return execMultipartRequest(
            endpoint = assembleUsersEndpointPath(Endpoints.CHANGE_PROFILE_PIC_ENDPOINT),
            body = body
        )
    }

    /**
     * Function to exec a multipart body  request
     *
     * @param endpoint: the endpoint path of the url
     * @param body: the body payload of the request
     *
     * @return the result of the request as [JSONObject]
     */
    private fun execMultipartRequest(
        endpoint: String,
        body: MultipartBody
    ): JSONObject {
        val mHeaders = mutableMapOf<String, String>()
        headers.headersKeys.forEach { headerKey ->
            mHeaders[headerKey] = headers.getHeader(headerKey)
        }
        val request: Request = Request.Builder()
            .headers(mHeaders.toHeaders())
            .url("$host$endpoint")
            .post(body)
            .build()
        val client = validateSelfSignedCertificate(OkHttpClient())
        var response: JSONObject? = null
        runBlocking {
            try {
                async {
                    response = try {
                        client.newCall(request).execute().body?.string()?.let { JSONObject(it) }
                    } catch (e: IOException) {
                        JSONObject(connectionErrorMessage())
                    }
                }.await()
            } catch (e: Exception) {
                response = JSONObject(connectionErrorMessage())
            }
        }
        return response!!
    }

    /**
     * Function to set the [RESPONSE_STATUS_KEY] to send when an error during the connection occurred
     *
     * No-any params required
     *
     * @return the error message as [String]
     */
    private fun connectionErrorMessage(): String {
        return JSONObject()
            .put(RESPONSE_STATUS_KEY, GENERIC_RESPONSE.name)
            .put(RESPONSE_MESSAGE_KEY, "Server is temporarily unavailable")
            .toString()
    }

    /**
     * Method to validate a self-signed SLL certificate and bypass the checks of its validity<br></br>
     * No-any params required
     *
     * @apiNote this method disable all checks on the SLL certificate validity, so is recommended to use for test only or
     * in a private distribution on own infrastructure
     */
    private fun validateSelfSignedCertificate(
        okHttpClient: OkHttpClient
    ): OkHttpClient {
        if (mustValidateCertificates) {
            val trustAllCerts = arrayOf<TrustManager>(@SuppressLint("CustomX509TrustManager")
            object : X509TrustManager {
                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }

                @SuppressLint("TrustAllX509TrustManager")
                override fun checkClientTrusted(
                    certs: Array<X509Certificate>,
                    authType: String
                ) {
                }

                @SuppressLint("TrustAllX509TrustManager")
                override fun checkServerTrusted(
                    certs: Array<X509Certificate>,
                    authType: String
                ) {
                }
            })
            val builder = okHttpClient.newBuilder()
            try {
                val sslContext = SSLContext.getInstance("TLS")
                sslContext.init(null, trustAllCerts, SecureRandom())
                builder.sslSocketFactory(
                    sslContext.socketFactory,
                    trustAllCerts[0] as X509TrustManager
                )
                builder.hostnameVerifier { _: String?, _: SSLSession? -> true }
                return builder.build()
            } catch (ignored: java.lang.Exception) {
            }
        }
        return OkHttpClient()
    }

}