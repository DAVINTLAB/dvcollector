package xtra;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class LineCounter {
	public static void main(String[]args) throws IOException{
		File jaggi = new File("C:\\Users\\eduardo\\Desktop\\JM\\sigh2.txt");

		BufferedReader readerJ = new BufferedReader(new FileReader(jaggi));

		String currentLine;
		int countJ = 0;
		String subs = "ã��";
		
		while((currentLine = readerJ.readLine()) != null) { //enquanto houver mais uma linha em Jaggi
		    if(currentLine.contains(subs)){countJ++;}
		} readerJ.close();
		
		System.out.print("Jaggi: " + countJ );
		
	}
}
