package ru.alexander.twistthetongue

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.view.*
import ru.alexander.twistthetongue.model.LocalInfo


class MainActivity : AppCompatActivity() {

    lateinit var appBarConfiguration: AppBarConfiguration

    var nightMode: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {

        val preference = getSharedPreferences(LocalInfo.PREF_NAME, Context.MODE_PRIVATE)
        nightMode = preference.getBoolean(LocalInfo.PREF_NIGHT_MODE, false)
        setTheme(
            if (nightMode) {
                R.style.DarkTheme
            } else {
                R.style.AppTheme
            }
        )
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration((navController.graph))
        toolbar.setupWithNavController(navController, appBarConfiguration)
        toolbar.nightmodeSwitch.isChecked = nightMode
        toolbar.nightmodeSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            nightMode = isChecked

            preference.edit().putBoolean(LocalInfo.PREF_NIGHT_MODE, nightMode).apply()
            val intent = Intent(this, this::class.java)

            startActivity(intent)
            finish()

        }
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

    override fun getTheme(): Resources.Theme {
        val theme = super.getTheme()
        if (nightMode) {
            theme.applyStyle(R.style.DarkTheme, true)
        }
        return theme
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

}
