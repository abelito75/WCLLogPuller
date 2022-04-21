package com.abesoft.wcl.MassPullLogs.output;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import com.abesoft.wcl.MassPullLogs.data.Field;
import com.abesoft.wcl.MassPullLogs.data.LogData;

public class CSVOutput implements AutoCloseable {

	private boolean headerWritten;

	private CSVPrinter csvPrinter;

	public CSVOutput(File file) throws IOException {
		headerWritten = false;
		BufferedWriter writer = Files.newBufferedWriter(Paths.get(file.getAbsolutePath()));
		csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);
	}

	public void writeHeader(LogData unit) throws IOException {
		Map<String, Field> fieldMapping = unit.getData();
		List<Field> fields = fieldMapping.values().stream().filter(e -> e.isOutputField()).toList();
		csvPrinter.printRecord(fields.stream().map(field -> field.getOutputName()).toList());
		headerWritten = true;
	}

	public void writeRecord(LogData unit) throws IOException {
		Map<String, Field> fieldMapping = unit.getData();
		List<Field> fields = fieldMapping.values().stream().filter(e -> e.isOutputField()).toList();

		if (!headerWritten) {
			writeHeader(unit);
		}

		csvPrinter.printRecord(fields.stream().map(field -> {
			Object value = field.getValue();
			if (value.getClass().isArray()) {
				return Arrays.toString((Object[]) value);
			}
			return value.toString();
		}).toList());
		csvPrinter.flush();
	}

	@Override
	public void close() throws Exception {
		csvPrinter.flush();
		csvPrinter.close();
	}
}
