package optional;

import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class StreamTest {
    @Test
    void stream() {
        //array stream
        String[] strArr = new String[]{"one", "two", "three", "four", "five"};
        Stream<String> stream = Arrays.stream(strArr);
        assertThat(stream.collect(Collectors.toList()), contains("one", "two", "three", "four", "five"));

        //part Stream
        Stream<String> partStream = Arrays.stream(strArr, 1, 2);
        assertThat(partStream.collect(Collectors.toList()), contains("two"));

        //empty Stream
        Stream<String> emptyStream = Stream.empty();
        assertThat(0L, equalTo(emptyStream.count()));

        //collection Stream
        List<String> list = Arrays.asList("one", "two", "three", "four", "five");
        Stream<String> listStream = list.stream();
        assertThat(listStream.collect(Collectors.toList()), contains("one", "two", "three", "four", "five"));

        //parallel Stream
        Stream<String> parralelStream = list.parallelStream();
        boolean isParallel = parralelStream.isParallel();
        assertThat(isParallel, equalTo(true));
        IntStream intParallelStream = IntStream.range(1, 10).parallel();
        //병렬 처리로 인해 순서대로 생성되지 않음
        List<Integer> collect = intParallelStream.boxed().collect(Collectors.toList());
        System.out.println("parallel " + collect);
        assertThat(collect, containsInAnyOrder(1, 2, 3, 4, 5, 6, 7, 8, 9));

        //parallel Stream re creation
        intParallelStream = IntStream.range(1, 10).parallel();

        //sequential stream change
        intParallelStream = intParallelStream.sequential();
        //시퀀셜 처리로 인해 순서대로 숫자생성됨.
        List<Integer> sequentialList = intParallelStream.boxed().collect(Collectors.toList());
        System.out.println("sequentialList = " + sequentialList);
        assertThat(sequentialList, contains(1, 2, 3, 4, 5, 6, 7, 8, 9));

        //builder patern stream creation
        Stream<String> builderStream = Stream.<String>builder().add("one").add("two").add("three").build();
        assertThat(builderStream.collect(Collectors.toList()), contains("one", "two", "three"));

        //stream generate - 무한으로 생성되므로 limit이 필요
        Stream<String> generateStream = Stream.generate(() -> "gen").limit(5);
        assertThat(generateStream.collect(Collectors.toList()), contains("gen", "gen", "gen", "gen", "gen"));

        //stream iterate - 무한으로 생성되므로 limit이 필요
        Stream<Integer> iterateStream = Stream.iterate(10, n -> n + 2).limit(5);
        assertThat(iterateStream.collect(Collectors.toList()), contains(10, 12, 14, 16, 18));

        //basic type Stream
        IntStream intStream = IntStream.range(1, 5); // 1~4
        assertThat(intStream.boxed().collect(Collectors.toList()), contains(1, 2, 3, 4));

        LongStream longStream = LongStream.rangeClosed(1, 5); // 1~5
        assertThat(longStream.boxed().collect(Collectors.toList()), contains(1L, 2L, 3L, 4L, 5L));

        DoubleStream doubleStream = new Random().doubles(10);
        assertThat(doubleStream.count(), equalTo(10L));

        //Boxing : IntStream -> Stream
        Stream<Integer> boxedIntStream = IntStream.range(1, 5).boxed();
        assertThat(boxedIntStream.collect(Collectors.toList()), contains(1, 2, 3, 4));

        //Char Stream
        IntStream charsStream = "Stream".chars();
        assertThat(charsStream.boxed().collect(Collectors.toList()), contains(83, 116, 114, 101, 97, 109));

        // RegEx - 문자열을 정규표현식을 적용하여 스트림으로 변환
        Stream<String> stringStream = Pattern.compile(", ").splitAsStream("one, two, three");
        assertThat(stringStream.collect(Collectors.toList()), contains("one", "two", "three"));

        //File -> Stream
        try {
            Stream<String> lineStream = Files.lines(Paths.get("file.txt"), Charset.forName("UTF-8"));
            System.out.println(lineStream.collect(Collectors.toList()));
            assertThat(lineStream.collect(Collectors.toList()), contains("test"));
        } catch (Exception e) {

        }

        // two Stream merge
        List<String> list1 = Stream.of("one", "two", "three").collect(Collectors.toList());
        List<String> list2 = Stream.of("four", "five", "six").collect(Collectors.toList());
        Stream<String> concatStream = Stream.concat(list1.stream(), list2.stream());
        assertThat(concatStream.collect(Collectors.toList()), contains("one", "two", "three", "four", "five", "six"));

        //filtering
        concatStream = Stream.concat(list1.stream(), list2.stream());
        Stream<String> filterStream = concatStream.filter(num -> num.contains("three"));
        assertThat(filterStream.collect(Collectors.toList()), contains("three"));

        //mapping - 특정값으로 변환 (map)
        concatStream = Stream.concat(list1.stream(), list2.stream());
        Stream<String> upperStream = concatStream.map(String::toUpperCase);
        assertThat(upperStream.collect(Collectors.toList()), contains("ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX"));

        // 중첩된 리스트를 단일 리스트로 변환 - flatMap
        List<List<String>> overlapList = Arrays.asList(Arrays.asList("one", "two"), Arrays.asList("three", "four"));
        Stream<String> flatStream = overlapList.stream().flatMap(Collection::stream);
        assertThat(flatStream.collect(Collectors.toList()), contains("one", "two", "three", "four"));

        // sort
        List<Integer> sortedList = IntStream.of(14, 11, 20, 39, 23)
                .sorted()
                .boxed()
                .collect(Collectors.toList());
        assertThat(sortedList, contains(11, 14, 20, 23, 39));

        // sort to list
        List<String> lang = Arrays.asList("Java", "Scala", "Groovy", "Python");

        List<String> sortedLists = lang.stream().sorted().collect(Collectors.toList());
        assertThat(sortedLists, contains("Groovy", "Java", "Python", "Scala"));

        // sort reverse to list
        List<String> sortReverseList = lang.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        assertThat(sortReverseList, contains("Scala", "Python", "Java", "Groovy"));

        // sort data length to list
        List<String> sortedSizeLists = lang.stream().sorted(Comparator.comparingInt(String::length)).collect(Collectors.toList());
        assertThat(sortedSizeLists, contains("Java", "Scala", "Groovy", "Python"));

        // sort data length reverse to list
        List<String> sortedSizeReverseLists = lang.stream().sorted((s1, s2) -> s2.length() - s1.length()).collect(Collectors.toList());
        assertThat(sortedSizeReverseLists, contains("Groovy", "Python", "Scala", "Java"));

        // peek - 연산 중간에 데이터 확인
        int sum = IntStream.of(1, 3, 5, 7, 9).peek(System.out::println).sum();
        assertThat(sum, equalTo(25));

        // Calculating - 데이터 건수, 합계 반환
        assertThat(5L, equalTo(IntStream.of(1, 3, 5, 7, 9).count()));
        assertThat(25, equalTo(IntStream.of(1, 3, 5, 7, 9).sum()));

        // 최소값 계산
        OptionalInt min = IntStream.of(1, 3, 5, 7, 9).min();
        assertThat(1, equalTo(min.getAsInt()));

        // 최대값 계산
        OptionalInt max = IntStream.of(1, 3, 5, 7, 9).max();
        assertThat(9, equalTo(max.getAsInt()));

        // 평균 계산
        OptionalDouble average = DoubleStream.of(1.1, 2.2, 3.3, 4.4, 5.5).average();
        assertThat(3.3, equalTo(average.getAsDouble()));

        // reduce - 컬렉션의 값 하나 하나를 꺼내 연산
        OptionalInt reduced = IntStream.range(1, 4).reduce(
                (a, b) -> {
                    return Integer.sum(a, b);
                });
        assertThat(6, equalTo(reduced.getAsInt()));

        // reduce - 위 연산과 동일하나 초기값 지정하여 연산.
        int reduceTwoParam = IntStream.range(1, 4).reduce(10, Integer::sum);
        // 1, 2, 3, 10 sum
        assertThat(16, equalTo(reduceTwoParam));

        Integer reducedParallel = Arrays.asList(1, 2, 3, 4).parallelStream()
                .reduce(10, Integer::sum, (a, b) -> {
                    return a + b;
                });
        assertThat(50, equalTo(reducedParallel.intValue()));

    }

    public static class Product {
        private int amount;
        private String name;

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAmount() {
            return amount;
        }

        public String getName() {
            return name;
        }

        public Product(int amount, String name) {
            this.amount = amount;
            this.name = name;
        }
    }



}
