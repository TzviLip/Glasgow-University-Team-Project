// @GENERATOR:play-routes-compiler
// @SOURCE:C:/Users/tzvil/Desktop/University of Glasgow/Team Project/Part 2/ITSD-DT2021-Template/conf/routes
// @DATE:Sun Jul 11 18:29:02 BST 2021


package router {
  object RoutesPrefix {
    private var _prefix: String = "/"
    def setPrefix(p: String): Unit = {
      _prefix = p
    }
    def prefix: String = _prefix
    val byNamePrefix: Function0[String] = { () => prefix }
  }
}
