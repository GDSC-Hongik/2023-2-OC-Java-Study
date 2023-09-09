package com.mangkyu.stream.Quiz1;

import com.opencsv.CSVReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Quiz1 {
    public static void main(String[] args) throws IOException {
        readCsvLines().stream()
                .map(line -> line[2].replaceAll("\\s", ""))
                .mapToInt(line->line.length()-line.replace("좋아","").length())

                .forEach(System.out::println);

    }

    // 1.1 각 취미를 선호하는 인원이 몇 명인지 계산하여라.
    public Map<String, Integer> quiz1() throws IOException {

        return readCsvLines().stream()
                .map(line -> line[1].replaceAll("\\s", ""))
                .flatMap(hobbies -> Arrays.stream(hobbies.split(":")))
                .collect(Collectors.toMap(
                        hobbies -> hobbies, prefix -> 1,
                        (oldValue, newValue) -> oldValue += newValue
                ));
    }

    // 1.2 각 취미를 선호하는 정씨 성을 갖는 인원이 몇 명인지 계산하여라.
    public Map<String, Integer> quiz2() throws IOException {

        return readCsvLines().stream()
                .filter(line -> line[0].startsWith("정"))
                .map(line -> line[1].replaceAll("\\s", ""))
                .flatMap(hobbies -> Arrays.stream(hobbies.split(":")))
                .collect(Collectors.toMap(
                        hobbies -> hobbies, prefix -> 1,
                        (oldValue, newValue) -> oldValue += newValue
                ));
    }

    // 1.3 소개 내용에 '좋아'가 몇번 등장하는지 계산하여라.
    public int quiz3() throws IOException {
        return readCsvLines().stream()
                .map(line -> line[2].replaceAll("\\s", ""))
                .mapToInt(line -> line.length() - line.replace("좋아", "").length())
                .sum()/2;

    }

    private static List<String[]> readCsvLines() throws IOException {
        CSVReader csvReader = new CSVReader(new FileReader(Quiz1.class.getResource("/user.csv").getFile()));
        csvReader.readNext();
        return csvReader.readAll();
    }

}
