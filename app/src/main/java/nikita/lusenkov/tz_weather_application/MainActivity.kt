package nikita.lusenkov.tz_weather_application

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import dagger.hilt.android.AndroidEntryPoint
import nikita.lusenkov.features.main.ui.MainScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    MainScreen()
                } else {
                    // Handle older versions if necessary
                }
            }
        }
    }
}