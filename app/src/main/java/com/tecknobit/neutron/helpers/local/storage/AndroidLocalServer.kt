package com.tecknobit.neutron.helpers.local.storage

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.tecknobit.apimanager.formatters.JsonHelper
import com.tecknobit.neutroncore.helpers.l.LUserController
import com.tecknobit.neutroncore.helpers.l.LUserController.Companion.CREATE_USERS_TABLE
import com.tecknobit.neutroncore.helpers.l.LUserController.Companion.DEFAULT_FAILURE_RESPONSE
import com.tecknobit.neutroncore.helpers.l.LUserController.Companion.SIGN_UP_QUERY
import com.tecknobit.neutroncore.helpers.l.LUserController.Companion.getSuccessfulResponse
import com.tecknobit.neutroncore.l.LNeutronServer.LOCAL_DATABASE_NAME
import com.tecknobit.neutroncore.records.User
import com.tecknobit.neutroncore.records.User.DEFAULT_PROFILE_PIC
import com.tecknobit.neutroncore.records.User.USERS_KEY
import org.json.JSONObject
import java.io.File
import java.util.UUID

class AndroidLocalServer(
    context: Context?,
    private var userId: String?,
    private var userToken: String?
) : SQLiteOpenHelper(context, LOCAL_DATABASE_NAME, null, LOCAL_DATABASE_VERSION), LUserController {

    companion object {
        private const val LOCAL_DATABASE_VERSION = 1
    }

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
            writableDatabase.execSQL(
                SIGN_UP_QUERY,
                arrayOf(
                    userId,
                    email.lowercase(),
                    language,
                    name,
                    password,
                    DEFAULT_PROFILE_PIC,
                    surname,
                    userToken
                )
            )
            val response = JSONObject()
            response.put(User.IDENTIFIER_KEY, userId)
            response.put(User.TOKEN_KEY, userToken)
            response.put(User.PROFILE_PIC_KEY, DEFAULT_PROFILE_PIC)
            onSuccess.invoke(getSuccessfulResponse(response))
        } catch (e: Exception) {
            onFailure.invoke(DEFAULT_FAILURE_RESPONSE)
        }
    }

    override fun signIn(
        email: String,
        password: String,
        onSuccess: (JsonHelper) -> Unit,
        onFailure: (JsonHelper) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun changeProfilePic(
        newProfilePic: File?,
        onSuccess: (JsonHelper) -> Unit,
        onFailure: (JsonHelper) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun changeUserInfo(
        key: String,
        newInfo: String,
        onSuccess: (JsonHelper) -> Unit,
        onFailure: (JsonHelper) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun deleteAccount(onSuccess: (JsonHelper) -> Unit, onFailure: (JsonHelper) -> Unit) {
        TODO("Not yet implemented")
    }

    private fun generateIdentifier(): String {
        return UUID.randomUUID().toString().replace("-", "")
    }

}
