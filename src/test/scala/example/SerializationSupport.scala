package example

import java.io.File

import com.typesafe.scalalogging.LazyLogging
import org.apache.avro.Schema
import org.apache.avro.generic.GenericRecord
import org.apache.hadoop.fs.Path
import parquet.avro.{AvroParquetReader, AvroParquetWriter}

trait SerializationSupport extends LazyLogging {

  val parquetFile = new File("target/sample.parquet")

  def serialize(schema: Schema, records: GenericRecord*): Unit = {
    parquetFile.delete()

    val dataFileWriter = new AvroParquetWriter[GenericRecord](new Path(parquetFile.getPath), schema)
    records.foreach { record =>
      logger.debug(record.toString)
      dataFileWriter.write(record)
    }
    dataFileWriter.close()
  }

  def deserialize(handler: GenericRecord => Unit): Unit = {
    val dataFileReader = new AvroParquetReader[GenericRecord](new Path(parquetFile.getPath()))

    var recordOut: GenericRecord = null
    while ( {
      recordOut = dataFileReader.read();
      recordOut != null
    }) {
      logger.debug(recordOut.toString)
      handler(recordOut)
    }
  }

}
