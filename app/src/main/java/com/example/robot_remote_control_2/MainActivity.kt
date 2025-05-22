package com.example.robot_remote_control_2

import RosNode
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    private lateinit var rosNode: RosNode // Your adjusted RosNode class

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        rosNode = RosNode("android_ihmc_controller")

        // Initialize ROS Node in a background thread
        lifecycleScope.launch {
            withContext(Dispatchers.IO) { // Perform network operations off the main thread
                rosNode.init()
            }
        }

        setContent {
            RobotControlScreen(rosNode) // Your existing Composable
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Destroy ROS Node, potentially in a background thread if it's blocking
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                rosNode.destroy()
            }
        }
    }
}

@Composable
fun RobotControlScreen(rosNode: RosNode) { // This Composable should remain the same
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

        Button(onClick = {
            // Publishing can also be moved to a background thread if it's blocking
            // For simple commands, it might be okay on the main thread if quick,
            // but for robustness, consider:
            // CoroutineScope(Dispatchers.IO).launch {
            //     rosNode.publishTwist(linearSpeed, angularSpeed)
            // }
            rosNode.publishTwist(linearSpeed, angularSpeed)
        }) {
            Text("Send Command")
        }
    }
}