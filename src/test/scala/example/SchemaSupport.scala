package example

import java.io.File

import com.typesafe.scalalogging.LazyLogging
import org.apache.avro.Schema
import org.apache.avro.Schema.Parser
import parquet.avro.AvroSchemaConverter
import parquet.schema.MessageType

trait SchemaSupport extends LazyLogging {

  private val schemaConverter = new AvroSchemaConverter

  def loadSchema(schemaLocation: String): Schema = {
    val avroSchema = new Parser().parse(new File(schemaLocation))
    logger.debug(avroSchema.toString(true))

    avroSchema
  }

  def convertSchema(avroSchema: Schema): MessageType = {
    val parquetSchema = schemaConverter.convert(avroSchema)
    logger.debug(parquetSchema.toString)

    parquetSchema
  }

  def convertSchema(parquetSchema: MessageType): Schema = {
    val avroSchema = schemaConverter.convert(parquetSchema)
    logger.debug(avroSchema.toString(true))

    avroSchema
  }

}
