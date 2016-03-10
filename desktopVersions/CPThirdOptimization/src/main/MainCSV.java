package main;

import java.io.FileWriter;
import java.io.IOException;

import com.opencsv.CSVWriter;

public class MainCSV {

	public static void main(String[] args) {

		CSVWriter writer = null;
		try {
			writer = new CSVWriter(new FileWriter("yourfile.csv"), ',');
			// feed in your array (or convert your data to an array)
			String[] entries = "first#second#third".split("#");
			writer.writeNext(entries);
			entries = "pippo#ciccio#ciccia".split("#");
			writer.writeNext(entries);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
