package wacc.integration

import os.Path
import os.RelPath

// path to test enabling file
val disablingFilePath = os.pwd / "src" / "test" / "wacc" / "integration" / "frontend" / "disable.json"
val disables = ujson.read(os.read(disablingFilePath))

// check in JSON disabling config
def isDisabled(testSet: String, category: String): Boolean = disables(testSet).arr.map(_.str).contains(category)

// convenience wrapper to compile from file
def compile(p: Path) = wacc.compile(os.read(p))

def listCategories(p: Path) = os.list.stream(p)
    .filter(os.isDir)
    .map(_.baseName)

def listTests(p: Path) = os.walk.stream(p)
