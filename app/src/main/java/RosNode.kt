import geometry_msgs.msg.dds.Twist
import geometry_msgs.msg.dds.TwistPubSubType
import us.ihmc.ros2.ROS2Node
import us.ihmc.ros2.ROS2NodeBuilder
import us.ihmc.ros2.ROS2Publisher
import us.ihmc.ros2.ROS2QosProfile


class RosNode(private val nodeName: String) {

    private var node: ROS2Node? = null
    private var publisher: ROS2Publisher<Twist>? = null
    private val twistMessage = Twist() // Reusable Twist message object

    @Throws(Exception::class) // It's good practice to declare exceptions network operations might throw
    fun init() {
        // Using an IntraProcessDomain for simplicity if running on the same device.
        node = ROS2NodeBuilder()
            .domainId(112)
            .namespace("/us/ihmc")
            .build(nodeName)
//            .buildRealtime(nodeName)

        // Create a publisher for Twist messages (standard for robot velocity commands)
        // The topic name "/cmd_vel" is a common convention. Adjust if your robot uses a different one.
        publisher = node?.createPublisher(TwistPubSubType(), "/cmd_vel", ROS2QosProfile.RELIABLE())
        // You can also use other QoS profiles like:
        // ROS2QosProfile.BEST_EFFORT()
        // ROS2QosProfile.KEEP_HISTORY(depth)
        // ROS2QosProfile.KEEP_LAST(depth)
    }

    fun publishTwist(linearSpeed: Double, angularSpeed: Double) {
        if (node == null || publisher == null) {
            println("ROS Node or Publisher not initialized!")
            return
        }

        // Set the linear and angular velocities
        // Typically, linear.x is forward/backward, and angular.z is rotation
        twistMessage.linear.x = linearSpeed
        twistMessage.linear.y = 0.0
        twistMessage.linear.z = 0.0

        twistMessage.angular.x = 0.0
        twistMessage.angular.y = 0.0
        twistMessage.angular.z = angularSpeed

        publisher?.publish(twistMessage)
        println("Published Twist: Linear=$linearSpeed, Angular=$angularSpeed")
    }

    fun destroy() {
        publisher?.remove() // Clean up publisher resources
        node?.destroy()
        node = null
        publisher = null
        println("ROS Node destroyed.")
    }
}