package example

import org.scalatest._
import org.scalatest.prop.TableDrivenPropertyChecks

class SchemaSpec extends FlatSpec with GivenWhenThen with Matchers with TableDrivenPropertyChecks with SchemaSupport {

  val samples = Table(
    "schemaLocation",
    "src/test/resources/avro/simple_types.avsc",
    "src/test/resources/avro/arrays.avsc",
    //"src/test/resources/avro/maps.avsc",
    //"src/test/resources/avro/enums.avsc",
    //"src/test/resources/avro/records.avsc",
    "src/test/resources/avro/optional_values.avsc"
    //"src/test/resources/avro/default_values.avsc"
  )

  "Avro schema" should "be symmetric" in {
    forAll(samples) { schemaLocation =>

      Given(s"Schema: $schemaLocation")
      val schema = loadSchema(schemaLocation)

      When("Schema is converted into Parquet and back into Avro")
      val symmetricSchema = convertSchema(convertSchema(schema))

      Then("Both schemas should be the same")
      schema should equal(symmetricSchema)

    }
  }

}
