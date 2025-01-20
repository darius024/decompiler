package wacc

import java.io.FileInputStream
import java.util.Properties
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import os.*

import parsley.{Parsley, Success, Failure}
import parser.parser

class FrontEndTests extends AnyFlatSpec {
    val props = new Properties()
    props.load(new FileInputStream("src/test/wacc/properties"))
    
    val examples = os.pwd / "src" / "test" / "wacc" / "examples"
    
    // Get enabled valid directories
    val validDirs = props.getProperty("valid.dirs").split(",").filter { dir =>
        props.getProperty(s"valid.$dir.enabled", "false").toBoolean
    }
    
    // Get enabled invalid directories
    val invalidDirs = props.getProperty("invalid.dirs").split(",").filter { dir =>
        props.getProperty(s"invalid.$dir.enabled", "false").toBoolean
    }
    
    // Generate paths for enabled valid directories
    val validFiles: Seq[Path] = validDirs.flatMap { dir =>
        os.walk.stream(examples / "valid" / dir).filter(os.isFile)
    }
    
    // Generate paths for enabled invalid directories
    val invalidFiles: Seq[Path] = invalidDirs.flatMap { dir =>
        os.walk.stream(examples / "invalid" / dir).filter(os.isFile)
    }

    "Front End" should "parse all valid WACC examples" in {
        for (file <- validFiles) {
            val waccFile: String = os.read(file)
            parser.parse(waccFile) match {
                case Success(_) => succeed(s"${file} : succeeded")
                case Failure(msg) => fail(s"${file} : failed\nwith message: ${msg}")
            }
        }
    }

    it should "not parse all invalid WACC examples" in {
        for (file <- invalidFiles) {
            val waccFile: String = os.read(file)
            parser.parse(waccFile) match {
                case Success(msg) => fail(s"${file} : failed\nwith message: ${msg}")
                case Failure(_) => succeed(s"${file} : succeeded")
            }
        }
    }
}