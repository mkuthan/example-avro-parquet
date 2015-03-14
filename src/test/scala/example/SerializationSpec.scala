package example

import java.nio.ByteBuffer

import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericData.Record
import org.scalatest._
import org.scalatest.prop.TableDrivenPropertyChecks

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

class SerializationSpec extends FlatSpec with GivenWhenThen with Matchers with TableDrivenPropertyChecks with SchemaSupport with SerializationSupport {

  val simpleTypesSchema = loadSchema("src/test/resources/avro/simple_types.avsc")

  val simpleTypesRecord = new Record(simpleTypesSchema)
  simpleTypesRecord.put("boolean_field", true)
  simpleTypesRecord.put("int_field", 1234)
  simpleTypesRecord.put("long_field", 1234L)
  simpleTypesRecord.put("float_field", 12.34f)
  simpleTypesRecord.put("double_field", 12.34d)
  simpleTypesRecord.put("bytes_field", ByteBuffer.wrap("any bytes".getBytes))
  simpleTypesRecord.put("string_field", "any string value")

  val arraysSchema = loadSchema("src/test/resources/avro/arrays.avsc")

  val arraysRecord = new Record(arraysSchema)
  arraysRecord.put("field1", Seq().asJavaCollection)
  arraysRecord.put("field2", Seq("single value").asJavaCollection)
  arraysRecord.put("field3", Seq("multiple values", "multiple values", "multiple values").asJavaCollection)

  val mapsSchema = loadSchema("src/test/resources/avro/maps.avsc")

  val mapsRecord = new Record(arraysSchema)
  mapsRecord.put("field1", mapAsJavaMap(Map()))
  mapsRecord.put("field2", mapAsJavaMap(Map("key" -> "single value")))
  mapsRecord.put("field3", mapAsJavaMap(Map("key1" -> "multiple values", "key2" -> "multiple values", "key3" -> "multiple values")))

  val enumsSchema = loadSchema("src/test/resources/avro/enums.avsc")

  val enumsRecord = new Record(enumsSchema)
  enumsRecord.put("field1", "A")

  val recordsSchema = loadSchema("src/test/resources/avro/records.avsc")

  val recordsRecord = new Record(arraysSchema)
  val recordsField1Record = new GenericData.Record(recordsSchema.getField("field1").schema())
  recordsField1Record.put("subfield1", "any string value")
  recordsField1Record.put("subfield2", "any string value")
  recordsRecord.put("field1", recordsField1Record)

  val optionalValuesSchema = loadSchema("src/test/resources/avro/optional_values.avsc")

  val optionalValuesRecord = new Record(optionalValuesSchema)
  optionalValuesRecord.put("field1", null)
  optionalValuesRecord.put("field2", "any string value")

  val defaultValuesSchema = loadSchema("src/test/resources/avro/default_values.avsc")

  val defaultValuesRecord = new Record(defaultValuesSchema)
  defaultValuesRecord.put("field1", null)
  defaultValuesRecord.put("field2", "any string value")

  val defaultValuesExpectedRecord = new Record(defaultValuesSchema)
  defaultValuesRecord.put("field1", "any default value")
  defaultValuesRecord.put("field2", "any string value")

  val samples = Table(
    ("schema", "inRecord", "expectedRecord"),
    (simpleTypesSchema, simpleTypesRecord, simpleTypesRecord),
    (arraysSchema, arraysRecord, arraysRecord),
    //(mapsSchema, mapsRecord, mapsRecord),
    (enumsSchema, enumsRecord, enumsRecord),
    //(recordsSchema, recordsRecord, recordsRecord),
    (optionalValuesSchema, optionalValuesRecord, optionalValuesRecord)
    //(defaultValuesSchema, defaultValuesRecord, defaultValuesExpectedRecord)
  )

  "Generic record" should "be saved as Parquet" in {
    forAll(samples) { (schema, inRrecord, expectedRecord) =>
      Given(s"Record compatible with schema: ${schema.getName}")

      When("Serialize record as Parquet")
      serialize(schema, inRrecord)

      Then("Deserialized records should be the same")
      deserialize { recordOut =>
        recordOut should equal(expectedRecord)
      }

    }
  }

}
