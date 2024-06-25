package com.tecknobit.neutron.helpers.local.storage

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.tecknobit.apimanager.formatters.JsonHelper
import com.tecknobit.neutron.R
import com.tecknobit.neutroncore.helpers.local.LNeutronController.Companion.LOCAL_DATABASE_NAME
import com.tecknobit.neutroncore.helpers.local.LNeutronController.Companion.getFailedResponse
import com.tecknobit.neutroncore.helpers.local.LNeutronController.Companion.getSuccessfulResponse
import com.tecknobit.neutroncore.helpers.local.LRevenuesController
import com.tecknobit.neutroncore.helpers.local.LUserController
import com.tecknobit.neutroncore.helpers.local.LUserController.Companion.CHANGE_USER_INFO_QUERY
import com.tecknobit.neutroncore.helpers.local.LUserController.Companion.CREATE_USERS_TABLE
import com.tecknobit.neutroncore.helpers.local.LUserController.Companion.DELETE_USER
import com.tecknobit.neutroncore.helpers.local.LUserController.Companion.SIGN_UP_QUERY
import com.tecknobit.neutroncore.records.User.CURRENCY_KEY
import com.tecknobit.neutroncore.records.User.DEFAULT_PROFILE_PIC
import com.tecknobit.neutroncore.records.User.IDENTIFIER_KEY
import com.tecknobit.neutroncore.records.User.LANGUAGE_KEY
import com.tecknobit.neutroncore.records.User.NAME_KEY
import com.tecknobit.neutroncore.records.User.PROFILE_PIC_KEY
import com.tecknobit.neutroncore.records.User.SURNAME_KEY
import com.tecknobit.neutroncore.records.User.TOKEN_KEY
import com.tecknobit.neutroncore.records.User.USERS_KEY
import com.tecknobit.neutroncore.records.revenues.RevenueLabel
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class AndroidLocalServer(
    context: Context,
    private var userId: String?,
    private var userToken: String?
) : SQLiteOpenHelper(context, LOCAL_DATABASE_NAME, null, LOCAL_DATABASE_VERSION),
    LUserController,
    LRevenuesController {

    companion object {

        private const val LOCAL_DATABASE_VERSION = 1

    }

    private val operationExecutedSuccessfully =
        context.getString(R.string.operation_executed_successfully)

    private val notAuthorizedMessage = context.getString(R.string.not_authorized_or_wrong_details)

    private val wrongProcedureMessage = context.getString(R.string.wrong_procedure)

    override fun onConfigure(db: SQLiteDatabase) {
        db.setForeignKeyConstraintsEnabled(true)
        super.onConfigure(db)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_USERS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $USERS_KEY")
        onCreate(db)
    }

    override fun signUp(
        name: String,
        surname: String,
        email: String,
        password: String,
        language: String,
        onSuccess: (JsonHelper) -> Unit,
        onFailure: (JsonHelper) -> Unit
    ) {
        userId = generateIdentifier()
        userToken = generateIdentifier()
        try {
            writableDatabase.use { database ->
                database.execSQL(
                    SIGN_UP_QUERY,
                    arrayOf(
                        userId,
                        email,
                        language,
                        name,
                        hash(password),
                        DEFAULT_PROFILE_PIC,
                        surname,
                        userToken
                    )
                )
                val response = JSONObject()
                response.put(IDENTIFIER_KEY, userId)
                response.put(TOKEN_KEY, userToken)
                response.put(PROFILE_PIC_KEY, DEFAULT_PROFILE_PIC)
                onSuccess.invoke(getSuccessfulResponse(response))
            }
        } catch (e: Exception) {
            onFailure.invoke(getFailedResponse(wrongProcedureMessage))
        }
    }

    override fun signIn(
        email: String,
        password: String,
        onSuccess: (JsonHelper) -> Unit,
        onFailure: (JsonHelper) -> Unit
    ) {
        try {
            readableDatabase.use { database ->
                val cursor = database.rawQuery(
                    LUserController.SIGN_IN_QUERY,
                    arrayOf(
                        email,
                        hash(password)
                    )
                )
                if (cursor.moveToNext()) {
                    userId = cursor.getString(cursor.getColumnIndexOrThrow(IDENTIFIER_KEY))
                    userToken = cursor.getString(cursor.getColumnIndexOrThrow(TOKEN_KEY))
                    val response = JSONObject()
                    response.put(IDENTIFIER_KEY, userId)
                    response.put(TOKEN_KEY, userToken)
                    response.put(
                        PROFILE_PIC_KEY,
                        cursor.getString(cursor.getColumnIndexOrThrow(PROFILE_PIC_KEY))
                    )
                    response.put(NAME_KEY, cursor.getString(cursor.getColumnIndexOrThrow(NAME_KEY)))
                    response.put(
                        SURNAME_KEY,
                        cursor.getString(cursor.getColumnIndexOrThrow(SURNAME_KEY))
                    )
                    response.put(
                        LANGUAGE_KEY,
                        cursor.getString(cursor.getColumnIndexOrThrow(LANGUAGE_KEY))
                    )
                    response.put(
                        CURRENCY_KEY,
                        cursor.getString(cursor.getColumnIndexOrThrow(CURRENCY_KEY))
                    )
                    cursor.close()
                    onSuccess.invoke(getSuccessfulResponse(response))
                } else
                    onFailure.invoke(getFailedResponse(notAuthorizedMessage))
            }
        } catch (e: Exception) {
            onFailure.invoke(getFailedResponse(wrongProcedureMessage))
        }
    }

    override fun changeUserInfo(
        key: String,
        newInfo: String,
        onSuccess: (JsonHelper) -> Unit,
        onFailure: (JsonHelper) -> Unit
    ) {
        try {
            writableDatabase.use { database ->
                println(CHANGE_USER_INFO_QUERY.format(key))
                database.execSQL(
                    CHANGE_USER_INFO_QUERY.format(key),
                    arrayOf(
                        newInfo,
                        userId,
                        userToken
                    )
                )
                onSuccess.invoke(getSuccessfulResponse(operationExecutedSuccessfully))
            }
        } catch (e: Exception) {
            onFailure.invoke(getFailedResponse(wrongProcedureMessage))
        }
    }

    override fun deleteAccount(
        onSuccess: (JsonHelper) -> Unit,
        onFailure: (JsonHelper) -> Unit
    ) {
        try {
            writableDatabase.use { database ->
                database.execSQL(
                    DELETE_USER,
                    arrayOf(
                        userId,
                        userToken
                    )
                )
                onSuccess.invoke(getSuccessfulResponse(operationExecutedSuccessfully))
            }
        } catch (e: Exception) {
            onFailure.invoke(getFailedResponse(wrongProcedureMessage))
        }
    }

    override fun list(
        userId: String,
        userToken: String
    ): JSONArray? {
        TODO("Not yet implemented")
    }

    override fun createProjectRevenue(
        title: String,
        value: Double,
        revenueDate: Long
    ) {
        TODO("Not yet implemented")
    }

    override fun createGeneralRevenue(
        title: String,
        description: String,
        value: Double,
        revenueDate: Long,
        labels: List<RevenueLabel?>
    ) {
        TODO("Not yet implemented")
    }

    override fun getProjectRevenue(
        revenueId: String
    ): JSONObject {
        TODO("Not yet implemented")
    }

    override fun addTicketToProjectRevenue(
        revenueId: String,
        ticketTitle: String,
        ticketValue: Double,
        ticketDescription: String,
        openingDate: Long,
        closingDate: Long
    ) {
        TODO("Not yet implemented")
    }

    override fun closeProjectRevenueTicket(
        revenueId: String,
        ticketId: String
    ) {
        TODO("Not yet implemented")
    }

    override fun deleteProjectRevenueTicket(
        revenueId: String,
        ticketId: String
    ) {
        TODO("Not yet implemented")
    }

    override fun deleteRevenue(
        table: String,
        revenueId: String
    ) {
        TODO("Not yet implemented")
    }

    private fun generateIdentifier(): String {
        return UUID.randomUUID().toString().replace("-", "")
    }

}
