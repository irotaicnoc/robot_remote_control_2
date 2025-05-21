import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var rosNode: RosNode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        rosNode = RosNode("android_controller")
        lifecycleScope.launch { // Use lifecycleScope for coroutines
            rosNode.init()
        }

        setContent {
            RobotControlScreen(rosNode)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        rosNode.destroy()
    }
}

@Composable
fun RobotControlScreen(rosNode: RosNode) {
    var linearSpeed by remember { mutableStateOf(0.0) }
    var angularSpeed by remember { mutableStateOf(0.0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Linear Speed: ${String.format("%.2f", linearSpeed)} m/s")
        Slider(
            value = linearSpeed.toFloat(),
            onValueChange = { linearSpeed = it.toDouble() },
            valueRange = -1.0f..1.0f
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Angular Speed: ${String.format("%.2f", angularSpeed)} rad/s")
        Slider(
            value = angularSpeed.toFloat(),
            onValueChange = { angularSpeed = it.toDouble() },
            valueRange = -1.0f..1.0f
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = { rosNode.publishTwist(linearSpeed, angularSpeed) }) {
            Text("Send Command")
        }
    }
}