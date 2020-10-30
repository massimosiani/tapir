package codegen

import codegen.openapi.models.OpenapiModels.{
  OpenapiDocument,
  OpenapiParameter,
  OpenapiPath,
  OpenapiRequestBody,
  OpenapiResponse,
  OpenapiResponseContent
}
import codegen.openapi.models.OpenapiSchemaType
import codegen.openapi.models.OpenapiSchemaType.{
  OpenapiSchemaArray,
  OpenapiSchemaBoolean,
  OpenapiSchemaDouble,
  OpenapiSchemaFloat,
  OpenapiSchemaInt,
  OpenapiSchemaLong,
  OpenapiSchemaObject,
  OpenapiSchemaRef,
  OpenapiSchemaSimpleType,
  OpenapiSchemaString
}

object BasicGenerator {

  val classGenerator = new ClassDefinitionGenerator()
  val endpointGenerator = new EndpointGenerator()

  def generateObjects(doc: OpenapiDocument) = {
    s"""|
        |$packageStr
        |
        |object $objName {
        |
        |${indent(2)(imports)}
        |
        |${indent(2)(classGenerator.classDefs(doc))}
        |
        |${indent(2)(endpointGenerator.endpointDefs(doc))}
        |
        |}
        |""".stripMargin
  }

  private[codegen] def packageStr: String = "package sttp.tapir.generated"

  private[codegen] def objName = "TapirGeneratedEndpoints"

  private[codegen] def imports: String =
    """import sttp.tapir._
      |import sttp.tapir.json.circe._
      |import io.circe.generic.auto._
      |""".stripMargin

  def indent(i: Int)(str: String): String = {
    str.linesIterator.map(" " * i + _ + "\n").mkString
  }

  def mapSchemaSimpleTypeToType(osst: OpenapiSchemaSimpleType): (String, Boolean) = {
    osst match {
      case OpenapiSchemaDouble(nb) =>
        ("Double", nb)
      case OpenapiSchemaFloat(nb) =>
        ("Float", nb)
      case OpenapiSchemaInt(nb) =>
        ("Int", nb)
      case OpenapiSchemaLong(nb) =>
        ("Long", nb)
      case OpenapiSchemaString(nb) =>
        ("String", nb)
      case OpenapiSchemaBoolean(nb) =>
        ("Boolean", nb)
      case OpenapiSchemaRef(t) =>
        (t.split('/').last, false)
      case _ => throw new NotImplementedError("Not all simple types supported!")
    }
  }
}
