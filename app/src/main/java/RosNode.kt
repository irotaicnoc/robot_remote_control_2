import org.ros2.rcljava.RCLJava
import org.ros2.rcljava.node.Node
import org.ros2.rcljava.publisher.Publisher
import geometry_msgs.msg.Twist
import geometry_msgs.msg.Vector3

class RosNode(private val nodeName: kotlin.text.String) {

    private var node: Node? = null
    private var cmdVelPublisher: Publisher<Twist>? = null

    fun init() {
        RCLJava.rclJavaInit()
        node = RCLJava.createNode(nodeName)
        cmdVelPublisher = node?.createPublisher(Twist::class.java, "/cmd_vel")
    }

    fun publishTwist(linearX: java.lang.Double, angularZ: java.lang.Double) {
        val twist = Twist()
        twist.linear = Vector3().setX(linearX)
        twist.angular = Vector3().setZ(angularZ)
        cmdVelPublisher?.publish(twist)
    }

    fun destroy() {
        node?.destroy()
        RCLJava.shutdown()
    }
}