package ru.alexander.twistthetongue

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.auth.oauth2.GoogleCredentials
import kotlinx.android.synthetic.main.toolbar.*
import ru.alexander.twistthetongue.model.LocalInfo
import ru.alexander.twistthetongue.model.LocalInfo.PREF_ACCESS_TOKEN_EXPIRATION_TIME
import ru.alexander.twistthetongue.model.LocalInfo.PREF_ACCESS_TOKEN_VALUE
import ru.alexander.twistthetongue.ui.main.MainFragment
import ru.alexander.twistthetongue.viewmodels.MainViewModel


class MainActivity : AppCompatActivity() {

    lateinit var appBarConfiguration : AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration((navController.graph))
        toolbar.setupWithNavController(navController, appBarConfiguration)
        //SpeechKit.getInstance().init(applicationContext, "9e82427f-33b9-48be-bdee-84ff91fb7134")

//        val inputStream = resources.openRawResource(R.raw.credential)
//        val credentials = GoogleCredentials.fromStream(inputStream).createScoped(arrayListOf("https://www.googleapis.com/auth/cloud-platform"))
//        val token = credentials.refreshAccessToken()
//        val prefs = getSharedPreferences(applicationContext.packageName, Context.MODE_PRIVATE)
//        prefs.edit()
//            .putString(PREF_ACCESS_TOKEN_VALUE, token.tokenValue)
//            .putLong(PREF_ACCESS_TOKEN_EXPIRATION_TIME,token.expirationTime.time)
//            .apply()
//        LocalInfo.token = token


    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

}
