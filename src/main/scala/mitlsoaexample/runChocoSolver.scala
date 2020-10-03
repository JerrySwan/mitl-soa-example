package mitlsoaexample

///////////////////////////////////

object runChocoSolver { 
  
  private def parseSolution(lines: List[String]): Option[String] = {  
    val startIndex = lines.indexWhere { _.startsWith("v <instantiation>") }
    val endIndex = lines.indexWhere { _.startsWith("v </instantiation>") }
    if( startIndex == -1 || endIndex == -1 )
      None
    else 
      Some( lines.slice( startIndex, endIndex + 1 ).map { _.replaceFirst("^v ", "" ) }.mkString( "\n" ) )
  }
  
  /////////////////////////////////
  
  private def parseSolutionValue(lines: List[String]): Option[Double] = {
    val successConditions = List( "s SATISFIABLE", "s UNSATISFIABLE", "s OPTIMUM FOUND" )
    val index = lines.indexWhere { s => successConditions.contains( s ) }
    
    if( index == -1 ) {
      None
    } else {
      if( lines( index - 1).split(" ")(0) == "o" ) { // objective value
        Some( lines( index - 1).split(" ")(1).toDouble )
      }
      else { 
        lines( index ) match { 
          case "s SATISFIABLE" => Some( 1.0 )
          case "s UNSATISFIABLE" => Some( 0.0 )
          case _ => Some( Double.NaN ) // unrecognised
        }
      }
    }
  }
  
  /////////////////////////////////
  
  def apply(xcspFile: java.io.File, timeoutInSeconds: Int): Option[String] = { 

    val root = System.getProperty("user.dir")
    val jarFilePath = s"${root}/resources/bin"
    val jarFile = s"${jarFilePath}/choco-parsers-4.10.4-jar-with-dependencies.jar"

    ///////////////////////////////    

    val hours: Int = timeoutInSeconds / 3600
    val minutes: Int = timeoutInSeconds / 60
    val seconds: Int = timeoutInSeconds % 60
    
    val cmd = s"""java -cp "${jarFile}" org.chocosolver.parser.xcsp.ChocoXCSP -limit ${hours}h${minutes}m${seconds}s "${xcspFile}" """

    val lines = scala.sys.process.Process( cmd ).lines_!.toList
    parseSolution(lines)    
  }
}

// End ///////////////////////////////////////////////////////////////
