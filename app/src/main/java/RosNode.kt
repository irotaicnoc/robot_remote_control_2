import us.ihmc.pubsub.DomainFactory;
import us.ihmc.ros2.ROS2Node;
import us.ihmc.ros2.ROS2Publisher;
import geometry_msgs.msg.Twist; // Assuming Twist is compatible or you have the IHMC equivalent
import geometry_msgs.msg.Vector3; // Assuming Vector3 is compatible

class RosNode(private val nodeName: String) {

    private var ros2Node: ROS2Node? = null
    private var cmdVelPublisher: ROS2Publisher<Twist>? = null
    // IHMC uses a DomainFactory for network partitioning, often with REALTIME as a common choice.
    private val domainFactory = DomainFactory.PubSubImplementation.FAST_RTPS // Or another implementation like INTRA_PROCESS

    fun init() {
        try {
            // 1. Create a ROS2Node
            // The IHMC ROS2Node typically takes a DomainFactory and a name.
            ros2Node = ROS2Node(domainFactory, nodeName)

            // 2. Create a Publisher
            // The createPublisher method will need the message type and topic name.
            // You'll need to ensure that the Twist.class is the correct message type
            // class expected by the IHMC library. It might have its own generator
            // or a specific way to handle standard messages.
            cmdVelPublisher = ros2Node?.createPublisher(Twist::class.java, "/cmd_vel")

        } catch (e: Exception) {
            // Handle initialization errors (e.g., network issues, DDS configuration problems)
            e.printStackTrace()
            // Consider more robust error handling/logging
        }
    }

    fun publishTwist(linearX: Double, angularZ: Double) {
        if (ros2Node == null || cmdVelPublisher == null) {
            println("ROS2 Node or Publisher not initialized!")
            return
        }

        val twist = Twist() // Or the IHMC equivalent message type
        // Setting message fields might be slightly different depending on the IHMC message classes
        val linear = Vector3()
        linear.x = linearX // IHMC message setters might be different (e.g., setX())
        twist.linear = linear

        val angular = Vector3()
        angular.z = angularZ // IHMC message setters might be different (e.g., setZ())
        twist.angular = angular

        cmdVelPublisher?.publish(twist)
    }

    fun destroy() {
        // IHMC's ROS2Node might have a destroy() or close() method.
        // Consult the library's documentation.
        ros2Node?.destroy()
        // There might not be a global shutdown like RCLJava.shutdown() with IHMC.
        // The destruction of the node might be sufficient.
    }
}