package com.mangkyu.stream.Quiz1;

import com.opencsv.CSVReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.*;

public class Quiz1 {

    // 1.1 각 취미를 선호하는 인원이 몇 명인지 계산하여라.
    public Map<String, Integer> quiz1() throws IOException {
        List<String[]> csvLines = readCsvLines();

        return csvLines.stream()
                .map(line -> line[1].split(":"))
                .flatMap(Arrays::stream)
                .map(s -> s.replace(" ", ""))
                .collect(groupingBy(s -> s, collectingAndThen(counting(), Long::intValue)));
    }

    // 1.2 각 취미를 선호하는 정씨 성을 갖는 인원이 몇 명인지 계산하여라.
    public Map<String, Integer> quiz2() throws IOException {
        List<String[]> csvLines = readCsvLines();

        return csvLines.stream()
                .filter(line -> line[0].startsWith("정"))
                .map(line -> line[1].split(":"))
                .flatMap(Arrays::stream)
                .map(s -> s.replace(" ", ""))
                .collect(groupingBy(s -> s, collectingAndThen(counting(), Long::intValue)));
    }

    // 1.3 소개 내용에 '좋아'가 몇번 등장하는지 계산하여라.
    public int quiz3() throws IOException {
        List<String[]> csvLines = readCsvLines();

        return csvLines.stream()
                .map(line -> line[2])
                .mapToInt(line -> line.length() - line.replace("좋아", " ").length())
                .sum();
    }

    private List<String[]> readCsvLines() throws IOException {
        CSVReader csvReader = new CSVReader(new FileReader(Quiz1.class.getClass().getResource("/user.csv").getFile()));
        csvReader.readNext();
        return csvReader.readAll();
    }

}
