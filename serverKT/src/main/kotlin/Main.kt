import io.javalin.Javalin
import io.javalin.websocket.WsConnectContext
import io.javalin.websocket.WsContext
import io.javalin.websocket.WsMessageContext
import java.io.File
import java.sql.*
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread

class Container<T>(var obj: T)
fun<T> T.toContainer() = Container<T>(this)

class window
{
    companion object
    {
        var innerWidth = 0
        var innerHeight = 0
    }
}
class GraphNode
{
    var radius : Double = 0.0
    var x: Double = 0.0
    var y: Double = 0.0
    var isActivated: Boolean = false
    constructor(x: Double, y: Double, radius: Double)
    {
        this.x = x
        this.y = y
        this.radius = radius
    }
    constructor(otherNode: GraphNode)
    {
        x = otherNode.x
        y = otherNode.y
    }
}

class OrientedGraph
{
    var normalRadius: Double = 0.0
    var selectedRadius: Double = 0.0

    var nodes: MutableList<GraphNode> = mutableListOf()
    var links: MutableMap<GraphNode, MutableList<GraphNode>> = mutableMapOf()

    var selectedNode: GraphNode? = null
    var scoreForWin : Int = 0
    var bExtractedVictory = false
    var bIsMoveing : AtomicBoolean = AtomicBoolean(false)

    public override fun toString(): String {
        return "".toContainer().apply {
            this.obj += "graphData\n"
            this.obj += normalRadius.toString() + "\n"
            this.obj += selectedRadius.toString() + "\n"
            this.obj += nodes.size.toString() + "\n"
            nodes.forEach { this.obj += it.x.toString() + " " + it.y.toString() + " " + (if (it.isActivated) "1" else "0") + "\n" }
            0.toContainer().run countLinks@{ "".toContainer().apply linkStrs@{ links.forEach { t, u -> u.forEach{link -> this@countLinks.obj++; this.obj += nodes.indexOfFirst { it == t }!!.toString() + " " + nodes.indexOfFirst { it == link }!!.toString() + "\n"}}}.let{this@apply.obj += this.obj.toString() + "\n" + it.obj}}
        }.obj
    }

    public fun checkForVictory(): Boolean {
        return try {
            links.forEach { vA, lsA ->  lsA.forEach{lA -> links.forEach { vB, lsB -> lsB.forEach{lB -> if ((vA to lA) intersect (vB to lB)) throw Exception("false")} }}}
            true
        } catch(e: Exception) {
            false
        }
    }

    public constructor(normalRaduis: Double, selectedRadius: Double, scoreForWin: Int)
    {
        this.normalRadius = normalRaduis
        this.selectedRadius = selectedRadius
        this.scoreForWin = scoreForWin
    }
}

class Game
{
    companion object
    {
        public var GameSessions: MutableMap<String, OrientedGraph> = mutableMapOf()
        var UserBoundry: MutableMap<String, User> = mutableMapOf()

        fun squareCreatingPattern(graph: OrientedGraph, dotsAmount: Int, currentIndex: Int, dotRadius: Int)
        {
            /*var dim = +(Math.sqrt(dotsAmount)).toFixed(0);
            let xI = currentIndex % dim;
            let yI = +Math.trunc(currentIndex / dim).toFixed(0);
            var newNode = new GraphNode(100 + xI * (window.innerWidth - 400) / (dim - 1) + yI * (200 / (dim - 1)) + ((yI % 2) * 100) - 50, 100 + yI * (window.innerHeight - 400) / (dim - 1) + xI * (200 / (dim - 1)) + ((xI % 2) * 100) - 50);
            return newNode;*/
        }

        fun randomCreatingPathKeepingDistance(graph: OrientedGraph, dotsAmount: Int, currentDotIndex: Int, dotRadius: Double): GraphNode
        {
            finding@ while (true)
            {
                return GraphNode(Math.random() * (window.innerWidth - 200) + 100, Math.random() * (window.innerHeight - 200) + 100, dotRadius).run {if (true.toContainer().apply{ graph.nodes.forEachIndexed(fun (ind: Int, node: GraphNode) : Unit{ if ((this@run distance node) < dotRadius * 3){ this.obj = false }})}.obj) this else null} ?: continue
            }
        }

        fun createGame(scoreForWin: Int, dotsAmount: Int, linksAmount: Int, dotRadius: Double, creativePattern: (graph: OrientedGraph, dotAmount: Int, currentDotIndex: Int, dotRadius: Double) -> GraphNode) : OrientedGraph
        {
            return OrientedGraph(dotRadius, dotRadius * 1.2, scoreForWin).apply graph@{
                repeat(dotsAmount, fun (ind: Int) : Unit{
                    var newNode = creativePattern(this, dotsAmount, ind, dotRadius)
                    this.nodes += newNode
                })
                (0 until linksAmount).forEach {
                    while (true) {
                        try
                        {
                            ((Math.random() * 1000).toInt() % this@graph.nodes.size to (Math.random() * 1000).toInt() % this@graph.nodes.size).apply indexes@{
                                if (this@indexes.second == this@indexes.first) throw Exception("")
                                this@graph.links.forEach{node, links -> links.forEach { link -> if ((node == this@graph.nodes[this@indexes.first.toInt()]) && (link == this@graph.nodes[this@indexes.second.toInt()])) throw Exception("already exist")}}
                                this@graph.links.forEach{node, links -> links.forEach { link -> if (node to link intersect (this@graph.nodes[this@indexes.first.toInt()] to this@graph.nodes[this@indexes.second.toInt()])) throw Exception("")}}
                            }.apply {
                                (this@graph.links).putIfAbsent(this@graph.nodes[this.first.toInt()], mutableListOf(this@graph.nodes[this.second.toInt()])
                                )?.let {
                                    this@graph.links.get(this@graph.nodes[this.first.toInt()])!!.add(this@graph.nodes[this.second.toInt()])
                                }
                                (this@graph.links).putIfAbsent(this@graph.nodes[this.second.toInt()], mutableListOf(this@graph.nodes[this.first.toInt()])
                                )?.let {
                                    this@graph.links.get(this@graph.nodes[this.second.toInt()])!!.add(this@graph.nodes[this.first.toInt()])
                                }
                            }
                            break
                        }
                        catch(e: java.lang.Exception)
                        {

                        }
                    }
                }
                repeat(4){

                    try {
                        ((Math.random() * 1000).toInt() % this@graph.nodes.size to (Math.random() * 1000).toInt() % this@graph.nodes.size).apply indexes@{
                        this@graph.nodes[this.first].x = this@graph.nodes[this.second].x.also{this@graph.nodes[this.second].x = this@graph.nodes[this.first].x}
                        this@graph.nodes[this.first].y = this@graph.nodes[this.second].y.also{this@graph.nodes[this.second].y = this@graph.nodes[this.first].y}
                    }
                    } catch (e: Exception) {
                        println(e.message + " " + this@graph.nodes.size)
                    }
                }
            }
        }
    }
}

infix fun GraphNode.distance(b: GraphNode): Double {
    return Math.sqrt(Math.pow((this.x - b.x).toDouble(), 2.0) + Math.pow((this.y - b.y).toDouble(), 2.0));
}


infix fun Pair<GraphNode, GraphNode>.intersect (b: Pair<GraphNode, GraphNode>) : Boolean
{
    if ((this.first.x == b.first.x && this.first.y == b.first.y) || (this.first.x == b.second.x && this.first.y == b.second.y) || (this.second.x == b.first.x && this.second.y == b.first.y) || (this.second.x == b.second.x && this.second.y == b.second.y)) {
        return false
    }
    val A = (this.second.x - this.first.x)
    val B = (this.second.y - this.first.y)
    val C = (b.second.x - b.first.x)
    val D = (b.second.y - b.first.y)
    val c1 = B * this.first.x - A * this.first.y;
    val c2 = D * b.first.x - C * b.first.y;

    var y = (c2 - c1 * D / B) / (A * D / B - C)
    var x = (c2 + C * y) / D
    return GraphNode(x, y, 0.0).run ( fun GraphNode.() : Boolean{
        return (((this@intersect.first distance this) < (this@intersect.first distance this@intersect.second)) && ((this@intersect.second distance this) < (this@intersect.first distance this@intersect.second)) && ((b.first distance this) < (b.first distance b.second)) && (((b.second distance this) < (b.first distance b.second))))
    } )

}

class Server
{
    companion object Companion
    {
        var Clients: MutableList<String> = mutableListOf()
        var Commands : MutableMap<List<String>, List<(WsMessageContext, List<String>) -> Unit>> = mutableMapOf()
        public fun CreateServer() : Unit
        {
            Commands += listOf("clearingMessageThread") to listOf (
                { client, args ->
                    thread {
                        client.send("food")
                    }
                })
            Commands += listOf("getFileAsStringFromPath") to listOf (
                { client, args ->
                    client.send(
                        File(
                            "src/main/resources/"+ args[0]
                        ).readLines().joinToString(separator = "\n")
                    )
                })
            Commands += listOf("checkUserRegistration") to listOf (
                { client, args ->
                    client.send(
                        Database.GetFromDb(User(args[0]))?.let{"1"} ?: "0"
                    )
                })
            Commands += listOf("logOut") to listOf(
                { client, args ->
                    Game.UserBoundry.remove(client.sessionId)
                }
            )
            Commands += listOf("registerUser") to listOf (
                { client, args ->
                    client.send(Database.GetFromDb(User(args[0]))?.let{"0"} ?: Database.AddToDb(User(args[0], args[1])).also{
                        Game.UserBoundry[client.sessionId] = User(args[0], args[1])
                    }.let{"1"})
                })
            Commands += listOf("logInUser") to listOf (
                {client, args ->
                    client.send(Database.GetFromDb(User(args[0]))?.run{if (this.password == args[1]) listOf(this.name, this.password, this.score, this.status).also{
                        Game.UserBoundry[client.sessionId] = this
                    }.joinToString(" ") else "0"} ?: "0")
                })
            Commands += listOf("createGame") to listOf (
                {client, args ->
                    window.innerHeight = args[1].toInt()
                    window.innerWidth = args[2].toInt()
                    client.send(Game.createGame(
                        when(args[0]) {
                            "easy" -> 30 + (Math.random() * 1000).toInt() % 10
                            "medium" -> 60 + (Math.random() * 1000).toInt() % 10
                            "hard" -> 90 + (Math.random() * 1000).toInt() % 10
                            "square" -> 110 + (Math.random() * 1000).toInt() % 10
                            "tutorial" -> 7 + (Math.random() * 1000).toInt() % 3
                            else -> 0
                        },
                        when(args[0]){
                            "easy" -> 10
                            "medium" -> 20
                            "hard" -> 30
                            "square" -> 25
                            "tutorial" -> 4
                            else -> 0
                        },
                        when(args[0]){
                            "easy" -> 8 + (Math.random() * 1000).toInt() % 3
                            "medium" -> 16 + (Math.random() * 1000).toInt() % 3
                            "hard" -> 23 + (Math.random() * 1000).toInt() % 3
                            "square" -> 15 + (Math.random() * 1000).toInt() % 3
                            "tutorial" -> 3 + (Math.random() * 1000).toInt() % 2
                            else -> 0
                        },
                        when(args[0]){
                            "easy" -> 70.0
                            "medium" -> 50.0
                            "hard" -> 30.0
                            "square" -> 40.0
                            "tutorial" -> 90.0
                            else -> 0.0
                        }, { a, b, c, d -> Game.randomCreatingPathKeepingDistance(a,b,c,d)}).apply newValue@ { Game.GameSessions[client.sessionId] =
                        this@newValue }.toString())
                })
            Commands += listOf("endGame") to listOf(
                { client, args ->
                    Game.GameSessions.remove(client.sessionId)
                }
            )
            Commands += listOf("canvasClicked") to listOf (
                {client, args ->
                    Game.GameSessions[client.sessionId]?.run ClientGraph@{
                        (args[0].toDouble() to args[1].toDouble()).run Coords@{
                            class NodeFoundedException : Throwable {
                                public var node: GraphNode? = null

                                constructor(node: GraphNode) {
                                    this.node = node
                                }
                            }
                            try {
                                this@ClientGraph.nodes.forEach {
                                    if (it distance GraphNode(
                                            this.first,
                                            this.second,
                                            0.0
                                        ) <= it.radius
                                    ) throw NodeFoundedException(it)
                                }
                            } catch (e: NodeFoundedException) {
                                while(this@ClientGraph.bIsMoveing.get()) {Thread.sleep(10)}
                                bIsMoveing.set(true)
                                e.node!!.isActivated = !e.node!!.isActivated
                                e.node!!.radius =
                                    if (e.node!!.isActivated) this@ClientGraph.selectedRadius else this@ClientGraph.normalRadius
                                if ((e.node!!.isActivated) && this@ClientGraph.selectedNode != null) {
                                    val aX = e.node!!.x
                                    val aY = e.node!!.y
                                    val bX = this@ClientGraph.selectedNode!!.x
                                    val bY = this@ClientGraph.selectedNode!!.y
                                    (0..50).map { it.toDouble() }.map { 50 - 50 * Math.cos((it / 50) * Math.PI) }
                                        .forEachIndexed { index, percent ->
                                            e.node!!.x = aX + (bX - aX) * percent / 100
                                            e.node!!.y = aY + (bY - aY) * percent / 100
                                            this@ClientGraph.selectedNode!!.x = bX + (aX - bX) * percent / 100
                                            this@ClientGraph.selectedNode!!.y = bY + (aY - bY) * percent / 100
                                            client.send(this@ClientGraph.toString())
                                            Thread.sleep(10)
                                        }
                                    e.node!!.x = bX.also { this@ClientGraph.selectedNode!!.x = aX }
                                    e.node!!.y = bY.also { this@ClientGraph.selectedNode!!.y = aY }


                                    e.node!!.isActivated = false
                                    this@ClientGraph.selectedNode!!.isActivated = false
                                    this@ClientGraph.selectedNode = null

                                    if (this@ClientGraph.checkForVictory()) {
                                        client.send("Victory")
                                        Game.UserBoundry[client.sessionId]?.let { bindedUser ->
                                            Game.GameSessions[client.sessionId]!!.let { gameGraph ->
                                                if (!gameGraph.bExtractedVictory) {
                                                    gameGraph.bExtractedVictory =
                                                        true; bindedUser.score += Game.GameSessions[client.sessionId]!!.scoreForWin; Database.UpdateOnDb(
                                                        bindedUser
                                                    )
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    if (e.node!!.isActivated) this@ClientGraph.selectedNode =
                                        e.node!! else this@ClientGraph.selectedNode = null
                                }
                                bIsMoveing.set(false)
                                client.send(this@ClientGraph.toString())
                            }
                        }
                    }
                })

            Javalin.create().apply {
                this.ws("/", fun(server) {
                    server.onConnect {
                        Clients += it.sessionId
                        println("Socket connected")
                    }
                    server.onClose {
                        Game.GameSessions.entries.removeIf { it1-> (it.sessionId == it1.key) }
                        Game.UserBoundry.entries.removeIf { it1-> (it.sessionId == it1.key) }
                        Clients -= it.sessionId
                        println("Socket closed")
                    }
                    server.onBinaryMessage{
                        println("transmission bin")
                    }
                    server.onMessage { sockClient ->
                        println("transmission received ${sockClient.message()}")

                        thread {
                            try {
                                sockClient.message().split(" ").run { this[0] to this.slice(1 until this.size) }.run {
                                    (this@Companion.Commands.entries.find { this.first in it.key }?.value
                                        ?: listOf<(WsMessageContext, List<String>) -> Unit>()).forEach { it1 ->
                                        it1.invoke(
                                            sockClient,
                                            this.second
                                        )
                                    }
                                }
                            } catch (e: Exception) {
                                println(e.message)
                            }
                        }
                    }
                })

            }//.start(7700)
                .start(8080)
        }
    }

}



data class User(var name: String, var password: String, var score: Int, var status: String)
{
    public constructor(Name: String, Password: String = "") : this(Name, Password, 0, "newbie")
}

class Database
{
    companion object Companion
    {
        var conn: Connection? = null
        public fun CreateDb()
        {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance()
                conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/unraveling_users_db", "root", "your_password")
                println("Success?")
            }
            catch (ex: SQLException) {
                // handle any errors
                println("1 ex: ${ex.message}")
            }
            catch (ex: Exception) {
                // handle any errors
                println("2 ex: ${ex.message}")
            }
            finally {
                println("conn is ${conn?.let{"alive"} ?: "null"}")
            }
        }
        public fun UpdateOnDb(user: User) : Unit
        {
            conn!!.createStatement().execute("UPDATE unraveling_users_db.users\n" +
                    "SET name=\"${user.name}\", password=\"${user.password}\", score=\"${user.score}\", status=\"${user.status}\"\n" +
                    "WHERE name=\"${user.name}\"");
        }

        public fun AddToDb(user: User) : Unit
        {
            conn!!.createStatement().execute("".toContainer().apply{this.obj =
                "INSERT INTO unraveling_users_db.users (name, password, score, status)\n" +
                        "VALUES(\"${user.name}\", \"${user.password}\", ${user.score}, \"${user.status}\")"
            }.obj)
        }
        public fun GetFromDb(user: User) : User?
        {
            return conn!!.createStatement().executeQuery("SELECT * FROM unraveling_users_db.users WHERE name = \"${user.name}\"").run{if (this.next()) User(this.getString("name"), this.getString("password"), this.getInt("score"), this.getString("status")) else null}
        }

    }
}

fun main(args: Array<String>) {
    println("1")
    Database.CreateDb()
    println("2")
    Server.CreateServer()
    ///(mutableMapOf<Char, Int>() to 0.toContainer()).apply{ Scanner(System.`in`).nextLine().also{this@apply.second.obj = it.length}.run{this.forEach(fun (ch: Char) : Unit{this@apply.first.put(ch, 1)?.let{this@apply.first.set(ch, it + 1)}})} }.let{it.first.forEach{key, value -> println("$key => ${value.toDouble() / it.second.obj}")}}
}