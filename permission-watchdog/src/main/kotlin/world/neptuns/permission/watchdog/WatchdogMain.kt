package world.neptuns.permission.watchdog

object WatchdogMain {

    @JvmStatic
    fun main(args: Array<String>) {
        val app = WatchdogApp()
        app.start()

        Runtime.getRuntime().addShutdownHook(Thread {
            app.stop()
        })
    }

}